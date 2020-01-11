package residual;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class VoicekraftFromXLSX {

	private static DecimalFormat df2 = new DecimalFormat("#.##");
	String sheetName;
	String firstCellOfFirstRow;
	ArrayList<ArrayList<String>> sheetNamesAndFirstElement = new ArrayList<>();

	public ArrayList<ArrayList<String>> read(String xlsxName,
			LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> output)
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
//				if (rowOfArrayList.get(0) != null && rowOfArrayList.get(0).matches("\\d{2,}.+")) {
//					// "Termék kód", "Nettó eladási egységár", "Beszerzési ár (Nettó)", "Termék
//					// típus", "Raktárkészlet"
//					String key = rowOfArrayList.get(0).replace(".0", "");
//					// output.get(sheetName).put(key, new ArrayList<>(rowOfArrayList));
//					rowOfArrayList.set(0, rowOfArrayList.get(0).replace(".0", ""));//Cikkszám
//					rowOfArrayList.remove(1);// - Kategória név/nevek
//					rowOfArrayList.remove(1);// - Terméknév (hu)
//					rowOfArrayList.remove(1);// - Bruttó ár
//					rowOfArrayList.add(2, df2.format(Double.parseDouble(rowOfArrayList.get(1)) * 0.75));
//					rowOfArrayList.add(3, "Termék");
//					output.get(sheetName).put(key, new ArrayList<>(rowOfArrayList));
//				}
				
				if (rowOfArrayList.get(0) != null) {
					output.get(sheetName).put(rowOfArrayList.get(0), rowOfArrayList);
				}
			}
		}
		myWorkBook.close();
		return sheetNamesAndFirstElement;
	}
}
