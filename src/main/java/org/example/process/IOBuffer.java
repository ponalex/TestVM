package org.example.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class IOBuffer {
    private static int bufferSize;
    private static Byte[] buffer;
    private static boolean input;

    private static Queue<Byte> bufferQueue;

    private static final int keyRegister = 0xE;
    private static final int dataRegister = 0xF;
    private static final int writeBit = 2;
    private static final int readyBit = 1;

    public IOBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new Byte[bufferSize];
        bufferQueue = new LinkedList<>();
    }

    public void writeBuffer(Byte[] line, CPU processor) {
        buffer = Arrays.stream(line).limit(bufferSize).toArray(Byte[]::new);
        bufferQueue.addAll(Arrays.asList(line));
        while (bufferQueue.peek() != null) {
            synchronized (this) {
                if ((processor.readRegister(keyRegister) & writeBit) == 2) {
                    if ((processor.readRegister(keyRegister) & readyBit) == 0) {
                        processor.writeToRegister(dataRegister, Byte.toUnsignedInt(bufferQueue.poll()));
                        int regE = processor.readRegister(keyRegister) | readyBit;
                        processor.writeToRegister(keyRegister, regE);
                    }
                }
            }
        }
    }

    public String readBuffer(CPU processor) {
        int temp = 0;
        String result = "";
        while (temp != 3) {
            synchronized (this) {
// READING data
                if ((processor.readRegister(keyRegister) & writeBit) == 0) {
                    if ((processor.readRegister(keyRegister) & readyBit) == 1) {
                        temp = processor.readRegister(dataRegister);
                        result = result + ((char) temp);
                        int regE = processor.readRegister(keyRegister) | readyBit;
                        processor.writeToRegister(keyRegister, regE);
                    }
                }
            }
        }
        return result;
    }

}
