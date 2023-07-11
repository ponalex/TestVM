package org.example.process;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.*;

public class Processor implements Runnable {

    private int[] Register = new int[16];
    private int[] Memory = new int[65536];

    private int programCounter;

    private static final Logger logger = Logger.getLogger(Processor.class.getName());
    // Finest is intented for debugging

    public Processor(String filename) {

        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String text = FileReader.loadStringFile(filename);
        Iterator<String> sc = Arrays.stream(text.split("\n")).iterator();

        while (sc.hasNext()) {
            String line = sc.next();
            logger.log(Level.FINE, String.format("Line from file: --> %s", line));
            String[] fields = line.split("[ ]*:[ ]*");
            logger.log(Level.FINEST, String.format("Cell number : %s", fields[0]));
            logger.log(Level.FINEST, String.format("Value : %s", fields[1]));
            int addr = Integer.parseInt(fields[0], 16) & 0xFFFF;
            int inst = Integer.parseInt(fields[1], 16) & 0xFF;
            Memory[addr] = inst;
        }
        programCounter = 0;
    }

    public Processor(String[] byteCode){

        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int addr=0;
        int inst=0;
        String[] subString = new String[2];
        for (String line:byteCode) {
            subString = line.split("[ ]*:[ ]*", 2);
            addr = Integer.parseInt(subString[0], 16) & 0xFFFF;
            inst = Integer.parseInt(subString[1], 16) & 0xFF;
            Memory[addr]=inst;
        }
        programCounter = 0;
    }

    public boolean readyToRead() {
        if ((Register[14] & 0b11) == 1) {
            logger.log(Level.FINE, "Set \'ready to read\' bit");
            return true;
        }
        logger.log(Level.FINE, "Drop \'ready to read\' bit");
        return false;
    }

    public boolean readyToWrite() {
        logger.log(Level.FINE, "Set \'ready to write\' bit");
        if ((Register[14] & 0b11) == 2) {
            return true;
        }
        return false;
    }

    public int readRegister() {
        int result = Register[15];
        logger.log(Level.FINE, String.format("Write to result  %s", Integer.toHexString(Register[15])));
        Register[14] = Register[14] & 0xFE;
        logger.log(Level.FINE, String.format("Set Register[14] = %s", Integer.toHexString(Register[14])));
        return result;
    }

    public void writeRegister(int number) {
        Register[15] = number;
        logger.log(Level.FINE, String.format("Write to Register[15]  %s", Integer.toHexString(Register[15])));
        Register[14] = Register[14] | 0b1;
        logger.log(Level.FINE, String.format("Set Register[14] = %s", Integer.toHexString(Register[14])));
    }

