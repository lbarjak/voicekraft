package eu.barjak.voicekraft;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FromXLSX {

    String sheetName;
    String firstCellOfFirstRow;
    ArrayList<ArrayList<String>> sheetNamesAndFirstElement = new ArrayList<>();

    public ArrayList<ArrayList<String>> read(String xlsxName, LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> output)
            throws FileNotFoundException, IOException, InvalidFormatException, OpenXML4JException {

        OPCPackage fis = OPCPackage.open(new FileInputStream(xlsxName));

        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        XSSFReader read = new XSSFReader(fis);
        Iterator<InputStream> sheetsIterator = read.getSheetsData();
        XSSFReader.SheetIterator sheet = (XSSFReader.SheetIterator) sheetsIterator;

        while (sheet.hasNext()) {
            sheet.next();
            sheetName = sheet.getSheetName();
            sheetNamesAndFirstElement.add(new ArrayList<>());
            sheetNamesAndFirstElement.add(new ArrayList<>());
            sheetNamesAndFirstElement.get(0).add(sheetName);
            System.out.println(xlsxName + " / " + sheetName);
            output.put(sheetName, new LinkedHashMap<>());
            XSSFSheet mySheet = myWorkBook.getSheet(sheetName);

            int numberOfColumns = mySheet.getRow(0).getPhysicalNumberOfCells();
            System.out.println("numberOfRows: " + mySheet.getPhysicalNumberOfRows());

            boolean firstRow = true;
            for (Row row : mySheet) {
                ArrayList<String> rowOfArrayList = new ArrayList<>(Collections.nCopies(numberOfColumns, null));

                for (int c = 0; c < numberOfColumns; c++) {
                    Cell cell = row.getCell(c);

                    if (!(cell == null)) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowOfArrayList.set(c, cell.getStringCellValue().trim());
                                break;
                            case NUMERIC:
                                rowOfArrayList.set(c, String.valueOf(cell.getNumericCellValue()));
                                break;
                            case BOOLEAN:
                                rowOfArrayList.set(c, String.valueOf(cell.getBooleanCellValue()));
                                break;
                            default:
                        }
                    }
                    if (firstRow == true && c == 0) {
                        System.out.println("firstCellOfFirstRow: " + rowOfArrayList.get(c) + "\n");
                        sheetNamesAndFirstElement.get(1).add(rowOfArrayList.get(c));
                        firstRow = false;
                    }
                }
                String key = rowOfArrayList.get(0).replace(".0", "");
                output.get(sheetName).put(key, new ArrayList<>(rowOfArrayList));
            }
        }
        return sheetNamesAndFirstElement;
    }
}
