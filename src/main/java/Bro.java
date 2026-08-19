import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

/** Runs Bro's command-line chatbot and translates invalid input into friendly errors. */
public class Bro {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    private static final String ERROR_BORDER = "    ____________________________________________________________";
    private static final Path SAVE_FILE = Path.of("data", "duke.txt");
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd> "
            + "or <d/M/yyyy HHmm>.";

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
        // Level 7 - Start the saving mechanisms
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine().trim();
            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }

            try {
                handleCommand(userInput);
            } catch (BroException exception) {
                printError(exception.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    /** Dispatches one non-exit command, throwing a BroException for invalid input. */
    private static void handleCommand(String userInput) throws BroException {
        if (userInput.isBlank()) {
            throw new BroException("Please enter a command.");
        }

        String[] commandParts = userInput.split("\\s+", 2);
        String actionWord = commandParts[0].toLowerCase();
        switch (actionWord) {
        case "list":
            ensureNoArguments(commandParts, "list");
            listTasks();
            break;
        case "mark":
            changeTaskStatus(commandParts, true);
            break;
        case "unmark":
            changeTaskStatus(commandParts, false);
            break;
        case "delete":
            deleteTask(commandParts);
            break;
        case "todo":
            addTodo(commandParts);
            break;
        case "deadline":
            addDeadline(commandParts);
            break;
        case "event":
            addEvent(commandParts);
            break;
        default:
            throw new BroException("I don't recognize that command. Try todo, deadline, event, "
                    + "list, mark, unmark, delete, or bye.");
        }
    }

    /** Prints every task in insertion order. */
    private static void listTasks() {
        for (int i = 0; i < userTasks.size(); i++) {
            System.out.println((i + 1) + ". " + userTasks.get(i));
        }
    }

    /** Adds a todo after checking that its description is present. */
    private static void addTodo(String[] commandParts) throws BroException {
        String description = requireArgument(commandParts, "A todo needs a description. "
                + "Try: todo <description>.");
        ToDos newToDo = new ToDos(description);
        userTasks.add(newToDo);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newToDo);
        System.out.println("Now you have" + userTasks.size() + " tasks in the list");

        // Level 7 - Saving
    }

    /** Adds a deadline after validating its description and /by component. */
    private static void addDeadline(String[] commandParts) throws BroException {
        String rest = requireArgument(commandParts, DEADLINE_USAGE);
        String[] parts = rest.split("\\s*/by\\s*", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BroException(DEADLINE_USAGE);
        }

        Deadlines newDeadline = createDeadline(parts[1].trim(), parts[0].trim());
        userTasks.add(newDeadline);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newDeadline);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /**
     * Creates a deadline from either an ISO date or the assignment's day/month/year time format.
     *
     * @param dueText text following the command's {@code /by} marker
     * @param description task description supplied before the marker
     * @return a deadline that retains whether the user supplied a time
     * @throws BroException if the date or time does not match a supported, valid format
     */
    private static Deadlines createDeadline(String dueText, String description) throws BroException {
        try {
            LocalDateTime dueDateTime = LocalDateTime.parse(dueText, DATE_TIME_INPUT_FORMAT);
            return new Deadlines(dueDateTime, description);
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate dueDate = LocalDate.parse(dueText, DATE_INPUT_FORMAT);
                return new Deadlines(dueDate, description);
            } catch (DateTimeParseException exception) {
                throw new BroException(DEADLINE_USAGE);
            }
        }
    }

    /** Adds an event after validating its description, start, and end components. */
    private static void addEvent(String[] commandParts) throws BroException {
        String rest = requireArgument(commandParts, "Use: event <description> /from <start> /to <end>.");
        String[] fromSplit = rest.split("\\s*/from\\s*", -1);
        if (fromSplit.length != 2 || fromSplit[0].isBlank()) {
            throw new BroException("Use: event <description> /from <start> /to <end>.");
        }

        String[] toSplit = fromSplit[1].split("\\s*/to\\s*", -1);
        if (toSplit.length != 2 || toSplit[0].isBlank() || toSplit[1].isBlank()) {
            throw new BroException("Use: event <description> /from <start> /to <end>.");
        }

        Events newEvent = new Events(toSplit[0].trim(), toSplit[1].trim(), fromSplit[0].trim());
        userTasks.add(newEvent);
        saveTasks();
        System.out.println("Got it. I've added: \n");
        System.out.println(newEvent);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Marks or unmarks the requested task after validating its index. */
    private static void changeTaskStatus(String[] commandParts, boolean done) throws BroException {
        String operation = done ? "mark" : "unmark";
        int index = getTaskIndex(commandParts, operation);

        Task chosenTask = userTasks.get(index - 1);
        chosenTask.isDone = done;
        saveTasks();
        System.out.println(done ? "Ok this item is marked!" : "Ok this item is not marked!");
        System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);
    }

    /** Removes the requested task and reports the remaining list size. */
    private static void deleteTask(String[] commandParts) throws BroException {
        int index = getTaskIndex(commandParts, "delete");
        Task removedTask = userTasks.remove(index - 1);
        saveTasks();
        System.out.println("Noted. I've removed:");
        System.out.println(removedTask);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Parses and validates a task index shared by mark, unmark, and delete. */
    private static int getTaskIndex(String[] commandParts, String operation) throws BroException {
        String argument = requireArgument(commandParts, "Use: " + operation + " <task number>.");
        if (argument.split("\\s+").length != 1) {
            throw new BroException("Use: " + operation + " <task number>.");
        }

        int index;
        try {
            index = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new BroException("Task number must be a positive whole number.");
        }
        if (index < 1) {
            throw new BroException("Task number must be a positive whole number.");
        }
        if (userTasks.isEmpty()) {
            throw new BroException("There are no tasks to " + operation + " yet.");
        }
        if (index > userTasks.size()) {
            throw new BroException("Task number must be between 1 and " + userTasks.size() + ".");
        }
        return index;
    }

    /** Returns a required command argument or throws an exception with its usage hint. */
    private static String requireArgument(String[] commandParts, String errorMessage) throws BroException {
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new BroException(errorMessage);
        }
        return commandParts[1].trim();
    }

    /** Rejects arguments for commands that should stand alone. */
    private static void ensureNoArguments(String[] commandParts, String command) throws BroException {
        if (commandParts.length > 1) {
            throw new BroException("The " + command + " command does not take arguments. Try: "
                    + command + ".");
        }
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
            for (Task task : userTasks) {
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