    public void makeOneStep(){
        logger.log(Level.FINE, String.format(   "|    Program counter: 0x%04X", programCounter));
        // IR - instruction registor
        int IR = Memory[programCounter];
        IR = IR << 8;
        programCounter++;
        IR = IR + Memory[programCounter];
        logger.log(Level.FINE,String.format(   "|       IR: %s", Integer.toBinaryString(IR)));
        programCounter++;

        int opcode = (IR >> 12) & 0xF;
        int regA = (IR >> 8) & 0xF;
        int reg1 = (IR >> 4) & 0xF;
        int reg2 = (IR >> 0) & 0xF;
        int expandedA = (Register[(regA & 0xFE)] << 8) + (Register[(regA & 0xFE) + 1]);
        int value = IR & 0xFF;
        int address = ((Register[reg1] << 8) & 0xFFFF) + ((Register[reg2]) & 0xFF);

        logger.log(Level.FINEST, String.format("+---------+", opcode));
        logger.log(Level.FINEST, String.format("|OPCODE: %x|", opcode));
        logger.log(Level.FINEST, String.format("|  regA %s |  reg1 %s  |  reg2 %s  |",
                Integer.toHexString(regA),
                Integer.toHexString(reg1),
                Integer.toHexString(reg2)));
        logger.log(Level.FINEST, String.format("| Expanded regA: %04X", expandedA));
        logger.log(Level.FINEST, String.format("| Value: %s", Integer.toBinaryString(value)));
        logger.log(Level.FINEST, String.format("| Address from reg1:reg2 %04X", address));
        int ex;
        switch (opcode) {
            case 0:
                logger.log(Level.FINEST, String.format("NOP"));
                break;
            case 1:
                logger.log(Level.FINEST, String.format("MVMV %s [%s]",
                        Integer.toHexString(regA),
                        Integer.toHexString(value)));
                Memory[expandedA] = value;
                break;
            case 2:
                logger.log(Level.FINEST, String.format("MVM %s [%s%s]",
                        Integer.toHexString(regA),
                        Integer.toHexString(Register[reg1]),
                        Integer.toHexString(Register[reg2])));
                Memory[address] = Register[regA];
                break;
            case 3:
                logger.log(Level.FINEST, String.format("MVRV %s [%s]",
                        Integer.toHexString(regA),
                        Integer.toHexString(value)));
                Register[regA] = value;
                break;
            case 4:
                logger.log(Level.FINEST, String.format("MVR %s [%s%s]",
                        Integer.toHexString(regA),
                        Integer.toHexString(Register[reg1]),
                        Integer.toHexString(Register[reg2])));
                Register[regA] = Memory[address];
                break;
            case 5:
                logger.log(Level.FINEST, String.format("SWP ---- %s %s",
                        Integer.toHexString(regA),
                        Integer.toHexString(reg1)));
                Register[reg2] = Register[reg1];
                break;
            case 6:
                logger.log(Level.FINEST, String.format("SPC"));
                Register[reg1] = (programCounter >> 8) & 0xFF;
                Register[reg2] = programCounter & 0xFF;
                break;
            case 7:
                logger.log(Level.FINEST, String.format("|:> CALL %04X", address));
                programCounter = address;
                break;
            case 8:
                logger.log(Level.FINEST, String.format("JMPE"));
                if (Register[reg1] == Register[reg2]) {
                    programCounter = expandedA;
                }
                break;
            case 9:
                logger.log(Level.FINEST, String.format("NOT"));
                Register[reg2] = ~Register[reg1];
                break;
            case 10:
                logger.log(Level.FINEST, String.format("AND"));
                Register[reg2] = Register[regA] & Register[reg1];
                break;
            case 11:
                logger.log(Level.FINEST, String.format("PLUS"));
                summation(reg2, Register[regA], Register[reg1]);
                break;
            case 12:
                logger.log(Level.FINEST, String.format("OR"));
                Register[reg2] = Register[regA] | Register[reg1];
                break;
            case 13:
                logger.log(Level.FINEST, String.format("MINUS"));
                summation(reg2, Register[regA], -1 * Register[reg1]);
                break;
            case 14:
                logger.log(Level.FINEST, String.format("INCR"));
                if (value > 127) {
                    ex = 127 - value;
                } else {
                    ex = value;
                }
                summation(regA, Register[regA], ex);
                break;
            case 15:
                logger.log(Level.FINEST, String.format("SHFT"));
                if (value > 127) {
                    ex = 127 - value;
                    Register[regA] = (Register[regA] >> ex) & 0xFF;
                } else {
                    ex = value;
                    Register[regA] = (Register[regA] << ex) & 0xFF;
                }
                break;
        }

        if (programCounter >= 65536) {
            logger.log(Level.FINE, String.format("Counter (%d) exceeds the size of memory", programCounter));
            System.exit(-1);
        }
    }

    public void run() {
        while (true) {
            makeOneStep();
        }
    }

    private void summation(int reg2, int valueA, int value1) {
        int ex = (valueA + value1) & 0xFFFF;
        logger.log(Level.FINE, String.format("The sum is : %d", ex));
        Register[reg2] = ex & 0xFF;
        if (reg2 > 13) {
            return;
        }
        if ((reg2 & 0b1) == 1) {
            ex = (ex >> 8) + Register[reg2 & 0xFE];
            logger.log(Level.FINE, String.format("The older Register[%s] contains %s",
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
