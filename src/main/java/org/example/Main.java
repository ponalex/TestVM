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

        HashMap<String, Integer> variableMap = new HashMap<>();
        for (int i = 0; i < 16; i++) {
            variableMap.put("reg" + Integer.toHexString(i).toUpperCase(), i);
        }

//  TODO
//  Write handling of command line arguments.
        CPU8 cpu = new CPU8();
        if (Configuration.INTERACTIVE) {
            String text = FileReader.loadStringFile(Configuration.INPUT_FILE);
            String[] byteCode;
            if (!Configuration.SOURCE_FILE) {
                byteCode = Interpreter.splitByteString(text.split("\n")).toArray(new String[0]);
            } else {
                byteCode = Interpreter.translateCommandToCode(text, variableMap);
            }
            cpu.loadMemory(byteCode);
        }

//  TODO
//  Write handling the line according the first symbol of command line
        Scanner scanner = new Scanner(System.in);
        String inputString = ">";
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