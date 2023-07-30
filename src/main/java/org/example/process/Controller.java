package org.example.process;

import org.example.auxiliary.SimpleLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Controller {

    private static CPU cpu;
    private static HashMap<String, Function<List<String>, List<String>>> functionTable;
    private static HashMap<String, Supplier<String>> supplierTable;


    public Controller(CPU processor) {
        cpu = processor;
        functionTable = new HashMap<>();
        supplierTable = new HashMap<>();
        supplierTable.put("!", this::stopExecuting);
        supplierTable.put(">", this::makeOneStep);
        supplierTable.put("&PC", this::getProgramCounter);
        supplierTable.put("&IR", this::getInstructionRegister);
        functionTable.put("&register", this::getRegister);
        functionTable.put("$register", this::setRegister);
        functionTable.put("&memory", this::getMemory);
        functionTable.put("$memory", this::setMemory);
        functionTable.put("$PC", this::setProgramCounter);
        functionTable.put("&run", this::makeStep);
    }

    private List<String> getMemory(List<String> commands) {
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        if (var.length > 2) {
            SimpleLogger.printWarning("&memory takes none, one or two variables!");
        }
        if (var.length == 0) {
            int r = cpu.readWordFromMemory();
            result.add("0x" + Integer.toHexString(r));
        }
        if (var.length == 1) {
            int r = cpu.readWordFromMemory(var[0]);
            result.add("0x" + Integer.toHexString(r));
        }
        if (var.length == 2) {
            int[] memoryValue = cpu.readBlockFromMemory(var[0], var[1]);
            String[] memoryData = new String[memoryValue.length];
            int counter = 0;
            for (int value : memoryValue) {
                memoryData[counter] = String.format("0x%04x: 0x%02x", counter + var[0], value);
                counter++;
            }
            result = Arrays.stream(memoryData).toList();
        }
        return result;
    }

    private List<String> setMemory(List<String> commands){
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        cpu.writeWordToMemory(var[0], var[1]);
        result.add(String.format("Memory 0x%04x: 0x%02x", var[0], cpu.readWordFromMemory(var[0])));
        return result;
    }

    private List<String> getRegister(List<String> commands) {
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        int r;
        if (var.length == 1) {
            r = cpu.readRegister(var[0]);
            result.add(String.format("Register %x: 0x%02x", var[0], r));
        }
        if (var.length == 2) {
            if (var[1] < var[0]) {
                return result;
            }
            for (int i = var[0]; i < var[1]; i++) {
                r = cpu.readRegister(i);
                result.add(String.format("Register %x: 0x%02x", i, r));
            }
        }
        return result;
    }

    private  List<String> setRegister(List<String> commands){
        List<String> result = new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        cpu.writeToRegister(var[0], var[1]);
        result.add(String.format("Register %x: 0x%02x", var[0], cpu.readRegister(var[0])));
        return result;
    }

    private String getInstructionRegister() {
        String result = "";
        int r = cpu.getInstructionRegister();
        result = String.format("0x%04x", r);
        return result;
    }

    private String getProgramCounter() {
        String result = "";
        int r = cpu.getProgramCounter();
        result = String.format("0x%04x", r);
        return result;
    }

    private List<String> setProgramCounter(List<String> commands){
        List<String> result =new ArrayList<>();
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        cpu.setProgramCounter(var[0]);
        result.add(String.format("Program counter: 0x%04x", cpu.getProgramCounter()));
        return result;
    }

    private String stopExecuting() {
        cpu.stopProcessor();
        return "Stop!";
    }

    private String makeOneStep() {
        cpu.makeOneStep();
        return String.format("Program counter: 0x%04x", cpu.getProgramCounter());
    }

    private List<String> makeStep(List<String> commands) {
        int[] var = commands.stream().mapToInt(Integer::decode).toArray();
        List<String> result = new ArrayList<>();
        if (var.length != 1) {
            SimpleLogger.printWarning("This function has one argument!");
        }
        int counter = var[0];
        while (counter > 0) {
            cpu.makeOneStep();
            counter--;
        }
        result.add(String.format("Program counter: 0x%04x", cpu.getProgramCounter()));
        return result;
    }

    public boolean getStatus() {
        return cpu.isWorking();
    }

    public boolean isSupplier(String command) {
        return supplierTable.containsKey(command);
    }

    public boolean isFunction(String command) {
        return functionTable.containsKey(command);
    }

    public String callSupplier(String funcName) {
        return supplierTable.get(funcName).get();
    }

    public List<String> callFunction(String funcName, List<String> arguments) {
        return functionTable.get(funcName).apply(arguments);
    }
}
