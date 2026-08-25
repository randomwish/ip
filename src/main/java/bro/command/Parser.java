package bro.command;

import bro.exception.BroException;
import bro.task.Deadlines;
import bro.task.Events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Converts raw user input into validated commands and task data. */
public class Parser {
    private static final DateTimeFormatter DATE_INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd> "
            + "or <d/M/yyyy HHmm>.";

    /** Parses a line into its lower-case action word and remaining arguments. */
    public Command parseCommand(String userInput) throws BroException {
        if (userInput == null || userInput.isBlank()) {
            throw new BroException("Please enter a command.");
        }

        String[] commandParts = userInput.trim().split("\\s+", 2);
        String action = commandParts[0].toLowerCase();
        String arguments = commandParts.length == 2 ? commandParts[1] : "";
        return new Command(action, arguments);
    }

    /** Returns a required command argument or throws an exception with its usage hint. */
    public String requireArgument(Command command, String errorMessage) throws BroException {
        if (!command.hasArguments()) {
            throw new BroException(errorMessage);
        }
        return command.getArguments();
    }

    /** Rejects arguments for a command that should stand alone. */
    public void ensureNoArguments(Command command, String commandName) throws BroException {
        if (command.hasArguments()) {
            throw new BroException("The " + commandName + " command does not take arguments. Try: "
                    + commandName + ".");
        }
    }

    /** Parses a deadline command into a typed deadline task. */
    public Deadlines parseDeadline(Command command) throws BroException {
        String rest = requireArgument(command, DEADLINE_USAGE);
        String[] parts = rest.split("\\s*/by\\s*", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BroException(DEADLINE_USAGE);
        }

        return createDeadline(parts[1].trim(), parts[0].trim());
    }

    /** Parses an event command into an event task. */
    public Events parseEvent(Command command) throws BroException {
        String rest = requireArgument(command, "Use: event <description> /from <start> /to <end>.");
        String[] fromSplit = rest.split("\\s*/from\\s*", -1);
        if (fromSplit.length != 2 || fromSplit[0].isBlank()) {
            throw new BroException("Use: event <description> /from <start> /to <end>.");
        }

        String[] toSplit = fromSplit[1].split("\\s*/to\\s*", -1);
        if (toSplit.length != 2 || toSplit[0].isBlank() || toSplit[1].isBlank()) {
            throw new BroException("Use: event <description> /from <start> /to <end>.");
        }

        return new Events(toSplit[0].trim(), toSplit[1].trim(), fromSplit[0].trim());
    }

    /** Parses a one-based task number and validates it against the current task count. */
    public int parseTaskIndex(Command command, String operation, int taskCount) throws BroException {
        String argument = requireArgument(command, "Use: " + operation + " <task number>.");
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
        if (taskCount == 0) {
            throw new BroException("There are no tasks to " + operation + " yet.");
        }
        if (index > taskCount) {
            throw new BroException("Task number must be between 1 and " + taskCount + ".");
        }
        return index;
    }

    /** Parses a supported date-only or date-time value into a typed deadline. */
    private Deadlines createDeadline(String dueText, String description) throws BroException {
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
}
