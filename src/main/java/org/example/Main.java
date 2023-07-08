package org.example;

import org.example.process.FileReader;
import org.example.process.Interpreter;
import org.example.process.Processor;

import java.util.Scanner;


public class Main {


    public static void main(String[] args) {

        boolean stepMode = true;

        if (args.length < 1) {
            System.out.println("Please, enter the name of source file or file with byte code.");
            System.exit(-1);
        }

        Processor cpu;
        String[] byteCode;

        if (args[0].matches(".*.byte")) {
            cpu = new Processor(args);
        } else {
            Interpreter interp = new Interpreter();
            String text = FileReader.loadStringFile(args[0]);
            byteCode = Interpreter.getByteCode(text);
            for (String l : byteCode) {
                System.out.println(l);
            }
            cpu = new Processor(byteCode);
        }

        Scanner scanner = new Scanner(System.in);
        String inputString = ">";
        if (!stepMode) {
            Thread process = new Thread(cpu);
            process.run();

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
            }
        }
    }
}