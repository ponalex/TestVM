package org.example.process;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileReader {

    public static final Logger logger = Logger.getLogger(FileReader.class.getName());
    public static final int exitcode = -2;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static String loadStringFile(String filename) {
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

    public static String[] loadFile(String filename) {
        Path defaultPath = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(defaultPath)) {
            throw new FileSystemNotFoundException(String.format("File %s was not found", filename));
        }

        try {
            lines = Files.readAllLines(defaultPath, CHARSET);
        } catch (IOException e) {
            throw new RuntimeException("Problem with reading '%s' file");
        }

        return lines.toArray(new String[0]);
    }

    public static void writeToFile(String filename, String[] lines) {
        Path pathToFile = Paths.get(filename);
        if (Files.exists(pathToFile)) {
            System.out.println("File already exists. Do you want to replace them?");
            if (!getPermission()) {
                return;
            }
        }
        if (!Files.isWritable(pathToFile)){
            throw new ReadOnlyFileSystemException();
        }
        String text = Arrays.stream(lines).map(line -> line+"\n").collect(Collectors.joining());
        try {
            Files.writeString(pathToFile, text, CHARSET, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Problem with writing file %s", filename));
        }
    }

    private static boolean getPermission() {
        Scanner sc = new Scanner(System.in);
        String answer;
        System.out.print("Please enter y(yes)/n(no): ");
        answer = sc.nextLine();
        answer = answer.strip();
        answer = answer.toLowerCase().substring(0, 2);
        if (answer.compareTo("y") == 0) {
            return true;
        }
        return false;
    }
}
