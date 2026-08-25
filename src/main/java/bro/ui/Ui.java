package bro.ui;

import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

/** Handles Bro's console input and output. */
public class Ui {
    private static final String ERROR_BORDER = "    ____________________________________________________________";
    private static final String BANNER = """
              ____               \s
             | __ )  _ __   ___  \s
             |  _ \\ | '__| / _ \\\s
             | |_) || |   | (_) |\s
             |____/ |_|    \\___/ \s
            """;

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI connected to the process console. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /** Creates a UI with the supplied input and output streams. */
    Ui(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    /** Shows Bro's banner and greeting. */
    public void showWelcome() {
        output.println(BANNER);
        output.println("Hello, I'm Bro! What drink do you want?");
    }

    /** Reads and trims the next command, or returns null when input is exhausted. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /** Shows a recoverable error message. */
    public void showError(String message) {
        output.println(ERROR_BORDER);
        output.println("     " + message);
        output.println(ERROR_BORDER);
    }

    /** Shows the task list in insertion order. */
    public void showTaskList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + ". " + tasks.getTask(i));
        }
    }

    /** Shows a confirmation and the newly added task. */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added: \n");
        output.println(task);

        String countPrefix = task instanceof ToDos ? "Now you have" : "Now you have ";
        output.println(countPrefix + taskCount + " tasks in the list");
    }

    /** Shows the result of marking or unmarking a task. */
    public void showTaskStatusChanged(boolean isDone, Task task) {
        output.println(isDone ? "Ok this item is marked!" : "Ok this item is not marked!");
        output.println("[" + task.showDone() + "] " + task.getDescription());
    }

    /** Shows the result of deleting a task. */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        output.println("Noted. I've removed:");
        output.println(task);
        output.println("Now you have " + remainingTaskCount + " tasks in the list");
    }

    /** Shows the session closing message. */
    public void showGoodbye() {
        output.println("Goodbye!");
    }
}
