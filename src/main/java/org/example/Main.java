package org.example;

import org.example.auxiliary.SimpleLogger;
import org.example.auxiliary.Configuration;
import org.example.auxiliary.Display;
import org.example.process.CPU8;
import org.example.process.CommandController;
import org.example.process.FileReader;
import org.example.parser.Interpreter;


import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;


public class Main {


    public static void main(String[] args) {

        SimpleLogger.setLoggerLevel(Level.WARNING);
        Configuration config = Configuration.getConfiguration();
        Configuration.setConfiguration(args);
// Set register name to substitute its address
        HashMap<String, Integer> variableMap = new HashMap<>();
        for (int i = 0; i < 16; i++) {
            variableMap.put("reg" + Integer.toHexString(i).toUpperCase(), i);
        }

//
//  Check if filename is passed to configuration.
        CPU8 cpu = new CPU8();
        if (!Configuration.INPUT_FILE.isEmpty()) {
            String text = FileReader.loadStringFile(Configuration.INPUT_FILE);
            String[] byteCode;
//  Source (SOURCE_FILE) contains pseudo assembly code
//  There are two types of input files
//  Pseudo assembly and pseudo byte code
            if (!Configuration.SOURCE_FILE) {
                byteCode = Interpreter.splitByteString(text.split("\n")).toArray(new String[0]);
            } else {
                byteCode = Interpreter.translateCommandToCode(text, variableMap);
            }
            cpu.loadMemory(byteCode);
        }

//  Write handling the line according the first symbol of command line
        Scanner scanner = new Scanner(System.in);
        String inputString = ">";
//  Create controller to get access to cpu
//      for run mode
//      for interactive mode
        CommandController controller = new CommandController(cpu);
        if (!Configuration.INTERACTIVE) {
            Thread process = new Thread(cpu);
            process.start();

            while (process.isAlive()) {
                inputString = scanner.nextLine();
//                if (cpu.readyToWrite()) {
//                    cpu.writeRegister(1);
//                }
            }
        } else {
            while (!controller.getStatus()) {
                Display.promptString();
                inputString = scanner.nextLine();
                controller.sendCommand(inputString);
            }
        }
    }
}