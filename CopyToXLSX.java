package residual;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CopyToXLSX {
    
    XSSFWorkbook workBook = new XSSFWorkbook();
    int rowSize;

    public void write(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> input) throws FileNotFoundException, IOException {

        for (String sheetName : input.keySet()) {
            XSSFSheet sheet = workBook.createSheet(sheetName);
            int r = 0;
            for (String key : input.get(sheetName).keySet()) {
                if (r == 0) {
                    rowSize = input.get(sheetName).get(key).size();
                }
                XSSFRow row = sheet.createRow(r++);
                for (int c = 0; c < rowSize; c++) {
                    row.createCell(c).setCellValue((String) input.get(sheetName).get(key).get(c));
                }
            }
        }
    }

    public void writeout(String excelFileName) throws FileNotFoundException, IOException {
        
        FileOutputStream fileOut = new FileOutputStream(excelFileName);
        workBook.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }
}
