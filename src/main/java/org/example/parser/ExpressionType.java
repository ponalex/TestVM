package org.example.parser;

import org.example.parser.Interpreter;

public enum ExpressionType {
    TYPE_ZERO {
        @Override
        public String parsing(String line) {
            return "0 00";
        }
    },
    TYPE_ONE {
        @Override
        public String parsing(String line) {
            String[] command = line.split("\s+",2);
            int firstNumber = Interpreter.numberConverter(command[0]);
            int secondNumber = Interpreter.numberConverter(command[1]);
            String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
            String secondString = Integer.toHexString(secondNumber & 0xFF).toUpperCase();
            return firstString + " " + secondString;
        }
    },
    TYPE_TWO {
        @Override
        public String parsing(String line) {
            String[] command = line.split("\s+",2);
            int firstNumber = Interpreter.numberConverter(command[0]);
            int secondNumber = Interpreter.numberConverter(command[1]);
            String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
            String secondString = Integer.toHexString(secondNumber & 0xF).toUpperCase();
            return "0 " + firstString + secondString;
        }
    },
    TYPE_THREE {
        @Override
        public String parsing(String line) {
            String[] command = line.split("\s+",3);
            int firstNumber = Interpreter.numberConverter(command[0]);
            int secondNumber = Interpreter.numberConverter(command[1]);
            int thirdNumber = Interpreter.numberConverter(command[2]);
            String firstString = Integer.toHexString(firstNumber & 0xF).toUpperCase();
            String secondString = Integer.toHexString(secondNumber & 0xF).toUpperCase();
            String thirdString = Integer.toHexString(thirdNumber & 0xF).toUpperCase();
            return  firstString + " " + secondString + thirdString;
        }
    };

    public abstract String parsing(String command);
}