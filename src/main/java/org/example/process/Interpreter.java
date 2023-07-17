package org.example.process;

import java.util.ArrayList;

public class Interpreter {

    private static final int BASE = 2;

    public static String[] getByteCode(String text){
        Structure structure = new Structure();
        String lines = formatterCode(text);
        String [] words = getVariablesValue(lines, structure);
        String[] source = setVariables(words, structure);

        int counter=0;
        String[] bytes;
        String[] byteCode = new String[words.length*2];
        for (String word:source) {
            try {
                bytes = structure.getByteString(word).split("\s+");
            }
            catch (IllegalArgumentException ex){
                // log it
                System.out.println("Error in " + words[counter>>1]);
                throw ex;
            }
            byteCode[counter] = String.format("%04X: %s", counter, bytes[0]);
            counter++;
            byteCode[counter] = String.format("%04X: %s", counter, bytes[1]);
            counter++;
        }
        return byteCode;
    }

    public static String formatterCode(String text) {
        String result = text.strip();
        result = result.replaceAll("[\s\t]+", " ");      // removing white spaces
        result = result.replaceAll("\s*%%.*\n*\s*", "\n");  // removing comments
        result = result.replaceAll("\s*\n+\s*", "\n");
        return result.strip();
    }

    private static String[] getVariablesValue(String text, Structure variableMap) {
        int counter = 0;
        String[] lines = text.strip().split("\n");
        ArrayList<String> result = new ArrayList<>();
        String tempLine;
        for (String line : lines) {
            if (line.startsWith("@")) {
                tempLine = line.substring(1);
                variableMap.addVariable(tempLine, counter, BASE);
                continue;
            }
            result.add(line.strip());
            counter = counter + 2;
        }
        return result.toArray(new String[0]);
    }

    private static String[] setVariables(String[] lines,Structure variableMap) {
        String[] result = new String[lines.length];
        int counter=0;
        for (String line:lines) {
            for (String var : variableMap.getKeySet()) {
                line = line.replaceAll(var, variableMap.getHexCode(var));
            }
            result[counter] = line;
            counter++;
        }
        return result;
    }

    public static int numberConverter(String text) {
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
                    // Log it
                    System.out.println("Wrong hex number " + text);
                    throw nfe;
                }
                break;
            case "0b":
                try {
                    result = Integer.parseInt(text.substring(2), 2);
                } catch (NumberFormatException nfe) {
                    System.out.println("Wrong binary number " + text);
                    throw nfe;
                }
                break;
            default:
                try {
                    result = Integer.parseInt(text);
                } catch (NumberFormatException nfe) {
                    System.out.println("Wrong decimal number " + text);
                    throw nfe;
                }
                break;
        }
        return result;
    }

}
