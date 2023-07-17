package org.example.process;

import static org.example.process.ExpressionType.*;

public enum Opcodes {
    NOP(0, ExpressionType.TYPE_ZERO),
    MVMV(1, TYPE_ONE),
    MVM(2, TYPE_THREE),
    MVRV(3, TYPE_ONE),
    MVR(4, TYPE_THREE),
    SWP(5, TYPE_TWO),
    SPC(6, TYPE_TWO),
    CALL(7, TYPE_TWO),
    JMPE(8, TYPE_THREE),
    NOT(9, TYPE_TWO),
    AND(10, TYPE_THREE),
    PLUS(11, TYPE_THREE),
    OR(12, TYPE_THREE),
    MINUS(13, TYPE_THREE),
    INCR(14, TYPE_ONE),
    SHFT(15, TYPE_ONE);

    private int opcode;
    private ExpressionType expressionType;

    Opcodes(int opcode, ExpressionType expressionType) {
        this.opcode = opcode;
        this.expressionType = expressionType;
    }

    public String getOpcode() {
        return Integer.toHexString(this.opcode);
    }

    public Opcodes getOpcode(int code) {
        switch (code){
            case 0: return NOP;
            case 1: return MVMV;
            case 2: return MVM;
            case 3: return MVRV;
            case 4: return MVR;
            case 5: return SWP;
            case 6: return SPC;
            case 7: return CALL;
            case 8: return JMPE;
            case 9: return NOT;
            case 0xA: return AND;
            case 0xB: return PLUS;
            case 0xC: return OR;
            case 0XD: return MINUS;
            case 0xE: return INCR;
            case 0xF: return SHFT;
        }
        return NOP;
    }

    public ExpressionType getType(){
        return this.expressionType;
    }
}
