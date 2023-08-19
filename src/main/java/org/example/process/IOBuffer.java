package org.example.process;

import org.example.auxiliary.Display;


import java.nio.charset.StandardCharsets;
import java.util.*;

public class IOBuffer implements Runnable {

    private final Thread process;
    private static CPU cpu;
    private static Queue<Byte> inputBuffer;
    private static Queue<Byte> outputBuffer;

    private static final int keyRegister = 0xE;
    private static final int dataRegister = 0xF;
    private static final int registerMask = 3;
    private static final int WAS_READED = 0;
    private static final int WAS_WRITTEN = 3;

    public IOBuffer(CPU processor) {
        inputBuffer = new LinkedList<>();
        outputBuffer = new LinkedList<>();
        cpu = processor;
        process = new Thread(cpu);
        process.start();
    }

    private String getString() {
        String result = "";
//        result = outputBuffer.stream().map(p -> Character.toString(p & 0xFF)).collect(Collectors.joining());
        while (!outputBuffer.isEmpty()) {
            result = result + Character.toString(outputBuffer.poll());
        }
        return result;
    }

    public synchronized void writeToBuffer(String message) {
        byte[] temp = message.getBytes(StandardCharsets.US_ASCII);
        for (byte el : temp) {
            inputBuffer.add(el);
        }
        inputBuffer.add((byte) 0);
    }

    public synchronized boolean isAlive() {
        return process.isAlive();
    }

    private void writeToCPU(int status) {
        int newFlag = ((status & (~registerMask)) & 0xFF) + WAS_WRITTEN;
        int data = Byte.toUnsignedInt(inputBuffer.poll());
        cpu.writeToRegister(dataRegister, data);
        cpu.writeToRegister(keyRegister, newFlag);
    }

    private void readFromCPU(int status) {
        int newFlag = (status & (0xFF - registerMask));
        int byteData = 0;
        byteData = (cpu.readRegister(dataRegister) & 0xFF);
        cpu.writeToRegister(keyRegister, newFlag);
        if (byteData == 0) {
            String outMessage;
            outMessage = getString();
            outMessage = Display.getFormattedLine(outMessage);
            Display.printLine(outMessage);
            return;
        }
        outputBuffer.add((byte) byteData);
    }

    public void run() {
        int status;
        while (process.isAlive()) {
            //synchronized (this) {
            status = cpu.readRegister(keyRegister);
            //}
            if ((status & registerMask) == 1) {
                readFromCPU(status);
            }
            if ((status & registerMask) == 2) {
                if (inputBuffer.isEmpty()) {
                    continue;
                }
                writeToCPU(status);
            }
        }
    }
}
