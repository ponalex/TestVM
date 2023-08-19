package org.example.process;


import org.example.auxiliary.Display;
import org.example.auxiliary.SimpleLogger;
import org.example.parser.Interpreter;
import org.example.parser.SpecialSymbols;

import java.util.*;

public class CommandController {

    private static Controller controller;

    public CommandController(CPU cpu) {
        controller = new Controller(cpu);
    }

    public void sendCommand(String command) {
        List<String> operands = Interpreter.getLexeme(command);
        try {
            operands = calculate(operands);
        }
        catch (NumberFormatException nfe){
            SimpleLogger.printWarning("wrong format!");
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            SimpleLogger.printWarning("One of the index out of bounds!");
        }
        for (String line:operands) {
            Display.printLine(Display.getFormattedLine(line));
        }
    }

    public List<String> calculate(List<String> command) {
        Stack<String> commands  = new Stack<>();
        Stack<String> arguments = new Stack<>();
        for (String line:command) {
            if(controller.isSupplier(line)){
                arguments.push(controller.callSupplier(line));
                continue;
            }
            if(controller.isFunction(line)){
                commands.push(line);
                continue;
            }
            if(SpecialSymbols.RIGHT_BRACKET.equals(line)){
                callFunction(commands.pop(), arguments);
                continue;
            }
            arguments.push(line);
        }
        return new ArrayList<>(arguments);
    }

    private void callFunction(String funcName, Stack<String> arguments){
        List<String> variables = new ArrayList<>();
        String temp ="]";
        while (!arguments.isEmpty()){
            temp = arguments.pop();
            if(SpecialSymbols.LEFT_BRACKET.equals(temp))break;
            variables.add(temp);
        }
        Collections.reverse(variables);
        variables = controller.callFunction(funcName, variables);
        for (String el:variables) {
            arguments.push(el);
        }
    }

    public boolean getStatus(){
        return controller.getStatus();
    }

    public void sendToCPU(String text){

    }

    public String readFromCPU(IOBuffer cpuBuffer, CPU cpu){


        //  Проверить что есть в процессоре для того что бы прочитать.
        //      Если есть то прочитать и вывести на экран
        //      Если нет то проверить есть ли что в буфере
        //          Если есть что то в буфере то записать в регистр

        return "Hello world!";
    }

}
