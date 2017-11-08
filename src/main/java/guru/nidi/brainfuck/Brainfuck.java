/*
 * Copyright © 2017 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.brainfuck;

import java.io.*;
import java.util.Stack;

public class Brainfuck {
    private static final int LEN_POS1 = 0x44;
    private static final int LEN_POS2 = 0x48;
    private static final int READ_POS = 0xc3;
    private static final int WRITE_POS = 0xd7;
    private static final byte[] DEC_PTR = new byte[]{0x4e}; //dec esi
    private static final byte[] INC_PTR = new byte[]{0x46}; //inc esi
    private static final byte[] DEC_VAL = new byte[]{(byte) 0xfe, 0x0e}; //dec byte[esi]
    private static final byte[] INC_VAL = new byte[]{(byte) 0xfe, 0x06}; //inc byte[esi]
    //could be optimized with short jump if possible
    private static final byte[] JZ = new byte[]{(byte) 0x80, 0x3e, 0x00, 0x0f, (byte) 0x84}; //cmp byte[esi],0; je + 4 bytes relative
    //could be optimized with short jump if possible
    private static final byte[] JUMP = new byte[]{(byte) 0xe9}; //jmp + 4 bytes relative
    private static final byte[] CALL = new byte[]{(byte) 0xe8}; //call + 4 bytes relative dest
    private static final byte[] RET = new byte[]{(byte) 0xc3}; //ret

    private RandomAccessFile out;

    public static void main(String[] args) throws IOException {
        new Brainfuck().compile(args[0], new File("a.out"));
    }

    public void compile(String in, File file) throws IOException {
        final Stack<Integer> loops = new Stack<>();
        file.delete();
        out = new RandomAccessFile(file, "rw");
        writeBase();
        for (byte b : in.getBytes()) {
            switch (b) {
                case '<':
                    out.write(DEC_PTR);
                    break;
                case '>':
                    out.write(INC_PTR);
                    break;
                case '-':
                    out.write(DEC_VAL);
                    break;
                case '+':
                    out.write(INC_VAL);
                    break;
                case '.':
                    out.write(CALL);
                    writeInt(WRITE_POS - 4 - (int) out.length());
                    break;
                case ',':
                    out.write(CALL);
                    writeInt(READ_POS - 4 - (int) out.length());
                    break;
                case '[':
                    out.write(JZ);
                    loops.push((int) out.length());
                    writeInt(0);
                    break;
                case ']':
                    if (loops.isEmpty()) {
                        throw new IllegalStateException("] without [");
                    }
                    final int target = loops.pop();
                    out.seek(target);
                    writeInt((int) out.length() - target + 1);
                    out.seek(out.length());
                    out.write(JUMP);
                    writeInt(target - (int) out.length() - 9);
                    break;
            }
        }
        if (!loops.isEmpty()) {
            throw new IllegalStateException("Unclosed [");
        }
        out.write(RET);
        out.seek(LEN_POS1);
        writeInt((int) out.length());
        out.seek(LEN_POS2);
        writeInt((int) out.length());
        out.close();
    }

    private void writeBase() throws IOException {
        final InputStream base = Thread.currentThread().getContextClassLoader().getResourceAsStream("base.out");
        final byte[] buf = new byte[base.available()];
        final int read = base.read(buf);
        out.write(buf, 0, read);
    }

    private void writeInt(int value) throws IOException {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

}
