package guru.nidi.brainfuck;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamplesTest {
    @Test
    void list() {
        assertEquals(BrainfuckExamples.list().size(), 21);
    }

    @Test
    void writeToFile() throws IOException {
        String name = "beep.bf";
        final File target = new File("target");
        new File(target, name).delete();
        BrainfuckExamples.writeToFile(name, target);
        assertTrue(new File(target, name).length() == 8);
    }
}
