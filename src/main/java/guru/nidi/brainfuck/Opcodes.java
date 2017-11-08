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

class Opcodes {
    //jumps could be optimized with short jump if possible
    static final byte[]
            DEC_PTR = new byte[]{0x4e}, //dec esi
            INC_PTR = new byte[]{0x46}, //inc esi
            DEC_VAL = new byte[]{(byte) 0xfe, 0x0e}, //dec byte[esi]
            INC_VAL = new byte[]{(byte) 0xfe, 0x06}, //inc byte[esi]
            JZ = new byte[]{(byte) 0x80, 0x3e, 0x00, 0x0f, (byte) 0x84}, //cmp byte[esi],0; je + 4 bytes relative
            JUMP = new byte[]{(byte) 0xe9}, //jmp + 4 bytes relative
            CALL = new byte[]{(byte) 0xe8}, //call + 4 bytes relative dest
            RET = new byte[]{(byte) 0xc3}; //ret
}
