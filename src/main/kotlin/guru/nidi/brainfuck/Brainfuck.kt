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
package guru.nidi.brainfuck

import java.io.*
import java.util.*

internal class Brainfuck(val out: OutputStream) {

    fun compile(source: String) {
        val loops = Stack<Int>()
        val commands = ArrayList<Command>()
        var i = 0
        while (i < source.length) {
            when (source[i]) {
                '<', '>' -> {
                    var value = 0
                    do {
                        value += if (source[i] == '<') -1 else +1
                        i++
                    } while (i < source.length && (source[i] == '<' || source[i] == '>'))
                    commands.add(ChangePtr(value))
                }
                '-', '+' -> {
                    var value = 0
                    do {
                        value += if (source[i] == '-') -1 else +1
                        i++
                    } while (i < source.length && (source[i] == '-' || source[i] == '+'))
                    commands.add(ChangeVal(value))
                }
                '.' -> {
                    commands.add(Write())
                    i++
                }
                ',' -> {
                    commands.add(Read())
                    i++
                }
                else -> i++
            }
            //                    out.write(JZ);
            //                    loops.push((int) out.length());
            //                    writeInt(0);
            //                    break;
            //                case ']':
            //                    if (loops.isEmpty()) {
            //                        throw new IllegalStateException("] without [");
            //                    }
            //                    final int target = loops.pop();
            //                    out.seek(target);
            //                    writeInt((int) out.length() - target + 1);
            //                    out.seek(out.length());
            //                    out.write(JUMP);
            //                    writeInt(target - (int) out.length() - 9);
            //                    break;
        }
        //        if (!loops.isEmpty()) {
        //            throw new IllegalStateException("Unclosed [");
        //        }
        commands.add(Ret())
        val baos = ByteArrayOutputStream()
        for (command in commands) {
            command.emit(baos, 0)
        }
        val prg = baos.toByteArray()
        Base().apply {
            totalLen = prg.size
            emit(out, 0)
        }
        out.write(prg)
    }
}

fun compile(source: String, output: File) {
    output.delete()
    try {
        FileOutputStream(output).use { out -> Brainfuck(out).compile(source) }
    } catch (e: Exception) {
        output.delete()
        throw e
    }

    output.setExecutable(true, false)
}