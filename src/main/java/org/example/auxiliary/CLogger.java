package org.example.auxiliary;

import java.util.logging.*;

import static java.util.logging.Level.*;

public class CLogger {

    public static Logger logger = Logger.getGlobal();
    public static Formatter formatter = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String result = "";
            int s = record.getLevel().intValue();
            if (s > 950) {
                result = "\u001B[31m[ERROR] " + ": " + record.getMessage() + "\n";
            } else if (s > 750) {
                result = "\u001B[33m[WARNING] " + record.getMessage() + "\n";
            } else {
                result = "\u001B[30;47m" + record.getMessage() + "\u001B[49m\n";
            }
            return result;
        }
    };

    public static void setLoggerLevel(Level logLevel) {
        logger.setLevel(logLevel);
        Handler[] handlers = logger.getParent().getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(logLevel);
            handler.setFormatter(formatter);
        }
    }

    public static void printPC(int PC, int IR) {
        // Lenght of string 45 symbols
        String message = "| Programm counter:      0x %16X |";
        logger.log(FINER, String.format(message, PC));
        message =        "| Instruction register:  0b %16s |";
        String ir = Integer.toBinaryString(IR);
        String result = String.format(message, ir);
        logger.log(FINER, result);
        //               | Program counter:      0x             00EF |
        //               | Instruction register: 0b 1000010111110000 |
    }

    public static void printVariable(
            int opcode,
            int regA,
            int reg0,
            int reg1,
            int expandedA,
            int value,
            int address
    ) {
//                      | Program counter:       0x             00EF |
        String[] line = new String[5];
        line[0] = "| OPCODE | RegisterA | Register0 | Register1 |";
        logger.log(FINER, line[0]);
        line[1] = "| 0x %3X | 0b %6s | 0b %6s | 0b %6s |";
        String registerA = Integer.toBinaryString(regA);
        String register0 = Integer.toBinaryString(reg0);
        String register1 = Integer.toBinaryString(reg1);
        String message = String.format(line[1], opcode, registerA, register0, register1);
        logger.log(FINER, message);
        line[2] = "| Address(regA) | 0x %23s |";
        message = String.format(line[2], Integer.toHexString(expandedA));
        logger.log(FINER, message);
        line[3] = "| Address(0:1)  | 0x %23s |";
        message = String.format(line[3], Integer.toHexString(address));
        logger.log(FINER, message);
        line[4] = "| Value:        | 0x %23s |";
        message = String.format(line[4], Integer.toHexString(value));
        logger.log(FINER, message);

    }

    public static void printTypeZero(String message){
        String template = "| %5s:                                     |";
        logger.log(FINER, String.format(template, message));
    }

    public static void printTypeOne(String message, int expandedA, int value){
        //                  | OPCODE | RegisterA | Register0 | Register1 |
        String template =  "| %5s: 0x%4X | 0x%24x |";
        logger.log(FINER, String.format(template, message , expandedA, value ));
    }

    public static void printTypeTwo(String message, int reg0, int reg1){
        //                  | OPCODE | RegisterA | Register0 | Register1 |
        String template =  "| %5s:        |           -    0x%2X : 0x%2x |";
        logger.log(FINER, String.format(template, message , reg0, reg1 ));
    }

    public static void printTypeThree(String message, int regA, int reg0, int reg1){
        //                  | OPCODE | RegisterA | Register0 | Register1 |
        String template =  "| %5s:        |        0x%4X  0x%2X : 0x%2X |";
        logger.log(FINER, String.format(template, message , regA, reg0, reg1 ));
    }

    public static void printOut(String message) {
        logger.log(FINEST, message);
    }

    public static void printWarning(String message) {
        logger.log(WARNING, message);
    }

    public static void printError(String message) {
        logger.log(SEVERE, message);
    }
}
