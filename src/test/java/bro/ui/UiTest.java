package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import bro.task.Events;
import bro.task.TaskList;
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

    /** Error output includes the supplied message and the standard visual border. */
    @Test
    void showError_message_includesBorderAndText() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showError("Something went wrong.");

        String renderedOutput = output.toString();
        assertTrue(renderedOutput.contains("Something went wrong."));
        assertTrue(renderedOutput.contains("____________________________________________________________"));
    }

    /** A task list is rendered in insertion order with one-based display numbering. */
    @Test
    void showTaskList_tasks_printsNumberedTasksInOrder() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new Events("2pm", "4pm", "project meeting"));

        ui.showTaskList(tasks);

        assertEquals("1. [T] [ ] read book\n2. [E] [ ] project meeting(from: 2pm to: 4pm)\n",
                output.toString());
    }

    /** Status output reflects a task's completion marker and description. */
    @Test
    void showTaskStatusChanged_completedTask_printsStatusAndTask() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));
        ToDos task = new ToDos("read book");
        task.setDone(true);

        ui.showTaskStatusChanged(true, task);

        assertEquals("Ok this item is marked!\n[X] read book\n", output.toString());
    }

    /** The session closing method emits the expected goodbye line. */
    @Test
    void showGoodbye_sessionEnds_printsGoodbye() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showGoodbye();

        assertEquals("Goodbye!\n", output.toString());
    }
}
