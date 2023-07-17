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
        int result;
        if(text.length()<=2){
            result = Integer.parseInt(text);
        }
        else{
            if(text.startsWith("0b")){
                result= Integer.getInteger(text, 2);
            }else{
                result=Integer.decode(text);
            }
        }
        return result;
    }

}
