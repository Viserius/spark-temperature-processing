package org.rug.scalablecomputing.temperatures.simulator.parser;


import org.rug.scalablecomputing.temperatures.simulator.domain.Measurement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileHandler {

    private DataParser parser;

    public FileHandler() {
        this.parser = new DataParser();
    }

    Scanner getScanner(File dataFile) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(dataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return scanner;
    }

    File getFile(String fileName) {
        return new File(fileName);
    }

}
