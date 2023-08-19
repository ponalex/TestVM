package org.example.process;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CPU implements Runnable{
    private final int[] memory;
    private final int[] register;
    private int instructionRegister;

    private int programCounter;
    private final int programCounterMap;
    private final int memoryMap;
    private final int registerMap;

    private static AtomicBoolean isStopped;


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
        memory = new int[lengthOfMemory];
        register = new int[numberOfRegisters];
        memoryMap = memoryField;
        registerMap = registerField;
        this.programCounterMap = programCounterMap;
        programCounter = 0;
        isStopped = new AtomicBoolean(false);
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
        if (finalPos - initPos >= 0) System.arraycopy(memory, initPos, result, 0, finalPos - initPos);
        return result;
    }

    public String[] getMemoryValue(int initPos, int lastPos){
        int[] memoryValue = readBlockFromMemory(initPos, lastPos);
        String[] result = new String[memoryValue.length];
        int counter = 0 ;
        for (int value:memoryValue) {
            result[counter] = String.format("0x%04x : 0x%02x", counter+initPos, value);
            counter++;
        }
        return result;
    }

    protected int readWordFromMemory() {
        return memory[programCounter];
    }

    public synchronized int readRegister(int i) {
        if (i >= register.length) {
            throw new NullPointerException("Wrong address of register");
        }
        return register[i];
    }

    public void writeBlockToMemory(int[] block, int initDestPos) {
        if((initDestPos + block.length) > memory.length){
            throw new NullPointerException("block for writing to memory is too big.");
        }
        System.arraycopy(block,0,memory,initDestPos, block.length);
    }

    public void writeBlockToMemory(int[] block) {
        if (block.length > memory.length) {
            throw new NullPointerException("block for writing to memory is too big.");
        }
        System.arraycopy(block,0,memory,0, block.length);
    }

    public synchronized void writeToRegister(int address, int word) {
        if (address >= register.length) {
            throw new NullPointerException("Wrong address of register");
        }
        register[address] = (word & registerMap);
    }

    public int getInstructionRegister() {
        return instructionRegister;
    }

    public void writeToIR(int operand) {
        this.instructionRegister = operand;
    }

    public abstract void makeOneStep();

    public void run(){
        while (!isStopped.get()){
            makeOneStep();
        }
    }

    public void stopProcessor(){
        isStopped.set(true);
    }

    public boolean isWorking(){
        return isStopped.get();
    }

}
