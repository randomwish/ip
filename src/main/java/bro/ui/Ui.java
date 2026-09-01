package bro.ui;

import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/** Handles Bro's console input and output. */
public class Ui {
    private static final String MESSAGE_BORDER = "    ____________________________________________________________";
    private static final String WELCOME_MESSAGE = "Hello, I'm Bro! What drink do you want?";
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

    /** Creates a UI that formats responses without writing them to the console. */
    public static Ui silent() {
        return new Ui(new Scanner(""), new PrintStream(OutputStream.nullOutputStream()));
    }

    /** Creates a UI with the supplied input and output streams. */
    Ui(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    /** Shows Bro's banner and greeting. */
    public String showWelcome() {
        return show(BANNER + "\n" + WELCOME_MESSAGE + "\n");
    }

    /** Returns the compact greeting used by interfaces that do not need the console banner. */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /** Reads and trims the next command, or returns null when input is exhausted. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /** Shows a recoverable error message. */
    public String showError(String message) {
        return show(MESSAGE_BORDER + "\n     " + message + "\n" + MESSAGE_BORDER + "\n");
    }

    /** Shows the task list in insertion order. */
    public String showTaskList(TaskList tasks) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            message.append(i + 1).append(". ").append(tasks.getTask(i)).append('\n');
        }
        return show(message.toString());
    }

    /**
     * Shows tasks whose descriptions match a find keyword.
     *
     * @param matchingTasks Tasks that matched the user's keyword.
     */
    public String showFindResults(List<Task> matchingTasks) {
        StringBuilder message = new StringBuilder(MESSAGE_BORDER)
                .append("\n     Here are the matching tasks in your list:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append(i + 1).append(". ").append(matchingTasks.get(i)).append('\n');
        }
        message.append(MESSAGE_BORDER).append('\n');
        return show(message.toString());
    }

    /** Shows a confirmation and the newly added task. */
    public String showTaskAdded(Task task, int taskCount) {
        String countPrefix = task instanceof ToDos ? "Now you have" : "Now you have ";
        return show("Got it. I've added: \n\n" + task + "\n"
                + countPrefix + taskCount + " tasks in the list\n");
    }

    /** Shows the result of marking or unmarking a task. */
    public String showTaskStatusChanged(boolean isDone, Task task) {
        String statusMessage = isDone ? "Ok this item is marked!" : "Ok this item is not marked!";
        return show(statusMessage + "\n[" + task.showDone() + "] " + task.getDescription() + "\n");
    }

    /** Shows the result of deleting a task. */
    public String showTaskDeleted(Task task, int remainingTaskCount) {
        return show("Noted. I've removed:\n" + task + "\nNow you have "
                + remainingTaskCount + " tasks in the list\n");
    }

    /** Shows the session closing message. */
    public String showGoodbye() {
        return show("Goodbye!\n");
    }

    /** Writes and returns a fully formatted response. */
    private String show(String message) {
        output.print(message);
        return message;
    }
}
