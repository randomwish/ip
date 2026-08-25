package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import bro.task.ToDos;
import org.junit.jupiter.api.Test;

/** Verifies that Ui owns console input and output behavior. */
class UiTest {
    /** Reading a command trims the same surrounding whitespace as the application did before extraction. */
    @Test
    void readCommand_trimsInput() {
        Ui ui = new Ui(new Scanner("  list  \n"), new PrintStream(new ByteArrayOutputStream()));

        assertEquals("list", ui.readCommand());
    }

    /** Adding a todo preserves the existing output, including its established task-count spacing. */
    @Test
    void showTaskAdded_preservesTodoOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showTaskAdded(new ToDos("read book"), 1);

        assertEquals("Got it. I've added: \n\n[T] [ ] read book\nNow you have1 tasks in the list\n",
                output.toString());
    }
}
