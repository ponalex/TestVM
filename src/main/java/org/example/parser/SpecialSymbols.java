package org.example.parser;

public enum SpecialSymbols {
    LEFT_BRACKET,
    RIGHT_BRACKET,
    DELIMITER,
    QUOTE,
    OTHER;

    public static SpecialSymbols getType(char symbol){
        SpecialSymbols result=OTHER;
        switch (symbol){
            case '[': result = LEFT_BRACKET; break;
            case ']': result = RIGHT_BRACKET; break;
            case ',': result =DELIMITER ; break;
            case '"': result = QUOTE; break;
        }
        return result;
    }

    public boolean equals(SpecialSymbols value){
        return (this==value);
    }

    public boolean equals(String text){
        if(text.length()>1) return false;
        return (getType(text.charAt(0))==this);
    }
}
