package bro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/** Runs Bro's command-line chatbot and translates invalid input into friendly errors. */
public class Bro {
    private static final TaskList userTasks = new TaskList();
    private static final String ERROR_BORDER = "    ____________________________________________________________";
    private static final Path SAVE_FILE = Path.of("data", "duke.txt");
    private static final Parser PARSER = new Parser();

    /** Starts a session, processing commands until the user says goodbye or closes the input. */
    public static void main(String[] args) {
        String banner = """
                  ____               \s
                 | __ )  _ __   ___  \s
                 |  _ \\ | '__| / _ \\\s
                 | |_) || |   | (_) |\s
                 |____/ |_|    \\___/ \s
                """;
        Scanner scanner = new Scanner(System.in);
        System.out.println(banner);
        System.out.println("Hello, I'm Bro! What drink do you want?");
        try {
            loadTasks();
        } catch (BroException exception) {
            printError(exception.getMessage());
        }
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }

            try {
                handleCommand(PARSER.parseCommand(userInput));
            } catch (BroException exception) {
                printError(exception.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    /** Dispatches one non-exit command, throwing a BroException for invalid input. */
    private static void handleCommand(Command command) throws BroException {
        switch (command.getAction()) {
        case "list":
            PARSER.ensureNoArguments(command, "list");
            listTasks();
            break;
        case "mark":
            changeTaskStatus(command, true);
            break;
        case "unmark":
            changeTaskStatus(command, false);
            break;
        case "delete":
            deleteTask(command);
            break;
        case "todo":
            addTodo(command);
            break;
        case "deadline":
            addDeadline(command);
            break;
        case "event":
            addEvent(command);
            break;
        default:
            throw new BroException("I don't recognize that command. Try todo, deadline, event, "
                    + "list, mark, unmark, delete, or bye.");
        }
    }

    /** Prints every task in insertion order. */
    private static void listTasks() {
        for (int i = 0; i < userTasks.size(); i++) {
            System.out.println((i + 1) + ". " + userTasks.getTask(i));
        }
    }

    /** Adds a todo after checking that its description is present. */
    private static void addTodo(Command command) throws BroException {
        String description = PARSER.requireArgument(command, "A todo needs a description. "
                + "Try: todo <description>.");
        ToDos newToDo = new ToDos(description);
        userTasks.add(newToDo);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newToDo);
        System.out.println("Now you have" + userTasks.size() + " tasks in the list");

    }

    /** Adds a deadline after validating its description and /by component. */
    private static void addDeadline(Command command) throws BroException {
        Deadlines newDeadline = PARSER.parseDeadline(command);
        userTasks.add(newDeadline);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newDeadline);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Adds an event after validating its description, start, and end components. */
    private static void addEvent(Command command) throws BroException {
        Events newEvent = PARSER.parseEvent(command);
        userTasks.add(newEvent);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newEvent);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Marks or unmarks the requested task after validating its index. */
    private static void changeTaskStatus(Command command, boolean done) throws BroException {
        String operation = done ? "mark" : "unmark";
        int index = PARSER.parseTaskIndex(command, operation, userTasks.size());

        Task chosenTask = userTasks.getTask(index - 1);
        chosenTask.isDone = done;
        saveTasks();
        System.out.println(done ? "Ok this item is marked!" : "Ok this item is not marked!");
        System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);
    }

    /** Removes the requested task and reports the remaining list size. */
    private static void deleteTask(Command command) throws BroException {
        int index = PARSER.parseTaskIndex(command, "delete", userTasks.size());
        Task removedTask = userTasks.removeTask(index - 1);
        saveTasks();
        System.out.println("Noted. I've removed:");
        System.out.println(removedTask);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Displays a consistent, recoverable error message without ending the session. */
    private static void printError(String message) {
        System.out.println(ERROR_BORDER);
        System.out.println("     " + message);
        System.out.println(ERROR_BORDER);
    }

    /** Converts a task to one line in Bro's saved task-file format. */
    private static String toFileLine(Task task) {
        String done = task.isDone ? "1" : "0";

        if (task instanceof ToDos) {
            return "T | " + done + " | " + task.description;
        }

        if (task instanceof Deadlines deadline) {
            return "D | " + done + " | " + task.description + " | "
                    + deadline.getDueDateTime() + " | " + (deadline.hasDueTime() ? "1" : "0");
        }

        Events event = (Events) task;
        return "E | " + done + " | " + task.description
                + " | " + event.startTime + " | " + event.dateline;
    }

    /** Recreates one task from a line in Bro's saved task-file format. */
    private static Task fromFileLine(String line) throws BroException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new BroException("A saved task has an invalid format.");
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3) {
            task = new ToDos(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 5) {
            task = readDeadline(parts);
        } else if (parts[0].equals("E") && parts.length == 5) {
            task = new Events(parts[3], parts[4], parts[2]);
        } else {
            throw new BroException("A saved task has an invalid format.");
        }

        task.isDone = parts[1].equals("1");
        return task;
    }

    /** Recreates a deadline from its ISO-8601 date-time and time-presence flag. */
    private static Deadlines readDeadline(String[] parts) throws BroException {
        if (!parts[4].equals("0") && !parts[4].equals("1")) {
            throw new BroException("A saved task has an invalid format.");
        }

        try {
            LocalDateTime dueDateTime = LocalDateTime.parse(parts[3]);
            return new Deadlines(dueDateTime, parts[4].equals("1"), parts[2]);
        } catch (DateTimeParseException exception) {
            throw new BroException("A saved task has an invalid format.");
        }
    }

    /** Saves the complete task list after a successful change. */
    private static void saveTasks() throws BroException {
        try {
            Files.createDirectories(SAVE_FILE.getParent());

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : userTasks.getTasks()) {
                lines.add(toFileLine(task));
            }

            Files.write(SAVE_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new BroException("I could not save your tasks.");
        }
    }

    /** Loads saved tasks when the data file exists on startup. */
    private static void loadTasks() throws BroException {
        if (!Files.exists(SAVE_FILE)) {
            return; // First run: there is nothing to load yet.
        }

        try {
            for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    userTasks.add(fromFileLine(line));
                }
            }
        } catch (IOException exception) {
            throw new BroException("I could not load your saved tasks.");
        }
    }
}
