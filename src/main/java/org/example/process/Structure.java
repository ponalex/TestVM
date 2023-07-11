package org.example.process;

import java.util.HashMap;
import java.util.Set;

public class Structure {

    private final HashMap<String, Integer> variableMap;
    private final static int registerNumber=16;
    private final static String registerSuffix="reg";

    public Structure() {
        variableMap = new HashMap<>(16);
        String temp;
        for (int i = 0; i < registerNumber; i++){
            temp = registerSuffix+Integer.toHexString(i).toUpperCase();
            variableMap.put(temp, i);
        }
    }

    public Set<String> getKeySet() {
        return variableMap.keySet();
    }

    public String getHexCode(String line) {
        Integer result = variableMap.get(line);
        return "0x" + Integer.toHexString(result);
    }

    public void addVariable(
            String variableName,
            int variableValue,
            int length){
        String tempName;
        int tempValue;
        for(int i = 0; i< length; i++){
            tempName= variableName + ":" + i;
            tempValue = (variableValue >> (i * 8)) & 0xFF;
            variableMap.put(tempName, tempValue);
        }
    }

    public String getByteString(String command) throws IllegalArgumentException {
        String[] words = command.split("\s+", 2);
        String result;
        Opcodes opcodes;
        opcodes = Opcodes.valueOf(words[0]);
        int type = opcodes.getType();
        result = opcodes.getOpcode();
        if (type == 0) {
            if(words.length>1){throw new IllegalArgumentException();}
            result = result + opcodes.parseTypeZero("");
        } else if (type == 1) {
            result = result + opcodes.parseTypeOne(words[1]);
        } else if (type == 2) {
            result = result + opcodes.parseTypeTwo(words[1]);
        } else if (type == 3) {
            result = result + opcodes.parseTypeThree(words[1]);
        }
        return result;
    }

}
