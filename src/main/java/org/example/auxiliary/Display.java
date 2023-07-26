package org.example.auxiliary;

import java.util.HashMap;

public class Display {

    private static final String DEFAULT = "\u001B[39;49m";
    private static final int STRING_LENGTH = 50;
    private static final String TERMINATOR= "|";
    private static final String BLACK_ON_GREY = "\u001B[30;47m";

    public static String getFormattedLine(String line){
        int minLength = (STRING_LENGTH/2)-4;
        String textLine = TERMINATOR + " ";
        int emptySpace;
        if(line.length() > (STRING_LENGTH -4)){line = line.substring(0, 45);}
        if (line.length() > minLength){
            emptySpace = STRING_LENGTH - line.length() -4;
            textLine =textLine + line + addSpaces(emptySpace) + " |";
        }
        else {
            emptySpace = minLength - line.length();
            textLine = textLine + line + addSpaces(emptySpace);
            textLine = textLine + " |";
        }
        return textLine;
    }

    public static String[] getFormattedBlock(String[] lines){
        String[] result = new String[lines.length];
        int counter=0;
        for (String line:lines) {
            result[counter] = getFormattedLine(line);
            counter++;
        }
        return result;
    }

    public static void promptString(){
        String startWith = "$ ";
        System.out.print(startWith);
    }

    private static String addSpaces(int n){
        String line = "";
        for(int i = 0 ; i < n ; i++)
        {
            line = line + " ";
        }
        return line;
    }



    public static void printLine(String message){
        System.out.print(BLACK_ON_GREY + message + DEFAULT+"\n");
    }

    public static void printStringArray(String[] message){
        for (String line:message) {
            System.out.print(BLACK_ON_GREY + line + DEFAULT+"\n");
        }
    }
}
