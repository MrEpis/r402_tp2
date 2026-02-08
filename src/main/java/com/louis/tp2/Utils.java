package com.louis.tp2;

import java.io.File;

public class Utils {

    public static String verifierDossiers(String langue) {
        String directoryPath = "langues/" + langue;
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return "Le dossier de cette langue n'existe pas";
        }
        File anagrammes = new File(directory, "anagrammes.json");
        if (!anagrammes.exists()) {
            return "Le fichier anagrammes.json pour cette langue n'existe pas";
        }
        return "";
    }
}
