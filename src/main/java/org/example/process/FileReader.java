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

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static String loadStringFile(String filename) {
        StringBuilder result = new StringBuilder();
        String[] lines = loadFile(filename);
        for (String line:lines) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    public static String[] loadFile(String filename){
        List<String> lines = new ArrayList<>();
        Path defaultPath = null;

        try {
            defaultPath = Paths.get(filename);
            lines = Files.readAllLines(defaultPath, CHARSET);
        }catch (InvalidPathException invalidPathException){
            SimpleLogger.printError(
                    String.format("Invalid filename '%s'.", filename));
        }
        catch (IOException e) {
            SimpleLogger.printError(
                    String.format("Cannot read file '%s'.", filename));
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
//        if (!Files.isWritable(pathToFile)){
//            throw new ReadOnlyFileSystemException();
//        }
        String text = Arrays.stream(lines).map(line -> line+"\n").collect(Collectors.joining());
        try {
            Files.writeString(pathToFile, text, CHARSET, StandardOpenOption.CREATE);
        } catch (IOException e) {
            SimpleLogger.printError(
                    String.format("Cannot write the file '%s'.", filename));
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
        return (answer.compareTo("y")==0);
    }
}
