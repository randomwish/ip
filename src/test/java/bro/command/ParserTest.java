package bro.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bro.exception.BroException;
import bro.task.Events;
import org.junit.jupiter.api.Test;

/** Verifies that Parser translates raw input into validated command data. */
class ParserTest {
    /** A command is split into a normalized action and its argument text. */
    @Test
    void parseCommand_separatesActionFromArguments() throws BroException {
        Command command = new Parser().parseCommand("DEADLINE return book /by 2019-10-15");

        assertEquals("deadline", command.getAction());
        assertEquals("return book /by 2019-10-15", command.getArguments());
        assertTrue(command.hasArguments());
    }

    /** A command without arguments is represented explicitly instead of using null. */
    @Test
    void parseCommand_representsMissingArgumentsAsEmpty() throws BroException {
        Command command = new Parser().parseCommand("list");

        assertEquals("list", command.getAction());
        assertEquals("", command.getArguments());
        assertFalse(command.hasArguments());
    }

    /** Blank input is rejected before it can become an invalid command. */
    @Test
    void parseCommand_rejectsBlankInput() {
        assertThrows(BroException.class, () -> new Parser().parseCommand("  "));
    }

    /** A required argument is returned after command construction has removed surrounding whitespace. */
    @Test
    void requireArgument_presentArgument_returnsArgumentText() throws BroException {
        Parser parser = new Parser();
        Command command = new Command("todo", "  read book  ");

        assertEquals("read book", parser.requireArgument(command, "missing"));
    }

    /** A missing required argument produces the caller's usage message. */
    @Test
    void requireArgument_missingArgument_throwsUsageMessage() {
        Parser parser = new Parser();

        BroException exception = assertThrows(BroException.class,
                () -> parser.requireArgument(new Command("todo", ""), "Use: todo <description>."));

        assertEquals("Use: todo <description>.", exception.getMessage());
    }

    /** A standalone command rejects extra text while accepting no arguments. */
    @Test
    void ensureNoArguments_extraArgument_throwsUsageMessage() throws BroException {
        Parser parser = new Parser();

        parser.ensureNoArguments(new Command("list", ""), "list");
        BroException exception = assertThrows(BroException.class,
                () -> parser.ensureNoArguments(new Command("list", "all"), "list"));

        assertEquals("The list command does not take arguments. Try: list.", exception.getMessage());
    }

    /** An event command separates its description, start time, and end time. */
    @Test
    void parseEvent_validCommand_createsEventWithAllFields() throws BroException {
        Events event = new Parser().parseEvent(
                new Command("event", "project meeting /from 2pm /to 4pm"));

        assertEquals("project meeting", event.getDescription());
        assertEquals("2pm", event.getStartTime());
        assertEquals("4pm", event.getEndTime());
        assertEquals("[E] [ ] project meeting(from: 2pm to: 4pm)", event.toString());
    }

    /** A task index accepts one-based positions and rejects invalid or out-of-range values. */
    @Test
    void parseTaskIndex_invalidValues_throwValidationErrors() throws BroException {
        Parser parser = new Parser();

        assertEquals(2, parser.parseTaskIndex(new Command("mark", "2"), "mark", 3));
        assertThrows(BroException.class,
                () -> parser.parseTaskIndex(new Command("mark", "0"), "mark", 3));
        assertThrows(BroException.class,
                () -> parser.parseTaskIndex(new Command("mark", "4"), "mark", 3));
        assertThrows(BroException.class,
                () -> parser.parseTaskIndex(new Command("mark", "abc"), "mark", 3));
        assertThrows(BroException.class,
                () -> parser.parseTaskIndex(new Command("mark", "1 2"), "mark", 3));
    }
}
