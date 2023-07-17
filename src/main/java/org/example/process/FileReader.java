package org.example.process;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReader {

    public static final Logger logger = Logger.getLogger(FileReader.class.getName());
    public static final int exitcode = -2;
    public static String loadStringFile(String filename){
        File configFile = new File(filename);
        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,
                    String.format("[%s] File \'%s\' not found", logger.getName(), filename));
            System.exit(exitcode);
        }

        byte[] nBytes = new byte[(int) configFile.length()];
        try {
            fileStream.read(nBytes);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    String.format("[%s] Cannot read file \'%s\'.", logger.getName(), filename),
                    new RuntimeException(e)
            );
        }
        String text = new String(nBytes, StandardCharsets.UTF_8);
        return text;
    }
}
