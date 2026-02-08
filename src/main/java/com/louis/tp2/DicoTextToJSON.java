package com.louis.tp2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DicoTextToJSON {

    private ObjectMapper mapper;
    private Dico dico;

    public DicoTextToJSON() {
        setMapper(new ObjectMapper());
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        setDico(new Dico());
    }

    /**
     * pour enregistrer le dictionnaire courant dans un fichier json
     * @param filename
     */
    public void export(String filename) {
        try {

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), dico);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * pour charger un fichier texte (un mot par ligne)
     * @param stream le flux du fichier texte
     * @throws IOException
     */
    public void load(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                if (! dico.addWord(line)) System.out.println("mot rejeté : "+line);
            }
            reader.close();
        }
        catch (IOException e) {
            // pour mise au point // e.printStackTrace();
            dico = new Dico();
            reader.close();
        }
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        this.mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setDico(Dico dico) {
        this.dico = dico;
    }

    public Dico getDico() {
        return dico;
    }
}
