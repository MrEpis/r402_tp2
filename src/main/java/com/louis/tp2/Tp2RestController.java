package com.louis.tp2;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.*;

@RestController
public class Tp2RestController {

    private static final SecureRandom RANDOM = new SecureRandom();

    @GetMapping("/ping")
    public String ping() {
        return "I'm alive";
    }

    @GetMapping("/langues")
    public ArrayList<String> langues() {
        ArrayList<String> liste = new ArrayList<>();
        File langues = new File("langues");
        if (langues.exists() && langues.isDirectory()) {
            File [] dossiers = langues.listFiles();
            if (dossiers != null) {
                for (File d : dossiers) {
                    if (d.isDirectory()) {
                        File anagrammes = new File(d, "anagrammes.json");
                        if (anagrammes.exists()) {
                            liste.add(d.getName());
                        }
                    }
                }
            }

        }
        return liste;
    }

    @PostMapping("/ajouterLangue")
    public Mono<String> ajouterLangue(@RequestPart("fichierLangue") FilePart filePart, @RequestPart("langue") String langue) {
        String directoryPath = "langues/" + langue;
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Mono<InputStream> monoInputStream = filePart.content()
                .map(dataBuffer -> dataBuffer.asInputStream(true))
                .reduce(SequenceInputStream::new);

        return monoInputStream.flatMap(is -> {
            DicoTextToJSON importText = new DicoTextToJSON();
            try {
                importText.load(is);
                is.close();
            } catch (IOException e) {
                return Mono.error(e);
            }
            importText.export(directoryPath  + "/anagrammes.json");
            return Mono.just("le fichier a été converti en JSON");
        });
    }

    @GetMapping("{langue}/anagrammesStrict/{mot}")
    public ArrayList<String> anagrammesStrict(@PathVariable String langue, @PathVariable String mot) {
        ArrayList<String> liste = new ArrayList<>();
        String verifLangue = Utils.verifierDossiers(langue);
        try {
            Dico dico = Utils.getDico(langue, verifLangue);
            if (dico != null) {
                String normMot = dico.normalize(mot);
                String cle = dico.anagram(normMot);

                if (dico.getDictionnary().get(cle) != null) liste.addAll(dico.getDictionnary().get(cle));
                else liste.add("Ce mot n'existe pas");
            } else {
                liste.add(verifLangue);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return liste;
    }

    @GetMapping("{langue}/anagrammes/{mot}")
    public ArrayList<String> anagrammes(@PathVariable String langue, @PathVariable String mot) {
        ArrayList<String> liste = new ArrayList<>();
        String verifLangue = Utils.verifierDossiers(langue);
        try {
            Dico dico = Utils.getDico(langue, verifLangue);
            if (dico != null) {
                Utils utils = new Utils(dico);
                HashSet<String> set = utils.getAllAnagrams(mot);
                if (!set.isEmpty()) liste.addAll(set);
                else liste.add("Ce mot n'existe pas");
            } else {
                liste.add(verifLangue);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return liste;
    }


    @GetMapping("{langue}/anagrammes/{mot}/joker/{i:[1-2]}")
    public HashSet<String> anagrammesJoker(@PathVariable String langue, @PathVariable String mot, @PathVariable int i) {
        HashSet<String> liste = new HashSet<>();
        String verifLangue = Utils.verifierDossiers(langue);
        try {
            Dico dico = Utils.getDico(langue, verifLangue);
            if (dico != null) {
                Utils utils = new Utils(dico);
                for (char c1 = 'a'; c1 <= 'z'; c1++) {
                    if (i == 1) {
                        String newMot = mot + c1;
                        liste.addAll(utils.getAllAnagrams(newMot));
                    } else {
                        for (char c2 = 'a'; c2 <= 'z'; c2++) {
                            String newMot = mot + c1 + c2;
                            liste.addAll(utils.getAllAnagrams(newMot));
                        }
                    }
                }
            } else {
                liste.add(verifLangue);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return liste;
    }


    @GetMapping("{langue}/unmot")
    public String unMot(@PathVariable String langue) {
        String verifLangue = Utils.verifierDossiers(langue);
        String resultat;
        try {
            Dico dico = Utils.getDico(langue, verifLangue);
            if (dico != null) {
                Set<String> keys = dico.getDictionnary().keySet();
                String[] keyArray = keys.toArray(new String[0]);
                resultat = Utils.getRandomWord(keyArray, RANDOM, dico);
            } else resultat = verifLangue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultat;
    }

    @GetMapping("{langue}/unmot/longueur/{i}")
    public String unMotLongueurCustom(@PathVariable String langue, @PathVariable int i) {
        String verifLangue = Utils.verifierDossiers(langue);
        String resultat;
        try {
            Dico dico = Utils.getDico(langue, verifLangue);
            if (dico != null) {
                Set<String> keys = dico.getDictionnary().keySet();
                keys.removeIf(key -> key.length() != i);
                if (!keys.isEmpty()) {
                    String[] keyArray = keys.toArray(new String[0]);
                    resultat = Utils.getRandomWord(keyArray, RANDOM, dico);
                } else resultat = "Aucun mot de cette taille";
            } else resultat = verifLangue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultat;
    }


}
