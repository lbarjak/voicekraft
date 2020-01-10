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

	// private final String kapottFile =
	// "FBT_teljes_arlista_2019_augusztus-reduced.xlsx";
	private final String kapottFile = "VK_Arlista.xlsx";
	// private final String kapottFile = "Hivatalos nagyker árlista kisker árakkal_2019_November-21.xlsx";
	private final String hangtechnikaFile = "hangzavar-xlsx-export-2019-12-10_19_03_39.xlsx";

	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> hangzavarMap = new LinkedHashMap<>();
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();

	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenter = new LinkedHashMap<>();
	private final ArrayList<String> toNetsoft = new ArrayList<>();

	private String firstSheetNameFromShoprenter;
	private LinkedHashMap<String, ArrayList<String>> firstSheetFromShoprenter;
	private String firstKeyFromFirstSheetOfShoprenter;

	private String sheetNameFromKapott;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private String firstKeyFromKapott;

	private Set<String> difference;
	private Set<String> residual;

	private int indexOfnettoArFromKapott;
	private int indexOfRaktarkeszletFromKapott;
	private int indexOfBeszerzesiArFromKapott;

	private ArrayList<ArrayList<String>> sheetNamesHangtechnikaInput;
	private ArrayList<ArrayList<String>> sheetNamesKapottInput;

	public static void main(String[] args) throws IOException, FileNotFoundException, OpenXML4JException {
		
		new Voicekraft().convert();//ha voicekraft
		//new Residual().main();
		
	}

	private void main() throws IOException, FileNotFoundException, OpenXML4JException {

		sheetNamesHangtechnikaInput = new FromXLSX().read(hangtechnikaFile, hangzavarMap);// ami megvan
		// System.out.println("seetNamesHangtechnikaInput: " +
		// seetNamesHangtechnikaInput.get(0) + "\n");
		// firstSheetNameFromShoprenter = hangzavarMap.keySet().toArray()[0].toString();
		firstSheetNameFromShoprenter = sheetNamesHangtechnikaInput.get(0).get(0);
		firstSheetFromShoprenter = hangzavarMap.get(firstSheetNameFromShoprenter);
		firstKeyFromFirstSheetOfShoprenter = firstSheetFromShoprenter.keySet().toArray()[0].toString();

		sheetNamesKapottInput = new FromXLSX().read(kapottFile, kapottMap);// amit kapunk
		// System.out.println("seetNamesKapottInput: " + seetNamesKapottInput.get(0) +
		// "\n");
		// sheetNameFromKapott = kapottMap.keySet().toArray()[0].toString();
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);
		firstKeyFromKapott = sheetFromKapott.keySet().toArray()[0].toString();

		indexOfnettoArFromKapott = sheetFromKapott.get(firstKeyFromKapott).indexOf("Nettó ár");
		indexOfRaktarkeszletFromKapott = sheetFromKapott.get(firstKeyFromKapott).indexOf("Raktárkészlet");
		indexOfBeszerzesiArFromKapott = sheetFromKapott.get(firstKeyFromKapott).indexOf("Beszerzési ár (Nettó)");

		residual();

//        toShoprenter();
//
//        CopyToXLSX copyToXLSXtoShoprenter = new CopyToXLSX();
//        copyToXLSXtoShoprenter.write(toShoprenter);
//        String time = new Dates().now();
//        copyToXLSXtoShoprenter.writeout("shoprenter_upload" + time + ".xlsx");

		toNetsoft();

		writeToFileCSV("netsoft_upload", toNetsoft);
	}

	private void residual() throws IOException {

		Set<String> kapottMapFirstSheetKeys = new HashSet<>(sheetFromKapott.keySet());
		Set<String> hangtechnikaMapFirstSheetKeys = new HashSet<>(firstSheetFromShoprenter.keySet());
		difference = new HashSet<>(kapottMapFirstSheetKeys);
		difference.removeAll(hangtechnikaMapFirstSheetKeys);
		// "ujak", difference
		residual = new HashSet<>(kapottMapFirstSheetKeys);
		residual.removeAll(difference);
		// "meglevok", residual
	}

//    private void toShoprenter() {
//
//        toShoprenter.put(firstSheetNameFromShoprenter, new LinkedHashMap<>());
//        LinkedHashMap<String, ArrayList<String>> firstSheetToShoprenter = toShoprenter.get(firstSheetNameFromShoprenter);
//        firstSheetToShoprenter.put(firstKeyFromFirstSheetOfShoprenter, new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));
//
//        LinkedHashMap<String, ArrayList<String>> columns = new LinkedHashMap<>();
//        toShoprenter.put("columns", columns);
//        toShoprenter.get("columns").put("sku", new ArrayList<>(Arrays.asList("sku", "stockStatusName")));
//        toShoprenter.get("columns").put("Cikkszám", new ArrayList<>(Arrays.asList("Cikkszám", "Nincs készleten állapot")));
//        String RaktarkeszletFromKapott;
//
//        for (String key : residual) {
//            if (!key.equals(firstKeyFromKapott)) {
//                RaktarkeszletFromKapott = sheetFromKapott.get(key).get(indexOfRaktarkeszletFromKapott).
//                        replace("van", "Szerdára").replace("nincs", "Jelenleg nem érhető el!");
//                firstSheetToShoprenter.put(key, new ArrayList<>(Arrays.asList(key, RaktarkeszletFromKapott)));
//            }
//        }
//    }

//    private void toNetsoft() {
//
//        toNetsoft.add("Termék kód;Nettó eladási egységár");
//        for (String key : residual) {
//            if (!key.equals(firstKeyFromKapott)) {
//                toNetsoft.add(key + ";" + 
//                        round(Double.parseDouble(sheetFromKapott.get(key).get(indexOfnettoArFromKapott))));
//            }
//        }
//    }

	private void toNetsoft() {

		toNetsoft.add("Termék kód;Nettó eladási egységár;Beszerzési ár (Nettó)");
		for (String key : residual) {
			if (!key.equals(firstKeyFromKapott)) {
				toNetsoft.add(key + ";"
						+ round(Double.parseDouble(sheetFromKapott.get(key).get(indexOfnettoArFromKapott))) + ";"
						+ round(Double.parseDouble(sheetFromKapott.get(key).get(indexOfBeszerzesiArFromKapott))));
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

	private void writeToFileCSV(String nameOfFile, ArrayList<String> toCSVFile) {

		String time = new Dates().now();
		FileWriter fw;
		try {
			fw = new FileWriter(nameOfFile + time + ".csv");
			for (String row : toCSVFile) {
				fw.write(row + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
