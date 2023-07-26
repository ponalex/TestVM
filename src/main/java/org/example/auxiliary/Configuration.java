package org.example.auxiliary;

import org.example.parser.GetOpt;

import java.util.*;

public class Configuration {

    private static Configuration configuration;
    private static final String TEMPLATE = "c:s:b:io:O:";
    public static boolean SOURCE_FILE;
    public static String INPUT_FILE;
    public static String OUTPUT_FILE;
    public static String CONFIG_FILE = "vm.config";
    public static boolean INTERACTIVE;

    private Configuration() {
        SOURCE_FILE = false;
        INTERACTIVE = true;
        INPUT_FILE="";
        OUTPUT_FILE="";
    }

    public static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public static void setConfiguration(String[] args) {
        List<String> arguments = GetOpt.getCommands(args, TEMPLATE);
        Iterator<String> iterator = arguments.iterator();
        String temp;
        while (iterator.hasNext()) {
            temp = iterator.next();
            switch (temp) {
                case "-c":
                    CONFIG_FILE = iterator.next();
                    break;
                case "-s":
                    INPUT_FILE = iterator.next();
                    SOURCE_FILE = true;
                    INTERACTIVE = false;
                    break;
                case "-b":
                    INPUT_FILE = iterator.next();
                    SOURCE_FILE = false;
                    INTERACTIVE = false;
                    break;
                case "-i":
                    INTERACTIVE = true;
                    break;
                case "-o":
                    OUTPUT_FILE = iterator.next();
                    SOURCE_FILE = false;
                    INTERACTIVE = false;
                    break;
                case "-O":
                    OUTPUT_FILE = iterator.next();
                    INTERACTIVE = true;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static void loadConfiguration(String filename) {

    }
}
