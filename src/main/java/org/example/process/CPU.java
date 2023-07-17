package org.example.process;


import java.util.Iterator;
import java.util.Map;

public abstract class CPU {
    private final int[] memory;
    private final int[] register;
    private int instructionRegister;

    private int programCounter;
    private final int programCounterMap;
    private final int memoryMap;
    private final int registerMap;

    //  lengthOfMemory - what is memory length in words
    //  numberOfRegisters - how many register which is used for calculation
    //  memoryField -   0xFF for one byte word, 0xFFFF for two bytes word and so on
    //  registerField - 0xFF for one byte word, 0xFFFF for two bytes word and so on
    //  programCounterMap - 0xF for 16 steps, 0xFF for 256 steps, 0xFFF for 4096 steps
    protected CPU(int lengthOfMemory,
                  int numberOfRegisters,
                  int memoryField,
                  int registerField,
                  int programCounterMap) {
        this.memory = new int[lengthOfMemory];
        this.register = new int[numberOfRegisters];
        this.memoryMap = memoryField;
        this.registerMap = registerField;
        this.programCounterMap = programCounterMap;
        this.programCounter = 0;
    }

    public void incrementProgramCounter() {
        programCounter = (programCounter + 1) & programCounterMap;
    }

    public void setProgramCounter(int newValue) {
        programCounter = (newValue & programCounterMap);
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void writeWordToMemory(int address, int word) {
        memory[address] = (word & memoryMap);
    }

    public int readWordFromMemory(int address) {
        return memory[(address & memoryMap)];
    }

    public int[] readBlockFromMemory(int initPos, int finalPos) {
        if (initPos >= memory.length || finalPos >= memory.length) {
            throw new NullPointerException("Wrong address of memory");
        }
        if (initPos > finalPos) {
            return new int[]{memory[finalPos], memory[initPos]};
        }
        int[] result = new int[finalPos - initPos];
        if (finalPos - initPos >= 0) System.arraycopy(memory, initPos, result, initPos, finalPos - initPos);
        return result;
    }

    public int readWordFromMemory() {
        return memory[instructionRegister];
    }

    public int readRegister(int i) {
        if (i >= register.length) {
            throw new NullPointerException("Wrong address of register");
        }
        return register[i];
    }

    public void writeBlockToMemory(Map<Integer, Integer> block) {
        Iterator<Map.Entry<Integer, Integer>> iterator = block.entrySet().iterator();
        Map.Entry<Integer, Integer> element;
        while (iterator.hasNext()) {
            element = iterator.next();
            memory[(element.getKey() & memory.length)] = (element.getValue() & memoryMap);
        }
    }

    public void writeToRegister(int address, int word) {
        if (address >= register.length) {
            throw new NullPointerException("Wrong address of register");
        }
        register[address] = (word & registerMap);
    }

    public void clearInstructionRegister() {
        instructionRegister = 0;
    }

    public int getInstructionRegister() {
        return instructionRegister;
    }

    public void writeToIR(int operand) {
        this.instructionRegister = operand;
    }

    public abstract void makeOneStep();

}
