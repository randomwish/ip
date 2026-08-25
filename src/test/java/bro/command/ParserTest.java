package bro.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bro.exception.BroException;
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
}
