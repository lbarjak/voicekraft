package eu.barjak.voicekraft;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;

public class VoiceKraft {

    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> hangzavarMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> voicekraftMap = new LinkedHashMap<>();

    private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> toShoprenter = new LinkedHashMap<>();
    //private final LinkedHashMap<String, String> toNetsoft = new LinkedHashMap<>();
    private final ArrayList<String> toNetsoft = new ArrayList<>();

    private final String voicekraftFile = "VK_Arlista.xlsx";
    private final String hangtechnikaFile = "hangzavar-xlsx-export-2019-10-15_19_58_46.xlsx";

    String firstSheetNameFromShoprenter;
    LinkedHashMap<String, ArrayList<String>> firstSheetFromShoprenter;
    String firstKeyFromfirstSheetOfShoprenter;

    String sheetNameFromVoicekraft;
    LinkedHashMap<String, ArrayList<String>> sheetFromVoicekraft;
    String firstKeyFromVoicekraft;

    String sheetNameFromResidual;
    LinkedHashMap<String, ArrayList<String>> sheetFromResidual;
    String firstKeyFromResidual;

    Set<String> difference;
    Set<String> residual;

    private int indexOfnettoArFromVoicekraft;
    private int indexOfRaktarkeszletFromVoicekraft;

    public static void main(String[] args) throws IOException, FileNotFoundException, OpenXML4JException {

        new VoiceKraft().main();
    }

    private void main() throws IOException, FileNotFoundException, OpenXML4JException {

        new FromXLSX().read(hangtechnikaFile, hangzavarMap);//ami megvan
        firstSheetNameFromShoprenter = hangzavarMap.keySet().toArray()[0].toString();
        firstSheetFromShoprenter = hangzavarMap.get(firstSheetNameFromShoprenter);
        firstKeyFromfirstSheetOfShoprenter = firstSheetFromShoprenter.keySet().toArray()[0].toString();

        new FromXLSX().read(voicekraftFile, voicekraftMap);//amit kapunk
        sheetNameFromVoicekraft = voicekraftMap.keySet().toArray()[0].toString();
        sheetFromVoicekraft = voicekraftMap.get(sheetNameFromVoicekraft);
        firstKeyFromVoicekraft = sheetFromVoicekraft.keySet().toArray()[0].toString();
        indexOfnettoArFromVoicekraft = sheetFromVoicekraft.get(firstKeyFromVoicekraft).indexOf("Nettó ár");
        indexOfRaktarkeszletFromVoicekraft = sheetFromVoicekraft.get(firstKeyFromVoicekraft).indexOf("Raktárkészlet");

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

        Set<String> voicekraftMapFirstSheetKeys = new HashSet<>(sheetFromVoicekraft.keySet());
        Set<String> hangtechnikaMapFirstSheetKeys = new HashSet<>(firstSheetFromShoprenter.keySet());
        difference = new HashSet<>(voicekraftMapFirstSheetKeys);
        difference.removeAll(hangtechnikaMapFirstSheetKeys);
        //"ujak", difference
        residual = new HashSet<>(voicekraftMapFirstSheetKeys);
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
        String RaktarkeszletFromVoicekraft;
        
        for (String key : residual) {
            if (!key.equals(firstKeyFromVoicekraft)) {
                RaktarkeszletFromVoicekraft = sheetFromVoicekraft.get(key).get(indexOfRaktarkeszletFromVoicekraft).
                        replace("van", "Szerdára").replace("nincs", "Jelenleg nem érhető el!");
                firstSheetToShoprenter.put(key, new ArrayList<>(Arrays.asList(key, RaktarkeszletFromVoicekraft)));
            }
        }
    }

    private void toNetsoft() {

        toNetsoft.add("Termék kód;Nettó eladási egységár");

        for (String key : residual) {
            if (!key.equals(firstKeyFromVoicekraft)) {
                toNetsoft.add(key + ";" + sheetFromVoicekraft.get(key).get(indexOfnettoArFromVoicekraft));
            }
        }
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