package bro;

import bro.command.Command;
import bro.command.Parser;
import bro.exception.BroException;
import bro.storage.Storage;
import bro.task.Deadlines;
import bro.task.Events;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;
import bro.ui.Ui;

import java.util.Objects;

/** Coordinates Bro's user interface, command parser, task list, and storage. */
public class Bro {
    private static final String DEFAULT_FILE_PATH = "data/duke.txt";

    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;

    /** Creates Bro with storage at the supplied file path and the process console as its UI. */
    public Bro(String filePath) {
        this(new Storage(filePath), new Ui(), new Parser());
    }

    /** Creates Bro with the supplied collaborators. */
    Bro(Storage storage, Ui ui, Parser parser) {
        this.storage = Objects.requireNonNull(storage);
        this.ui = Objects.requireNonNull(ui);
        this.parser = Objects.requireNonNull(parser);
        this.tasks = new TaskList();
    }

    /** Runs the chatbot until the user says goodbye or closes the input. */
    public void run() {
        ui.showWelcome();
        try {
            tasks = storage.load();
        } catch (BroException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }

        String userInput;
        while ((userInput = ui.readCommand()) != null) {
            if (userInput.equalsIgnoreCase("bye")) {
                break;
            }

            try {
                handleCommand(parser.parseCommand(userInput));
            } catch (BroException exception) {
                ui.showError(exception.getMessage());
            }
        }
        ui.showGoodbye();
    }

    /** Starts Bro with its default relative storage path. */
    public static void main(String[] args) {
        new Bro(DEFAULT_FILE_PATH).run();
    }

    /** Dispatches one parsed command, throwing a BroException for invalid input. */
    private void handleCommand(Command command) throws BroException {
        switch (command.getAction()) {
        case "list":
            parser.ensureNoArguments(command, "list");
            ui.showTaskList(tasks);
            break;
        case "find":
            findTasks(command);
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
                    + "list, find, mark, unmark, delete, or bye.");
        }
    }

    /** Finds tasks whose descriptions contain the requested keyword. */
    private void findTasks(Command command) throws BroException {
        String keyword = parser.requireArgument(command, "Use: find <keyword>.");
        ui.showFindResults(tasks.findTasks(keyword));
    }

    /** Adds a todo after checking that its description is present. */
    private void addTodo(Command command) throws BroException {
        String description = parser.requireArgument(command, "A todo needs a description. "
                + "Try: todo <description>.");
        ToDos newToDo = new ToDos(description);
        tasks.add(newToDo);
        storage.save(tasks);
        ui.showTaskAdded(newToDo, tasks.size());
    }

    /** Adds a deadline after validating its description and /by component. */
    private void addDeadline(Command command) throws BroException {
        Deadlines newDeadline = parser.parseDeadline(command);
        tasks.add(newDeadline);
        storage.save(tasks);
        ui.showTaskAdded(newDeadline, tasks.size());
    }

    /** Adds an event after validating its description, start, and end components. */
    private void addEvent(Command command) throws BroException {
        Events newEvent = parser.parseEvent(command);
        tasks.add(newEvent);
        storage.save(tasks);
        ui.showTaskAdded(newEvent, tasks.size());
    }

    /** Marks or unmarks the requested task after validating its index. */
    private void changeTaskStatus(Command command, boolean done) throws BroException {
        String operation = done ? "mark" : "unmark";
        int index = parser.parseTaskIndex(command, operation, tasks.size());

        Task chosenTask = tasks.getTask(index - 1);
        chosenTask.setDone(done);
        storage.save(tasks);
        ui.showTaskStatusChanged(done, chosenTask);
    }

    /** Removes the requested task and reports the remaining list size. */
    private void deleteTask(Command command) throws BroException {
        int index = parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.removeTask(index - 1);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
