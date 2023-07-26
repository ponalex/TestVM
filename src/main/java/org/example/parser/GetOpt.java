package org.example.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetOpt {
    public static List<String> getCommands(String[] args, String pattern) {
        List<String> commands = new ArrayList<>();
        for (int i =0 ; i < pattern.length() ; i++) {
            commands.add(i,"");
        }
        String prefix = "-";
        int position=0;
        for (String line : args) {
            if (line.startsWith(prefix)) {
                position = charsToList(line.substring(1), pattern, commands);
            } else {
                commands.add(position, line);
            }
        }
        commands = commands.stream().filter(l -> !l.isEmpty()).collect(Collectors.toList());
        return commands;
    }

    private static int charsToList(
            String line,
            String pattern,
            List<String> commands) {
        String text = line;
        String symbol;
        String prefix = "-";
        int position = 0;
        while (text.length() > 0) {
            symbol = text.substring(0, 1);
            text = text.substring(1);
            position = pattern.indexOf(symbol);
            if (position >= 0) {
                commands.add(position, prefix + symbol);
                if (text.length() > 0) {
                    if ((position + 1) < pattern.length() && pattern.charAt(position + 1) == ':') {
                        throw new NullPointerException(
                                String.format("Wrong format of command: %s", line)
                        );
                    }
                }
            } else {
                throw new NullPointerException("There is no such key. Please use -h for help");
            }
        }
        return position+1;
    }

}
