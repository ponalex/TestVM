package org.example.process;


import org.example.auxiliary.Display;
import org.example.auxiliary.SimpleLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommandController {

    private static CPU processor;
    private static final String OPERANDS_STARTS = "[";
    private static final String OPERANDS_FINISHES = "]";
    private static final String OPERANDS_SEPARATOR = ",";


    public CommandController(CPU cpu) {
        processor = cpu;
    }

    public boolean getStatus() {
        return processor.isWorking();
    }


    public void sendCommand(String command) {
        List<String> operands = getLexeme(command);
        try {
            String lines = calculate(operands);
        }
        catch (NumberFormatException nfe){
            SimpleLogger.printWarning("wrong format!");
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            SimpleLogger.printWarning("One of the index out of bounds!");
        }
    }

    public static List<String> getLexeme(String text) {
        List<String> lexeme = new ArrayList<>();
        String command = text.replaceAll(" ", "");
        Iterator<String> letter = Arrays.stream(command.split("")).iterator();
        String symbol;
        String result = "";
        while (letter.hasNext()) {
            symbol = letter.next();
            if (symbol.matches("\\" + OPERANDS_STARTS) || symbol.matches(OPERANDS_FINISHES)) {
                if (!result.isEmpty()) {
                    lexeme.add(result);
                    result = "";
                }
                lexeme.add(symbol);
                continue;
            }
            if (symbol.compareTo(OPERANDS_SEPARATOR) == 0) {
                if (!result.isEmpty()) lexeme.add(result);
                result = "";
                continue;
            }
            result = result + symbol;
        }
        return lexeme;
    }

    public String calculate(List<String> command) {
        String temp;
        int counter = 0;
        String result = "";
        int startOperand = 0;
        int finishOperand = 0;
        List<String> variables = new ArrayList<>();
        while (counter < command.size()) {
            temp = command.get(counter);
            if (temp.compareTo(OPERANDS_STARTS) == 0) {
                variables.clear();
                variables.add(command.get(counter - 1));
                startOperand = counter;
            }
            if (temp.compareTo(OPERANDS_FINISHES) == 0) {
                finishOperand = counter;
//                command.add(startOperand-1, callFunc(variables));
                for (int i = finishOperand; i >= (startOperand - 1); i--) {
                    command.remove(i);
                }
                command.addAll(startOperand - 1, this.callFunc(variables));

                counter = 0;
            }
            if (temp.matches("(0x)?[+\\-0-9a-fA-F]+")) {
                variables.add(temp);
            }
            counter++;
        }
//        for (String c:command) {
//            result= result +  c +"\n";
//        }
        String[] c = command.toArray(String[]::new);
        Display.printStringArray(Display.getFormattedBlock(c));
        return result;
    }

    private List<String> callFunc(List<String> command) {
        String func = command.remove(0);
        List<String> result = new ArrayList<>();
        switch (func) {
            case "&memory" -> {
                result = getMemory(command);
            }
            case "&register" -> {
                result = getRegister(command);
            }
            case "&IR" -> {
                result.add(getInstructionRegister(command));
            }
            case "&PC" -> {
                result.add(getProgramCounter(command));
            }
            case ">" -> {
                result.add(makeStep(command));
            }
            case "$register" -> {
                int[] var = command.stream().mapToInt(Integer::decode).toArray();
                processor.writeToRegister(var[0], var[1]);
                result.add(String.format("Register %x: 0x%02x", var[0], processor.readRegister(var[0])));
            }
            case "$memory" -> {
                int[] var = command.stream().mapToInt(Integer::decode).toArray();
                processor.writeWordToMemory(var[0], var[1]);
                result.add(String.format("Memory 0x%04x: 0x%02x", var[0], processor.readRegister(var[0])));
            }
            case "$PC" -> {
                int[] var = command.stream().mapToInt(Integer::decode).toArray();
                processor.setProgramCounter(var[0]);
                result.add(String.format("Program counter: 0x%04x", processor.readRegister(var[0])));
            }
            default -> SimpleLogger.printWarning(
                            String.format("There is no such function!"));
        }
        return result;
    }

    private List<String> getMemory(List<String> commands){
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        if (var.length > 2) {
            // Log it
            SimpleLogger.printWarning("&memory takes none, one or two variables!");
        }
        if (var.length == 0) {
            int r = processor.readWordFromMemory();
            result.add("0x" + Integer.toHexString(r));
        }
        if (var.length == 1) {
            int r = processor.readWordFromMemory(var[0]);
            result.add("0x" + Integer.toHexString(r));
        }
        if (var.length == 2) {
            int[] memoryValue = processor.readBlockFromMemory(var[0], var[1]);
            String[] memoryData = new String[memoryValue.length];
            int counter = 0;
            for (int value:memoryValue) {
                memoryData[counter] = String.format("0x%04x: 0x%02x", counter+var[0], value);
                counter++;
            }
            result = Arrays.stream(memoryData).toList();
        }
        return result;
    }

    public List<String> getRegister(List<String> commands){
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        int r;
        if (var.length == 1) {
            r = processor.readRegister(var[0]);
            result.add(String.format("Register %x: 0x%02x", var[0], r));
        }
        if (var.length == 2) {
            if(var[1]<var[0]){ return result;}
            for (int i = var[0]; i < var[1] ; i++) {
                r = processor.readRegister(i);
                result.add(String.format("Register %x: 0x%02x", i, r));
            }
        }
        return result;
    }

    public String getInstructionRegister(List<String> commands){
        String result="";
        if (commands.size() != 0) {
            // Log it
            throw new IllegalArgumentException("This arguments takes one or two variables!");
        } else {
            int r = processor.getInstructionRegister();
            result = String.format("0x%04x", r);
        }
        return result;
    }

    private String getProgramCounter(List<String> commands){
        String result="";
        if (commands.size() != 0) {
            // Log it
            throw new IllegalArgumentException("This arguments takes one or two variables!");
        } else {
            int r = processor.getProgramCounter();
            result = String.format("0x%04x", r);
        }
        return result;
    }

    private String makeStep(List<String> commands){
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        String result = "";
        if (var.length > 1) {
            // Log it
//            throw new IllegalArgumentException("This arguments takes one or two variables!");
            return "Too many arguments!";
        } else {
            if (var.length == 0) {
                processor.makeOneStep();
            } else {
                int counter = var[0];
                while (counter > 0) {
                    processor.makeOneStep();
                    counter--;
                }
            }
        }
        result = String.format("Program counter: 0x%04x", processor.getProgramCounter());
        return result;
    }
}
