import java.util.ArrayList;
import java.util.Scanner;

/** Runs Bro's command-line chatbot and translates invalid input into friendly errors. */
public class Bro {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    private static final String ERROR_BORDER = "    ____________________________________________________________";

    /** Starts a session, processing commands until the user says goodbye or closes the input. */
    public static void main(String[] args) {
        String banner = "  ____                \n"
                + " | __ )  _ __   ___   \n"
                + " |  _ \\ | '__| / _ \\ \n"
                + " | |_) || |   | (_) | \n"
                + " |____/ |_|    \\___/  \n";
        Scanner scanner = new Scanner(System.in);
        System.out.println(banner);
        System.out.println("Hello, I'm Bro! What drink do you want?");

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
        System.out.println("Got it. I've added: \n");
        System.out.println(newToDo);
        System.out.println("Now you have" + userTasks.size() + " tasks in the list");
    }

    /** Adds a deadline after validating its description and /by component. */
    private static void addDeadline(String[] commandParts) throws BroException {
        String rest = requireArgument(commandParts, "Use: deadline <description> /by <date>.");
        String[] parts = rest.split("\\s*/by\\s*", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BroException("Use: deadline <description> /by <date>.");
        }

        Deadlines newDeadline = new Deadlines(parts[1].trim(), parts[0].trim());
        userTasks.add(newDeadline);
        System.out.println("Got it. I've added: \n");
        System.out.println(newDeadline);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
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
        System.out.println(done ? "Ok this item is marked!" : "Ok this item is not marked!");
        System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);
    }

    /** Removes the requested task and reports the remaining list size. */
    private static void deleteTask(String[] commandParts) throws BroException {
        int index = getTaskIndex(commandParts, "delete");
        Task removedTask = userTasks.remove(index - 1);
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
}
