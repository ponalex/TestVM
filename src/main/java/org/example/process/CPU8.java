package org.example.process;

public class CPU8 extends CPU {

    private static final int LENGTH_OF_MEMORY = 65536;
    private static final int NUMBERS_OF_REGISTERS = 16;
    private static final int MEMORY_MAP = 0xFF;
    private static final int REGISTER_MAP = 0xFF;
    private static final int PROGRAM_COUNTER_MAP = 0xFF;
    private static final int PROTECTED_REGISTER = 14;

    int opcode;
    int regA;
    int reg0;
    int reg1;
    int expandedA;
    int value;
    int address;

    public CPU8() {
        super(
                LENGTH_OF_MEMORY,
                NUMBERS_OF_REGISTERS,
                MEMORY_MAP,
                REGISTER_MAP,
                PROGRAM_COUNTER_MAP
        );
    }

    @Override
    public void makeOneStep() {
        excerpt();
        evaluate();
        clearInstructionRegister();
    }

    // selection from
    private void excerpt() {
        writeToIR(readWordFromMemory() << 8);
        incrementProgramCounter();
        writeToIR(getInstructionRegister() + readWordFromMemory());
        incrementProgramCounter();
    }

    private void evaluate() {
        opcode = ((getInstructionRegister() >> 12) & 0xF);
        regA = (getInstructionRegister() >> 8) & 0xF;
        reg0 = (getInstructionRegister() >> 4) & 0xF;
        reg1 = getInstructionRegister() & 0xF;

        expandedA = (readRegister(regA & 0xFE) << 8) + (readRegister((regA & 0xFE) + 1));
        value = getInstructionRegister() & 0xFF;
        address = ((readRegister(reg0) << 8) & 0xFFFF) + (readRegister(reg1) & 0xFF);
        calculate();
    }

    private void calculate() {
        int ex;
        switch (Opcodes.NOP.getOpcode(opcode)) {
            case NOP:
                break;
            case MVMV:
                writeWordToMemory(expandedA, value);
                break;
            case MVM:
                writeWordToMemory(address, readRegister(regA));
                break;
            case MVRV:
                writeToRegister(regA, value);
                break;
            case MVR:
                writeToRegister(regA, readWordFromMemory(address));
                break;
            case SWP:
                writeToRegister(reg1, readRegister(reg0));
                break;
            case SPC:
                writeToRegister(reg0, (getProgramCounter() >> 8) & 0xFF);
                writeToRegister(reg1, (getProgramCounter() & 0xFF));
                break;
            case CALL:
                setProgramCounter(address);
                break;
            case JMPE:
                if (readRegister(reg0) == readRegister(reg1)) {
                    setProgramCounter(expandedA);
                }
                break;
            case NOT:
                writeToRegister(reg1, ~readRegister(reg0));
                break;
            case AND:
                writeToRegister(reg1, readRegister(regA) & readRegister(reg0));
                break;
            case PLUS:
                summation(reg1, readRegister(regA), readRegister(reg0));
                break;
            case OR:
                writeToRegister(reg1, readRegister(regA) | readRegister(reg0));
                break;
            case MINUS:
                summation(reg1, readRegister(regA), -1 * readRegister(reg0));
                break;
            case INCR:
                if (value > 127) {
                    ex = 127 - value;
                } else {
                    ex = value;
                }
                summation(regA, readRegister(regA), ex);
                break;
            case SHFT:
                if (value > 127) {
                    ex = value - 127;
                    writeToRegister(regA, (readRegister(regA) >> ex) & 0xFF);
                } else {
                    ex = value;
                    writeToRegister(regA, (readRegister(regA) << ex) & 0xFF);
                }
                break;
        }
    }

    private void summation(int reg2, int valueA, int value1) {
        if (reg2 >= PROTECTED_REGISTER) {
            return;
        }
        int ex = (valueA + value1) & 0xFFFF;
        writeToRegister(reg2, ex & 0xFF);
        if ((reg2 & 0b1) == 1) {
            ex = (ex >> 8) + readRegister(reg2 & 0xFE);
            writeToRegister((reg2 & 0xFE), (ex & 0xFF));
        }
        ex = ex >> 8;
        if (reg2 < 8 && ex != 0) {
            writeToRegister(PROTECTED_REGISTER, readRegister(PROTECTED_REGISTER) | (1 << (4 + (reg2 >> 1))));
        }
    }

}
