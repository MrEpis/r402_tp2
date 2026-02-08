package com.louis.tp2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;

@Component
public class Tp2Handler {

    public Mono<ServerResponse> pong(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(BodyInserters.fromValue("I'm alive"));
    }

    public Mono<ServerResponse> langues(ServerRequest request) {
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
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(liste));
    }

    public Mono<ServerResponse> ajouterLangue(ServerRequest request) {
        Flux<Object> result = request.multipartData().map(parts ->
                        parts.get("langue") )
                .flatMapMany(Flux::fromIterable)
                .cast(FormFieldPart.class).flatMap(langueField -> {
                    String langue = langueField.value();
                    if (langue.isEmpty()) return Mono.just("Langue non précisée");


                    String directoryPath = "langues/" + langue;
                    File directory = new File(directoryPath);

                    if (!directory.exists()) {
                        if (!directory.mkdirs()) return Mono.just("Erreur création dossiers");
                    }
                    return request.multipartData().map(parts ->
                                    parts.get("fichierLangue"))
                            .flatMapMany(Flux::fromIterable)
                            .cast(FilePart.class)
                            .flatMap(fp -> {
                                Mono<InputStream> monoInputStream = fp.content()
                                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                                        .reduce(SequenceInputStream::new);
                                return monoInputStream.flatMap(is -> {
                                    DicoTextToJSON importText = new DicoTextToJSON();
                                    try {
                                        importText.load(is);
                                    } catch (IOException e) {
                                        return Mono.error(e);
                                    }
                                    importText.export(directoryPath + "/anagrammes.json");
                                    return Mono.just("le fichier a été converti en JSON");
                                });
                            });
                });
        return ServerResponse.ok().body(result, String.class);
    }

    public Mono<ServerResponse> anagrammesStrict(ServerRequest request) {
        String langue = request.pathVariable("langue");
        String mot = request.pathVariable("mot");
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

        }
        else {
            liste.add(verifLangue);
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(liste));
    }

}
