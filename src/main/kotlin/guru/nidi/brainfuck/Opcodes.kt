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

internal fun bytes(vararg bs: Int) = ByteArray(bs.size).also {
    bs.forEachIndexed { value, index -> bs[index] = value }
}

val incEcx = bytes(0x41)
val decEcx = bytes(0x49)

fun addEcxConst1(value: Int) = bytes(0x83, 0xc1, value)
fun subEcxConst1(value: Int) = bytes(0x83, 0xe9, value)
fun addEcxConst4(value: Int) = bytes(0x81, 0xc1, value, value shr 8, value shr 16, value shr 24)
fun subEcxConst4(value: Int) = bytes(0x81, 0xe9, value, value shr 8, value shr 16, value shr 24)

val inc1PtrEcx = bytes(0xfe, 0x01)
val dec1PtrEcx = bytes(0xfe, 0x09)
fun add1PtrEcx(value: Int) = bytes(0x80, 0x01, value)
fun sub1PtrEcx(value: Int) = bytes(0x80, 0x29, value)

val callPtrEdi = bytes(0xff, 0x17)
val callPtrEsi = bytes(0xff, 0x16)

val ret = bytes(0xc3)

fun cmp1PtrEcx(value: Int) = bytes(0x80, 0x3e, value)

fun jump1IfZero(value: Int) = bytes(0x74, value)
//TODO is there jump2?
fun jump4IfZero(value: Int) = bytes(0x0f, 0x84, value, value shr 8, value shr 16, value shr 24)

fun jump1(value: Int) = bytes(0xeb, value)
//TODO is there jump2?
fun jump4(value: Int) = bytes(0xe9, value, value shr 8, value shr 16, value shr 24)
