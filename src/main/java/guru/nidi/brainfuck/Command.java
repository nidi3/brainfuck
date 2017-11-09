package guru.nidi.brainfuck;

import java.io.IOException;
import java.io.OutputStream;

abstract class Command {
     void writeInt(OutputStream out,int value) throws IOException {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    abstract void emit(OutputStream out,int pos) throws IOException;
}

class IncPtr extends Command {
    private static final byte[] OPS = new byte[]{0x41}; //inc ecx

    void emit(OutputStream out,int pos) throws IOException {
        out.write(OPS);
    }
}

class DecPtr extends Command {
    private static final byte[] OPS = new byte[]{0x49}; //dec ecx

    void emit(OutputStream out,int pos) throws IOException {
        out.write(OPS);
    }
}

class IncVal extends Command {
    private static final byte[] OPS = new byte[]{(byte) 0xfe, 0x01}; //inc byte[ecx]

    void emit(OutputStream out,int pos) throws IOException {
        out.write(OPS);
    }
}

class DecVal extends Command {
    private static final byte[] OPS = new byte[]{(byte) 0xfe, 0x09}; //dec byte[ecx]

    void emit(OutputStream out,int pos) throws IOException {
        out.write(OPS);
    }
}

class Call extends Command{
    void emit(OutputStream out, int pos) throws IOException {

    }
}

