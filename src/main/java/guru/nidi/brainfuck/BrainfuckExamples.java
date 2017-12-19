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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BrainfuckExamples {
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public static List<String> list() {
        try (BufferedReader bfs = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("examples/list.txt")))) {
            return bfs.lines().filter(s -> s.trim().length() > 0).collect(toList());
        } catch (IOException e) {
            throw new RuntimeException("Problem getting list", e);
        }
    }

    public static void writeToFile(String name, File directory) throws IOException {
        final File file = new File(directory, name);
        Files.copy(loader.getResourceAsStream("examples/" + name), file.toPath());
    }
}
