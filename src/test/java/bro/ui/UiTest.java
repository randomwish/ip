package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
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

    /** Adding a todo uses Bro's friendly task-tracking phrase and singular count. */
    @Test
    void showTaskAdded_todo_usesBroStyleOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showTaskAdded(new ToDos("read book"), 1);

        assertEquals("Nice, bro — I've logged:\n\n[T] [ ] read book\nBro is keeping tabs on 1 task.\n",
                output.toString());
    }

    /** Error output includes Bro's voice, the supplied message, and the visual border. */
    @Test
    void showError_message_includesBorderAndText() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showError("Something went wrong.");

        String renderedOutput = output.toString();
        assertTrue(renderedOutput.contains("Something went wrong."));
        assertTrue(renderedOutput.contains("Bro says: Something went wrong."));
        assertTrue(renderedOutput.contains("============================================================"));
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

    /** Find results use the standard border, heading, numbering, and task formatting. */
    @Test
    void showFindResults_matchingTasks_printsNumberedResults() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showFindResults(List.of(new ToDos("read book"), new ToDos("return book")));

        assertEquals("    ============================================================\n"
                        + "     Bro found these matching tasks:\n"
                        + "1. [T] [ ] read book\n"
                        + "2. [T] [ ] return book\n"
                        + "    ============================================================\n",
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

        assertEquals("Let's go, bro — this task is complete!\n[X] read book\n", output.toString());
    }

    /** The session closing method emits Bro's friendly sign-off. */
    @Test
    void showGoodbye_sessionEnds_printsGoodbye() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output));

        ui.showGoodbye();

        assertEquals("Catch you later, bro. Keep crushing that to-do list!\n", output.toString());
    }

    /** The compact greeting omits the console-only banner used by graphical interfaces. */
    @Test
    void getWelcomeMessage_returnsGreetingWithoutBanner() {
        Ui ui = new Ui(new Scanner(""), new PrintStream(new ByteArrayOutputStream()));

        assertEquals("Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?",
                ui.getWelcomeMessage());
    }
}
