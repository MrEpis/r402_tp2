package com.louis.tp2;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;

@RestController
public class Tp2RestController {

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
    public ArrayList<String> anagrammesStric(@PathVariable String langue, @PathVariable String mot) {
        ArrayList<String> liste = new ArrayList<>();
        String verifLangue = Utils.verifierDossiers(langue);
        if (verifLangue.isEmpty()) {
            File anagrammes = new File("langues/" + langue + "/anagrammes.json");

            ObjectMapper mapper = new ObjectMapper();
            try {
                Dico dico = mapper.readValue(anagrammes, Dico.class);
                String normMot = dico.normalize(mot);
                String cle = dico.anagram(normMot);

                if (dico.getDictionnary().get(cle) != null) liste.addAll(dico.getDictionnary().get(cle));
                else liste.add("Ce mot n'existe pas");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            liste.add(verifLangue);
        }
        return liste;
    }
}
