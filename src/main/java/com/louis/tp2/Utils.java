package com.louis.tp2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.CombinatoricsUtils;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Utils {

    private final Dico dico;

    public Utils(Dico dico) {
        this.dico = dico;
    }

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

    public static Dico getDico(String langue, String verifLangue) throws IOException {
        if (verifLangue.isEmpty()) {
            File anagrammes = new File("langues/" + langue + "/anagrammes.json");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(anagrammes, Dico.class);
        }
        return null;
    }

    public HashSet<String> getAllAnagrams(String word) {
        HashSet<String> possibleAnagrams = new HashSet<>();
        String key = dico.normalize(word.trim());
        key = dico.anagram(key);

        ArrayList<int[]> subwordIndexes = new ArrayList<>();

        for(int i = 1; i <= key.length(); i++) {
            Iterator<int[]> list = CombinatoricsUtils.combinationsIterator(key.length(), i);
            while (list.hasNext()) {
                final int[] combination = list.next();
                subwordIndexes.add(combination);
            }
        }

        for (int[] indexes : subwordIndexes) {
            StringBuilder anagram = new StringBuilder();
            for (int index : indexes) anagram.append(key.charAt(index));

            ArrayList<String> adds = dico.getDictionnary().get(anagram.toString());
            if ((adds != null) && (!adds.isEmpty())) possibleAnagrams.addAll(adds);

        }

        return possibleAnagrams;
    }

    public static String getRandomWord(String[] keyArray, SecureRandom random, Dico dico) {
        String randomKey = keyArray[random.nextInt(keyArray.length)];
        ArrayList<String> mots = dico.getDictionnary().get(randomKey);
        return mots.get(random.nextInt(mots.size()));
    }
}
