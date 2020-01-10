package residual;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

//bemenet: a Voicekraft file teljes neve
//kimenet: kapottFile

public class Voicekraft {

	private ArrayList<ArrayList<String>> sheetNamesKapottInput;
	private String sheetNameFromKapott;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private final String kapottFile = "bb11a3a45af20fe453cca9a783effd05_VK_Arlista.xlsx";
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();

	public void convert() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {

		sheetNamesKapottInput = new VoicekraftFromXLSX().read(kapottFile, kapottMap);// amit kapunk
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);

//		for (String key : sheetFromKapott.keySet()) {
//			System.out.println(key + " - " + sheetFromKapott.get(key));
//		}
		// voiceKraftToNetsoftArfrissites();
		//voiceKraftToNetsoftArlista();
		voiceKraftToShoprenterKeszlet();
	}

	public void voiceKraftToNetsoftArfrissites() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (String key : sheetFromKapott.keySet()) {
			toCSVFile.add(key + ";" + sheetFromKapott.get(key).get(1));
		}
		for (String row : toCSVFile) {
			System.out.println(row);
		}
	}

	public void voiceKraftToNetsoftArlista() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (String key : sheetFromKapott.keySet()) {
			toCSVFile.add(key + ";" + sheetFromKapott.get(key).get(1) + ";" + sheetFromKapott.get(key).get(2) + ";"
					+ sheetFromKapott.get(key).get(3));
		}
		for (String row : toCSVFile) {
			System.out.println(row);
		}
	}

	public void voiceKraftToShoprenterKeszlet() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (String key : sheetFromKapott.keySet()) {
			toCSVFile.add(key + ";" + sheetFromKapott.get(key).get(4).replace("van", "Szerdára").replace("nincs",
					"Jelenleg nem érhető el!"));
		}
		for (String row : toCSVFile) {
			System.out.println(row);
		}
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
