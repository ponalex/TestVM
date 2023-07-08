package org.example.process;

import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter {

    private static HashMap<String, Integer> addressMap = new HashMap<>();

    public Interpreter() {
        String reg = "reg";
        for (int i = 0; i < 16; i++) {
            addressMap.put(reg + Integer.toHexString(i).toUpperCase(), i);
        }

    }

    public static String[] getByteCode(String text){
        String lines = formatterCode(text);
        String [] words = subsAddress(lines);

        int counter=0;
        String[] bytes;
        String[] byteCode = new String[words.length*2];
        for (String word:words) {
            bytes = Interpreter.SetOpcodes.NOP.getOpcode(word).split(" ",2);
            byteCode[counter] = String.format("%04X: %s", counter, bytes[0]);
            counter++;
            byteCode[counter] = String.format("%04X: %s", counter, bytes[1]);
            counter++;
        }
        return byteCode;
    }

    public static String formatterCode(String text) {
        String result = text.strip();
        result = result.replaceAll("[ ]{2,}", " ");
        result = result.replaceAll("\\n[ \\t]+", "\n");
        result = result.replaceAll("[ ]*%%.*\\n", "\n");
        result = result.replaceAll("\\n[ ]*\\n", "\n");
        return result;
    }

    public static String[] subsAddress(String text) {
        int counter = 0;
        String[] lines = text.split("\n");
        ArrayList<String> result = new ArrayList<>();
        String tempLine;
        for (String line : lines) {
            if (line.startsWith("@")) {
                tempLine = line.substring(1);
                addressMap.put(tempLine + ":0", (counter & 0xFF));
                addressMap.put(tempLine + ":1", ((counter >> 8) & 0xFF));
                continue;
            }
            String l = String.format("0x%04X : %s", counter, line);
            result.add(l);
            counter = counter + 2;
        }
        String[] answer = new String[result.size()];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = setAddress(result.get(i));
        }
        return answer;
    }

    private static String setAddress(String text) {
        String[] variables = addressMap.keySet().toArray(new String[0]);
        String result = text;
        int value;
        for (String var : variables) {
            value = addressMap.get(var);
            result = result.replaceAll(var, "0x" + Integer.toHexString(value).toUpperCase());
        }
        return result;
    }

    private static int numberConverter(String text) {
        String line;
        if (text.length()>2){
            line = text.substring(0, 2);
        }
        else{
            line = "";
        }
        int result = 0;
        switch (line) {
            case "0x":
                try {
                    result = Integer.parseInt(text.substring(2), 16);
                } catch (NumberFormatException nfe) {
                    System.out.println("Wrong number " + text);
                    System.exit(-1);
                }
                break;
            case "0b":
                try {
                    result = Integer.parseInt(text.substring(2), 2);
                } catch (NumberFormatException nfe) {
                    System.out.println("Wrong number " + text);
                    System.exit(-1);
                }
                break;
            default:
                String l = text;
                if ( l.contains("-")){l="0";}
                try {
                    result = Integer.parseInt(l);
                } catch (NumberFormatException nfe) {
                    System.out.println("Wrong number " + text);
                    System.exit(-1);
                }
                break;
        }
        return result;
    }

    public enum SetOpcodes {
        NOP {
            @Override
            public String evaluate(String data) {
                String[] lines = data.strip().split(" ");
                String result = "00 00";
                if (lines.length == 2) {
                    lines[0] = String.format("%011X", numberConverter(lines[0]) & 0xF);
                    lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xFF);
                    result = "0" + lines[0] + " " + lines[1];
                }
                if (lines.length == 3) {
                    lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                    lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                    lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                    result = "0" + lines[0] + " " + lines[1] + lines[2];
                }
                return result;
            }
        },
        MVMV {
            @Override
            public String evaluate(String data) {
                String prefix = "1";
                String[] lines = data.strip().split(" ");
                if (lines.length != 2) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = String.format("%02X", numberConverter(lines[1]) & 0xFF);
                return prefix + lines[0] + " " + lines[1];
            }
        },
        MVM {
            @Override
            public String evaluate(String data) {
                String prefix = "2";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        MVRV {
            @Override
            public String evaluate(String data) {
                String prefix = "3";
                String[] lines = data.strip().split(" ");
                if (lines.length != 2) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = String.format("%02X", numberConverter(lines[1]) & 0xFF);
                return prefix + lines[0] + " " + lines[1];
            }
        },
        MVR {
            //4
            @Override
            public String evaluate(String data) {
                String prefix = "4";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        SWP {
            @Override
            public String evaluate(String data) {
                String prefix = "5";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        SPC {
            @Override
            public String evaluate(String data) {
                String prefix = "6";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        CALL {
            @Override
            public String evaluate(String data) {
                String prefix = "7";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("CALL: Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        JMPE {
            @Override
            public String evaluate(String data) {
                String prefix = "8";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        NOT {
            @Override
            public String evaluate(String data) {
                String prefix = "9";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        AND {
            @Override
            public String evaluate(String data) {
                String prefix = "A";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        PLUS {
            @Override
            public String evaluate(String data) {
                String prefix = "B";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        OR {
            @Override
            public String evaluate(String data) {
                String prefix = "C";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        MINUS {
            @Override
            public String evaluate(String data) {
                String prefix = "D";
                String[] lines = data.strip().split(" ");
                if (lines.length != 3) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = Integer.toHexString(numberConverter(lines[1]) & 0xF);
                lines[2] = Integer.toHexString(numberConverter(lines[2]) & 0xF);
                return prefix + lines[0] + " " + lines[1] + lines[2];
            }
        },
        INCR {
            @Override
            public String evaluate(String data) {
                String prefix = "E";
                String[] lines = data.strip().split(" ");
                if (lines.length != 2) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = String.format("%02X", numberConverter(lines[1]) & 0xFF);
                return prefix + lines[0] + " " + lines[1];
            }
        },
        SHFT {
            @Override
            public String evaluate(String data) {
                String prefix = "F";
                String[] lines = data.strip().split(" ");
                if (lines.length != 2) {
                    System.out.println("Error, wrong parameter: " + data);
                    System.exit(-1);
                }
                lines[0] = Integer.toHexString(numberConverter(lines[0]) & 0xF);
                lines[1] = String.format("%02X", numberConverter(lines[1]) & 0xFF);
                return prefix + lines[0] + " " + lines[1];
            }
        };

        public String getOpcode(String line) {
            String command = line.split(" : ", 2)[1];
            String[] operands = command.split(" ", 2);
            String result = "";
            switch (operands[0].toUpperCase()) {
                case "NOP":
                    if (operands.length > 1)
                        result = SetOpcodes.NOP.evaluate(operands[1]);
                    else result = SetOpcodes.NOP.evaluate(" ");
                    break;
                case "MVMV":
                    result = SetOpcodes.MVMV.evaluate(operands[1]);
                    break;
                case "MVM":
                    result = SetOpcodes.MVM.evaluate(operands[1]);
                    break;
                case "MVRV":
                    result = SetOpcodes.MVRV.evaluate(operands[1]);
                    break;
                case "MVR":
                    result = SetOpcodes.MVR.evaluate(operands[1]);
                    break;
                case "SWP":
                    result = SetOpcodes.SWP.evaluate(operands[1]);
                    break;
                case "SPC":
                    result = SetOpcodes.SPC.evaluate(operands[1]);
                    break;
                case "CALL":
                    result = SetOpcodes.CALL.evaluate(operands[1]);
                    break;
                case "JMPE":
                    result = SetOpcodes.JMPE.evaluate(operands[1]);
                    break;
                case "NOT":
                    result = SetOpcodes.NOT.evaluate(operands[1]);
                    break;
                case "AND":
                    result = SetOpcodes.AND.evaluate(operands[1]);
                    break;
                case "PLUS":
                    result = SetOpcodes.PLUS.evaluate(operands[1]);
                    break;
                case "OR":
                    result = SetOpcodes.OR.evaluate(operands[1]);
                    break;
                case "MINUS":
                    result = SetOpcodes.MINUS.evaluate(operands[1]);
                    break;
                case "INCR":
                    result = SetOpcodes.INCR.evaluate(operands[1]);
                    break;
                case "SHFT":
                    result = SetOpcodes.SHFT.evaluate(operands[1]);
                    break;
                default:
                    System.out.println("Wrong opcode: " + operands[0]);
                    System.exit(-1);
            }
            return result.toUpperCase();
        }

        public abstract String evaluate(String opcodes);
    }
}
