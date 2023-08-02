package org.example.process;

import org.example.auxiliary.SimpleLogger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileReader {

    public static final int exitcode = -2;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static String loadStringFile(String filename) {
        File configFile = new File(filename);
        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            SimpleLogger.printError(
                    String.format("File \'%s\' was not found.", filename));
            System.exit(exitcode);
        }
        catch (SecurityException se){
            SimpleLogger.printError(
                    String.format("\'%s\':  permission denied.", filename));
            System.exit(exitcode);
        }

        byte[] nBytes = new byte[(int) configFile.length()];
        try {
            fileStream.read(nBytes);
        } catch (IOException e) {
            SimpleLogger.printError(
                    String.format("Cannot read file \'%s\'.", filename));
                    throw new RuntimeException(e);
        }
        String text = new String(nBytes, StandardCharsets.UTF_8);
        return text;
    }

    public static String[] loadFile(String filename) {
        Path defaultPath = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(defaultPath)) {
            SimpleLogger.printError(String.format("File \'%s\' was not found", filename));
            throw new FileSystemNotFoundException(String.format("File %s was not found", filename));
        }

        try {
            lines = Files.readAllLines(defaultPath, CHARSET);
        } catch (IOException e) {
            SimpleLogger.printError(
                    String.format("Cannot read file \'%s\'.", filename));
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
            SimpleLogger.printError(
                    String.format("Cannot write the file \'%s\'.", filename));
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
