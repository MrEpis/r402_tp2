package com.louis.tp2;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dico {

    private final Pattern pattern;
    HashMap<String, ArrayList<String>> dico ;

    public HashMap<String, ArrayList<String>> getDictionnary() {
        return dico;
    }

    public void setDictionnary(HashMap<String, ArrayList<String>> dico) {
        this.dico = dico;
    }

    public Dico() {
        dico = new HashMap<>();
        pattern = Pattern.compile("\\s|-|!|'|\\.|\\)");
    }

    /**
     * pour ajouter un mot au dictionnaire des anagrammes. Le mot est d'abord normalisé (accent, etc.), puis ses lettres sont rangées par ordre alphabétique
     * et finalement il est ajouté à la liste des mots correspondant à son anagramme "alphabétique"
     * @param line  le mot à ajouter
     * @return vrai si l'ajout est effectif
     */
    public boolean addWord(String line) {
        String word = line.trim();
        boolean result = filter(word);

        if (result) {
            word = normalize(word);
            String key = anagram(word);
            ArrayList<String> anagrams;
            if (dico.containsKey(key)) {
                anagrams = dico.get(key);
            } else {
                anagrams = new ArrayList<>();
                dico.put(key, anagrams);
            }

            result = ! anagrams.contains(line);

            if (result) {
                anagrams.add(line);
            }
        }


        return result;
    }



    /**
     * exclure les mots vides, les mots avec des espaces ou des tirêts
     * @param word
     * @return
     */
    private boolean filter(String word) {
        if ((word == null) || (word == "")) return false;

        Matcher matcher = pattern.matcher(word);
        return ! matcher.find();
    }

    /**
     * pour déterminer l'anagramme "alphabétique" (les lettres du mot sont ordonnées)
     * @param line le mot dont on veut connaitre l'anagramme
     * @return l'anagramme "alphabétique"
     */
    public String anagram(String line) {
        String word = line.strip();
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }


    /**
     * pour enlever et remplacer les lettres accentuées et les ligatures (sauf le w) par des lettres correspondantes
     * le mot est mis en minuscule
     * @param word à normaliser
     * @return le mot normalisé
     */
    public String normalize(String word) {
        String result =  word.toLowerCase();

        // les ligatures sont wꝡ
        result = result.replaceAll("ꜳ", "aa");
        result = result.replaceAll("ꜳ", "ae");
        result = result.replaceAll("ꜵ", "ao");
        result = result.replaceAll("ꜷ", "au");
        result = result.replaceAll("ꜹ", "av");
        result = result.replaceAll("ꜻ", "av");
        result = result.replaceAll("ꜽ", "ay");
        result = result.replaceAll("\uD83D\uDE70", "et");
        result = result.replaceAll("ﬀ", "ff");
        result = result.replaceAll("ﬃ", "ffi");
        result = result.replaceAll("ﬄ", "ffl");
        result = result.replaceAll("ﬁ", "fi");
        result = result.replaceAll("ﬂ", "fl");
        result = result.replaceAll("ƕ", "hv");
        result = result.replaceAll("℔", "lb");
        result = result.replaceAll("ỻ", "ll");
        result = result.replaceAll("œ", "oe");
        result = result.replaceAll("ꝏ", "oo");
        result = result.replaceAll("ꭢ", "ɔe");
        result = result.replaceAll("ß", "ſs");
        result = result.replaceAll("ﬆ", "st");
        result = result.replaceAll("ﬅ", "ft");
        result = result.replaceAll("ꜩ", "tz");
        result = result.replaceAll("ᵫ", "ue");
        result = result.replaceAll("ꭣ", "uo");
        result = result.replaceAll("ꝡ", "vy");

        result = Normalizer.normalize(result, Normalizer.Form.NFKD);
        result = result.replaceAll("\\p{M}", "");
        return result;
    }


}