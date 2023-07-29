package org.example.parser;

import java.util.*;

public class Interpreter {

    private static final int BASE = 2;

    public static String[] translateCommandToCode(String text, HashMap<String, Integer> variableMap) {
        String lines = formatterCode(text);
        String[] commands = getShiftOfFunction(lines, variableMap, BASE);
        commands = Arrays.stream(commands).map(w -> substituteVariables(w, variableMap)).toArray(String[]::new);

        return splitByteString(commands).toArray(new String[0]);
    }

    public static List<String> splitByteString(String[] lines){
        int counter = 0;
        String[] bytes;
        List<String> byteCode = new ArrayList<>();
        for (String word : lines) {
            try {
                bytes = convertStringToCode(word).split("\s+");
            } catch (IllegalArgumentException ex) {
                // log it
                System.out.println("Error in " + lines[counter >> 1]);
                throw ex;
            }
            byteCode.add(String.format("%04X: %s", counter, bytes[0]));
            counter++;
            byteCode.add(String.format("%04X: %s", counter, bytes[1]));
            counter++;
        }
        return byteCode;
    }

    public static String formatterCode(String text) {
        String result = text.strip();
        result = removeSpaces(result);      // removing white multiple spaces
        result = removeComments(result);
        result = removeEmptyStrings(result);
        return result.strip();
    }

    private static String removeSpaces(String text) {
        return text.replaceAll("[\s\t]+", " ");
    }

    private static String removeComments(String text) {
        return text.replaceAll("\s*(%%)+.*\n*", "\n");
    }

    private static String removeEmptyStrings(String text) {
        return text.replaceAll("(\s*\n+)+\s*", "\n");
    }

    public static String[] getShiftOfFunction(String text,
                                              HashMap<String, Integer> variableMap,
                                              int base) {
        int counter = 0;
        String[] lines = text.strip().split("\n");
        ArrayList<String> result = new ArrayList<>();
        String tempLine;
        for (String line : lines) {
            if (line.startsWith("@")) {
                tempLine = line.substring(1);
                if (variableMap.containsKey(tempLine + ":0")) {
                    throw new IllegalArgumentException(
                            String.format("%s function have been already defined", tempLine));
                }
                addVariableToMap(tempLine,counter,base,variableMap);
                continue;
            }
            result.add(line.strip());
            counter = counter + 2;
        }
        return result.toArray(new String[0]);
    }


    private static void addVariableToMap(
            String variableName,
            int variableValue,
            int length,
            HashMap<String, Integer> variableMap) {
        String tempName;
        int tempValue;
        for (int i = 0; i < length; i++) {
            tempName = variableName + ":" + i;
            tempValue = (variableValue >> (i * 8)) & 0xFF;
            variableMap.put(tempName, tempValue);
        }
    }
    public static String substituteVariables(String line,
                                             HashMap<String, Integer> variableMap) {
        Iterator<String> iterator = variableMap.keySet().iterator();
        String var;
        String value;
        while (iterator.hasNext()) {
            var=iterator.next();
            value = "0x" + Integer.toHexString(variableMap.get(var)).toUpperCase();
            line = line.replaceAll(var, value);
        }
        return line;
    }

    public static int numberConverter(String text) {
        int result;
        if (text.length() <= 2) {
            result = Integer.parseInt(text);
        } else {
            if (text.startsWith("0b")) {
                result = Integer.getInteger(text, 2);
            } else {
                result = Integer.decode(text);
            }
        }
        return result;
    }

    public static String convertStringToCode(String command) throws IllegalArgumentException {
        String[] words = command.split("\s+", 2);
        String result;
        Opcodes opcodes;
        opcodes = Opcodes.valueOf(words[0]);
        try {
            result = opcodes.getType().parsing(words[1]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            result = opcodes.getType().parsing("");
        }
        return opcodes.getOpcode() + result;
    }

}
