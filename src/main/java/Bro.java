package bro;

import java.util.Scanner;

/** Runs Bro's command-line chatbot and translates invalid input into friendly errors. */
public class Bro {
    private static TaskList userTasks = new TaskList();
    private static final String ERROR_BORDER = "    ____________________________________________________________";
    private static final Parser PARSER = new Parser();
    private static final Storage STORAGE = new Storage("data/duke.txt");

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
            userTasks = STORAGE.load();
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
        STORAGE.save(userTasks);
        System.out.println("Got it. I've added: \n");
        System.out.println(newToDo);
        System.out.println("Now you have" + userTasks.size() + " tasks in the list");

    }

    /** Adds a deadline after validating its description and /by component. */
    private static void addDeadline(Command command) throws BroException {
        Deadlines newDeadline = PARSER.parseDeadline(command);
        userTasks.add(newDeadline);
        STORAGE.save(userTasks);
        System.out.println("Got it. I've added: \n");
        System.out.println(newDeadline);
        System.out.println("Now you have " + userTasks.size() + " tasks in the list");
    }

    /** Adds an event after validating its description, start, and end components. */
    private static void addEvent(Command command) throws BroException {
        Events newEvent = PARSER.parseEvent(command);
        userTasks.add(newEvent);
        STORAGE.save(userTasks);
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
        STORAGE.save(userTasks);
        System.out.println(done ? "Ok this item is marked!" : "Ok this item is not marked!");
        System.out.println("[" + chosenTask.showDone() + "] " + chosenTask.description);
    }

    /** Removes the requested task and reports the remaining list size. */
    private static void deleteTask(Command command) throws BroException {
        int index = PARSER.parseTaskIndex(command, "delete", userTasks.size());
        Task removedTask = userTasks.removeTask(index - 1);
        STORAGE.save(userTasks);
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

}
