package org.example.process;

public enum Opcodes {
    NOP(0){
        @Override
        public int getType(){return 0;}
    },MVMV(1){
        @Override
        public int getType(){return 1;}
    },MVM(2){
        @Override
        public int getType(){return 3;}
    },MVRV(3){
        @Override
        public int getType(){return 1;}
    },MVR(4){
        @Override
        public int getType(){return 3;}
    },SWP(5){
        @Override
        public int getType(){return 2;}
    },SPC(6){
        @Override
        public int getType(){return 2;}
    },CALL(7){
        @Override
        public int getType(){return 2;}
    },JMPE(8){
        @Override
        public int getType(){return 2;}
    },NOT(9){
        @Override
        public int getType(){return 2;}
    },AND(10){
        @Override
        public int getType(){return 3;}
    },PLUS(11){
        @Override
        public int getType(){return 3;}
    },OR(12){
        @Override
        public int getType(){return 3;}
    },MINUS(13){
        @Override
        public int getType(){return 3;}
    },INCR(14){
        @Override
        public int getType(){return 1;}
    },SHFT(15){
        @Override
        public int getType(){return 1;}
    };

    private int opcode;
    Opcodes(int opcode) {
        this.opcode = opcode;
    }

    public String getOpcode(){
        return Integer.toHexString(this.opcode);
    }

    public String parseTypeZero(String line){
        return "0 00";
    }

    public String parseTypeOne(String line){
        String[] command = line.split("\s+",2);
        int firstNumber = Interpreter.numberConverter(command[0]);
        int secondNumber = Interpreter.numberConverter(command[1]);
        String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
        String secondString = Integer.toHexString(secondNumber & 0xFF).toUpperCase();
        return firstString + " " + secondString;
    }

    public String parseTypeTwo(String line){
        String[] command = line.split("\s+",2);
        int firstNumber = Interpreter.numberConverter(command[0]);
        int secondNumber = Interpreter.numberConverter(command[1]);
        String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
        String secondString = Integer.toHexString(secondNumber & 0xF).toUpperCase();
        return "0 " + firstString + secondString;
    }

    public String parseTypeThree(String line){
        String[] command = line.split("\s+",3);
        int firstNumber = Interpreter.numberConverter(command[0]);
        int secondNumber = Interpreter.numberConverter(command[1]);
        int thirdNumber = Interpreter.numberConverter(command[2]);
        String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
        String secondString = Integer.toHexString(secondNumber & 0xF).toUpperCase();
        String thirdString = Integer.toHexString(thirdNumber & 0xF).toUpperCase();
        return  firstString + " " + secondString + thirdString;
    }

    public abstract int getType();
}
