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

import static guru.nidi.brainfuck.BaseDependent.*;
import static guru.nidi.brainfuck.Opcodes.*;

public class Brainfuck {
    private final RandomAccessFile out;

    private Brainfuck(RandomAccessFile out) {
        this.out = out;
    }

    public static void main(String[] args) throws IOException {
        compile(args[0], new File("a.out"));
    }

    public static void compile(String source, File output) throws IOException {
        output.delete();
        try (RandomAccessFile out = new RandomAccessFile(output, "rw")) {
            new Brainfuck(out).compile(source);
        } catch (Exception e) {
            output.delete();
            throw e;
        }
        output.setExecutable(true,false);
    }

    private void compile(String source) throws IOException {
        final Stack<Integer> loops = new Stack<>();
        writeBase();
        for (byte b : source.getBytes()) {
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
        adjustLength();
    }

    private void writeBase() throws IOException {
        final InputStream base = Thread.currentThread().getContextClassLoader().getResourceAsStream("base.out");
        final byte[] buf = new byte[base.available()];
        final int read = base.read(buf);
        out.write(buf, 0, read);
    }

    private void adjustLength() throws IOException {
        out.seek(LEN_POS1);
        writeInt((int) out.length());
        out.seek(LEN_POS2);
        writeInt((int) out.length());
    }

    private void writeInt(int value) throws IOException {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

}
