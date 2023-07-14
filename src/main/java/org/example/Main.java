package org.example;

import org.example.auxiliary.CLogger;
import org.example.process.FileReader;
import org.example.process.Interpreter;
import org.example.process.Processor;

import java.util.Scanner;
import java.util.logging.Level;


public class Main {


    public static void main(String[] args) {

        boolean stepMode = false;

        CLogger.setLoggerLevel(Level.FINER);
        if (args.length < 1) {
            CLogger.printWarning("[Main]Please, enter the name of source file or file with byte code.");
            return;
        }

        Processor cpu;
        String[] byteCode;
//  TODO
//  Write handling of command line arguments.
        if (args[0].matches(".*.byte")) {
            cpu = new Processor(args[0]);
        } else {
            String text = FileReader.loadStringFile(args[0]);
            byteCode = Interpreter.getByteCode(text);
//            for (String l : byteCode) {
//                System.out.println(l);
//            }
            cpu = new Processor(byteCode);
        }

        if(args.length>=2){
            if (args[1].matches("-t")){
                stepMode=true;
            }
        }

//  TODO
//  Write handling the line according the first symbol of command line
        Scanner scanner = new Scanner(System.in);
        String inputString = ">";
        if (!stepMode) {
            Thread process = new Thread(cpu);
            process.start();

            while (process.isAlive()) {
                inputString = scanner.nextLine();
                if (cpu.readyToWrite()) {
                    cpu.writeRegister(1);
                }
            }
        } else {
            while (inputString.compareTo("!")!=0) {
                inputString = scanner.nextLine();
                if (inputString.compareTo(">")==0) {
                    cpu.makeOneStep();
                }
                if (inputString.compareTo("!")==0) {
                    cpu.stopProcessor();
                }
            }
        }
    }
}