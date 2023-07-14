package org.example.process;

import org.example.auxiliary.CLogger;

import java.util.Arrays;
import java.util.Iterator;


public class Processor implements Runnable {

    private final int[] Register = new int[16];
    private final int[] Memory = new int[65536];
    // IR - instruction registor
    private int IR;
    //
    private int programCounter;

    private static boolean isStopped = false;


    public Processor(String filename) {

        String text = FileReader.loadStringFile(filename);
        Iterator<String> sc = Arrays.stream(text.split("\n")).iterator();

        while (sc.hasNext()) {
            String line = sc.next();
            CLogger.printOut(String.format("Line from file: --> %s", line));
            String[] fields = line.split("\s*:\s*");
            int addr = Integer.parseInt(fields[0], 16) & 0xFFFF;
            int inst = Integer.parseInt(fields[1], 16) & 0xFF;
            Memory[addr] = inst;
        }
        programCounter = 0;
    }

    public Processor(String[] byteCode) {

        int addr = 0;
        int inst = 0;
        String[] subString = new String[2];
        for (String line : byteCode) {
            subString = line.split("\s*:\s*", 2);
            addr = Integer.parseInt(subString[0], 16) & 0xFFFF;
            inst = Integer.parseInt(subString[1], 16) & 0xFF;
            Memory[addr] = inst;
        }
        programCounter = 0;
    }

    public boolean readyToRead() {
        if ((Register[14] & 0b11) == 1) {
            CLogger.printOut("Set 'ready to read' bit");
            return true;
        }
        CLogger.printOut("Clear 'ready to read' bit");
        return false;
    }

    public boolean readyToWrite() {
        CLogger.printOut("Set 'ready to write' bit");
        return ((Register[14] & 0b11) == 2);
    }

    public int readRegister() {
        int result = Register[15];
        CLogger.printOut(String.format("Write to result  %s", Integer.toHexString(Register[15])));
        Register[14] = Register[14] & 0xFE;
        CLogger.printOut(String.format("Set Register[14] = %s", Integer.toHexString(Register[14])));
        return result;
    }

    public void writeRegister(int number) {
        Register[15] = number;
        CLogger.printOut(String.format("Write to Register[15]  %s", Integer.toHexString(Register[15])));
        Register[14] = Register[14] | 0b1;
        CLogger.printOut(String.format("Set Register[14] = %s", Integer.toHexString(Register[14])));
    }

    public void readMemory() {

        IR = Memory[programCounter];
        IR = IR << 8;
        programCounter++;
        programCounter = programCounter & 0xFFFF;
        IR = IR + Memory[programCounter];
        programCounter++;
        programCounter = programCounter & 0xFFFF;
        CLogger.printPC(programCounter, IR);

    }

    public void makeOneStep() {

        readMemory();

        int opcode = (IR >> 12) & 0xF;
        int regA = (IR >> 8) & 0xF;
        int reg0 = (IR >> 4) & 0xF;
        int reg1 = IR & 0xF;
        int expandedA = (Register[(regA & 0xFE)] << 8) + (Register[(regA & 0xFE) + 1]);
        int value = IR & 0xFF;
        int address = ((Register[reg0] << 8) & 0xFFFF) + ((Register[reg1]) & 0xFF);
        CLogger.printVariable(
                opcode,
                regA,
                reg0,
                reg1,
                expandedA, value, address);
        int ex;
        switch (opcode) {
            case 0:
                CLogger.printTypeZero("NOP");
                break;
            case 1:
                CLogger.printTypeOne("MVMV", expandedA, value);
                Memory[expandedA] = value;
                break;
            case 2:
                CLogger.printTypeThree("MVM", Register[regA], Register[reg0], Register[reg1]);
                Memory[address] = Register[regA];
                break;
            case 3:
                CLogger.printTypeOne("MVRV", regA, value);
                Register[regA] = value;
                break;
            case 4:
                CLogger.printTypeThree("MVR", regA, Register[reg0], Register[reg1]);
                Register[regA] = Memory[address];
                break;
            case 5:
                CLogger.printTypeTwo("SWP", reg0, reg1);
                Register[reg1] = Register[reg0];
                break;
            case 6:
                CLogger.printTypeTwo("SPC", reg0, reg1);
                Register[reg0] = (programCounter >> 8) & 0xFF;
                Register[reg1] = programCounter & 0xFF;
                break;
            case 7:
                CLogger.printTypeTwo("CALL", Register[reg0], Register[reg1]);
                programCounter = address;
                break;
            case 8:
                CLogger.printTypeThree("JMPE", expandedA, Register[reg0], Register[reg1]);
                if (Register[reg0] == Register[reg1]) {
                    programCounter = expandedA;
                }
                break;
            case 9:
                CLogger.printTypeTwo("NOT", Register[reg0], reg1);
                Register[reg1] = ~Register[reg0];
                break;
            case 10:
                CLogger.printTypeThree("AND", Register[regA], Register[reg0], reg1);
                Register[reg1] = Register[regA] & Register[reg0];
                break;
            case 11:
                CLogger.printTypeThree("PLUS", Register[regA], Register[reg0], reg1);
                summation(reg1, Register[regA], Register[reg0]);
                break;
            case 12:
                CLogger.printTypeThree("OR", Register[regA], Register[reg0], reg1);
                Register[reg1] = Register[regA] | Register[reg0];
                break;
            case 13:
                CLogger.printTypeThree("MINUS", Register[regA], Register[reg0], reg1);
                summation(reg1, Register[regA], -1 * Register[reg0]);
                break;
            case 14:
                CLogger.printTypeOne("INCR", Register[regA], value);
                if (value > 127) {
                    ex = 127 - value;
                } else {
                    ex = value;
                }
                summation(regA, Register[regA], ex);
                break;
            case 15:
                CLogger.printTypeOne("SHFT", Register[regA], value);
                if (value > 127) {
                    ex = value - 127;
                    Register[regA] = (Register[regA] >> ex) & 0xFF;
                } else {
                    ex = value;
                    Register[regA] = (Register[regA] << ex) & 0xFF;
                }
                break;
        }

    }

    public void stopProcessor(){
        isStopped = true;
    }

    public void run() {
        while (!isStopped) {
            makeOneStep();
        }
    }

    private void summation(int reg2, int valueA, int value1) {
        int ex = (valueA + value1) & 0xFFFF;
        CLogger.printOut(String.format("The sum is : %d", ex));
        Register[reg2] = ex & 0xFF;
        if (reg2 > 13) {
            return;
        }
        if ((reg2 & 0b1) == 1) {
            ex = (ex >> 8) + Register[reg2 & 0xFE];
            CLogger.printOut(String.format("The older Register[%s] contains %s",
                    Integer.toHexString(reg2 & 0xFE),
                    Integer.toHexString(Register[reg2 & 0xFE])));
            Register[reg2 & 0xFE] = ex & 0xFF;
            ex = ex >> 8;
            if (reg2 < 8 && ex != 0) {
                Register[14] = Register[14] | (1 << (4 + (reg2 >> 1)));
            }
        }
    }

}
