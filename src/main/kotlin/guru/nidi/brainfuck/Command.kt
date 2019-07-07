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

import java.io.OutputStream
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE

internal abstract class Command {
    fun OutputStream.writeInt(value: Int) = apply {
        write(value)
        write(value shr 8)
        write(value shr 16)
        write(value shr 24)
    }


    internal abstract fun emit(out: OutputStream, pos: Int)
}

internal abstract class SimpleCommand : Command() {
    abstract val bytes: ByteArray

    override fun emit(out: OutputStream, pos: Int) {
        out.write(bytes)
    }
}

internal class Base : Command() {
    private val LEN_POS1 = 0x44
    private val LEN_POS2 = 0x48
    internal var totalLen: Int = 0

    override fun emit(out: OutputStream, pos: Int) {
        val base = Thread.currentThread().contextClassLoader.getResourceAsStream("base.out")
        val buf = ByteArray(base.available())
        val read = base.read(buf)
        val bout = SimpleByteArrayOutputStream(buf)
        bout.at(LEN_POS1).writeInt(totalLen + read)
        bout.at(LEN_POS2).writeInt(totalLen + read)
        out.write(buf, 0, read)
    }
}

internal class ChangePtr(val value: Int) : Command() {
    override fun emit(out: OutputStream, pos: Int) {
        when (value) {
            in MIN_VALUE..-129 -> out.write(subEcxConst4(-value))
            in -128..-4 -> out.write(addEcxConst1(value))
            in -3..-1 -> (value..-1).forEach { out.write(decEcx) }
            0 -> return
            in 1..3 -> (1..value).forEach { out.write(incEcx) }
            in 4..128 -> out.write(subEcxConst1(-value))
            in 129..MAX_VALUE -> out.write(addEcxConst4(value))
        }
    }
}

internal class ChangeVal(val value: Int) : Command() {
    override fun emit(out: OutputStream, pos: Int) {
        when (value) {
            in MIN_VALUE..-129 -> {
                out.write(add1PtrEcx(-128))
                out.write(add1PtrEcx(value + 128))
            }
            in -128..-2 -> out.write(add1PtrEcx(value))
            -1 -> out.write(dec1PtrEcx)
            0 -> return
            1 -> out.write(inc1PtrEcx)
            in 2..128 -> out.write(subEcxConst1(-value))
            in 129..MAX_VALUE -> {
                out.write(sub1PtrEcx(-128))
                out.write(sub1PtrEcx(128 - value))
            }
        }
    }
}

internal class Write : SimpleCommand() {
    override val bytes = callPtrEdi
}

internal class Read : SimpleCommand() {
    override val bytes = callPtrEsi
}

internal class Ret : SimpleCommand() {
    override val bytes = ret
}

internal class JumpIfZero : Command() {
    var target: Command? = null
    override fun emit(out: OutputStream, pos: Int) {
        out.write(cmp1PtrEcx(0))
        val dist = target - pos + 4
        out.write(if (dist < -128 || dist > 127) jump4IfZero(dist) else jump1IfZero(dist))
    }
}

internal class Jump : Command() {
    var target: Command? = null
    override fun emit(out: OutputStream, pos: Int) {
        val dist = target - pos + 4
        out.write(if (dist < -128 || dist > 127) jump1(dist) else jump1(dist))
    }
}
