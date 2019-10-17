package residual;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class Residual {

    //private final String kapottFile = "Mipro_in+tipus.xlsx";
    private final String kapottFile = "VK_Arlista.xlsx";
    private final String hangtechnikaFile = "hangzavar-xlsx-export-2019-10-15_19_58_46.xlsx";

    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> hangzavarMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();

    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenter = new LinkedHashMap<>();
    private final ArrayList<String> toNetsoft = new ArrayList<>();

    private String firstSheetNameFromShoprenter;
    private LinkedHashMap<String, ArrayList<String>> firstSheetFromShoprenter;
    private String firstKeyFromfirstSheetOfShoprenter;

    private String sheetNameFromKapott;
    private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
    private String firstKeyFromKapott;

    private Set<String> difference;
    private Set<String> residual;

    private int indexOfnettoArFromKapott;
    private int indexOfRaktarkeszletFromKapott;

    private ArrayList<ArrayList<String>> seetNamesHangtechnikaInput;
    private ArrayList<ArrayList<String>> seetNamesKapottInput;

    public static void main(String[] args) throws IOException, FileNotFoundException, OpenXML4JException {

        new Residual().main();
    }

    private void main() throws IOException, FileNotFoundException, OpenXML4JException {

        seetNamesHangtechnikaInput = new FromXLSX().read(hangtechnikaFile, hangzavarMap);//ami megvan
        //System.out.println("seetNamesHangtechnikaInput: " + seetNamesHangtechnikaInput.get(0) + "\n");
        //firstSheetNameFromShoprenter = hangzavarMap.keySet().toArray()[0].toString();
        firstSheetNameFromShoprenter = seetNamesHangtechnikaInput.get(0).get(0);
        firstSheetFromShoprenter = hangzavarMap.get(firstSheetNameFromShoprenter);
        firstKeyFromfirstSheetOfShoprenter = firstSheetFromShoprenter.keySet().toArray()[0].toString();

        seetNamesKapottInput = new FromXLSX().read(kapottFile, kapottMap);//amit kapunk
        //System.out.println("seetNamesKapottInput: " + seetNamesKapottInput.get(0) + "\n");
        //sheetNameFromKapott = kapottMap.keySet().toArray()[0].toString();
        sheetNameFromKapott = seetNamesKapottInput.get(0).get(0);
        sheetFromKapott = kapottMap.get(sheetNameFromKapott);
        firstKeyFromKapott = sheetFromKapott.keySet().toArray()[0].toString();
        
        indexOfnettoArFromKapott = sheetFromKapott.get(firstKeyFromKapott).indexOf("Nettó ár");
        indexOfRaktarkeszletFromKapott = sheetFromKapott.get(firstKeyFromKapott).indexOf("Raktárkészlet");

        residual();

        toShoprenter();

        CopyToXLSX copyToXLSXtoShoprenter = new CopyToXLSX();
        copyToXLSXtoShoprenter.write(toShoprenter);
        String time = new Dates().now();
        copyToXLSXtoShoprenter.writeout("shoprenter_upload" + time + ".xlsx");

        toNetsoft();

        writeToFileCSV("netsoft_upload", toNetsoft);
    }

    private void residual() throws IOException {

        Set<String> kapottMapFirstSheetKeys = new HashSet<>(sheetFromKapott.keySet());
        Set<String> hangtechnikaMapFirstSheetKeys = new HashSet<>(firstSheetFromShoprenter.keySet());
        difference = new HashSet<>(kapottMapFirstSheetKeys);
        difference.removeAll(hangtechnikaMapFirstSheetKeys);
        //"ujak", difference
        residual = new HashSet<>(kapottMapFirstSheetKeys);
        residual.removeAll(difference);
        //"meglevok", residual
    }

    private void toShoprenter() {

        toShoprenter.put(firstSheetNameFromShoprenter, new LinkedHashMap<>());
        LinkedHashMap<String, ArrayList<String>> firstSheetToShoprenter = toShoprenter.get(firstSheetNameFromShoprenter);
        firstSheetToShoprenter.put(firstKeyFromfirstSheetOfShoprenter, new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));

        LinkedHashMap<String, ArrayList<String>> columns = new LinkedHashMap<>();
        toShoprenter.put("columns", columns);
        toShoprenter.get("columns").put("sku", new ArrayList<>(Arrays.asList("sku", "stockStatusName")));
        toShoprenter.get("columns").put("Cikkszám", new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));
        String RaktarkeszletFromKapott;

        for (String key : residual) {
            if (!key.equals(firstKeyFromKapott)) {
                RaktarkeszletFromKapott = sheetFromKapott.get(key).get(indexOfRaktarkeszletFromKapott).
                        replace("van", "Szerdára").replace("nincs", "Jelenleg nem érhető el!");
                firstSheetToShoprenter.put(key, new ArrayList<>(Arrays.asList(key, RaktarkeszletFromKapott)));
            }
        }
    }

    private void toNetsoft() {

        toNetsoft.add("Termék kód;Nettó eladási egységár");
        for (String key : residual) {
            if (!key.equals(firstKeyFromKapott)) {
                toNetsoft.add(key + ";" + 
                        round(Double.parseDouble(sheetFromKapott.get(key).get(indexOfnettoArFromKapott))));
            }
        }
    }

    public static String round(double number) {

        Locale locale = new Locale("hu", "HU");
        String pattern = ".##";
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        String formatted = decimalFormat.format(number);
        return formatted;
    }

    private void writeToFileCSV(String nameOfFile, ArrayList<String> toFile) {

        String time = new Dates().now();
        FileWriter fw;
        try {
            fw = new FileWriter(nameOfFile + time + ".csv");
            for (String row : toFile) {
                fw.write(row + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
