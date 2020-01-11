package residual;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

//bemenet: a Voicekraft file teljes neve
//kimenet: kapottFile

public class Voicekraft {

	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private ArrayList<ArrayList<String>> sheetNamesKapottInput;
	private String sheetNameFromKapott;
	private LinkedHashMap<String, ArrayList<String>> sheetFromKapott;
	private final String kapottFile = "bb11a3a45af20fe453cca9a783effd05_VK_Arlista.xlsx";
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> kapottMap = new LinkedHashMap<>();
	private final ArrayList<ArrayList<String>> out = new ArrayList<>();

	public void convert() throws FileNotFoundException, InvalidFormatException, IOException, OpenXML4JException {

		sheetNamesKapottInput = new VoicekraftFromXLSX().read(kapottFile, kapottMap);// amit kapunk
		sheetNameFromKapott = sheetNamesKapottInput.get(0).get(0);
		sheetFromKapott = kapottMap.get(sheetNameFromKapott);

		out.add(new ArrayList<String>(Arrays.asList("Termék kód", "Nettó eladási egységár", "Beszerzési ár (Nettó)",
				"Termék típus", "Raktárkészlet")));
		for (String key : sheetFromKapott.keySet()) {
			if (sheetFromKapott.get(key).get(0).matches("\\d{2,}.+")) {
				out.add(new ArrayList<String>(Arrays.asList(sheetFromKapott.get(key).get(0).replace(".0", ""), // Termék_kód
						sheetFromKapott.get(key).get(4), // Nettó eladási egységár
						df2.format(Double.parseDouble(sheetFromKapott.get(key).get(4)) * 0.75), // Beszerzési ár (Nettó)
						"Termék", // Termék típus
						sheetFromKapott.get(key).get(5) // Raktárkészlet
				)));
			}
		}

		voiceKraftToNetsoftArfrissites();
		voiceKraftToNetsoftArlista();
		voiceKraftToShoprenterKeszlet();
	}

	public void voiceKraftToNetsoftArfrissites() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toCSVFile.add(row.get(0) + ";" + row.get(1).replace(".", ","));
		}
		for (String row : toCSVFile) {
			System.out.println(row);
		}
	}

	public void voiceKraftToNetsoftArlista() {
		ArrayList<String> toCSVFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toCSVFile.add(row.get(0) //Termék kód
					+ ";" + row.get(1).replace(".", ",") // Nettó eladási egységár 
					+ ";" + row.get(2) + ";" // Beszerzési ár (Nettó)
					+ row.get(3)); // Termék típus
		}
		for (String row : toCSVFile) {
			System.out.println(row);
		}
	}

	public void voiceKraftToShoprenterKeszlet() {
		ArrayList<String> toXLSXFile = new ArrayList<>();
		for (ArrayList<String> row : out) {
			toXLSXFile.add(row.get(0) + ";"
					+ row.get(4).replace("van", "Szerdára").replace("nincs", "Jelenleg nem érhető el!"));
		}
		for (String row : toXLSXFile) {
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
