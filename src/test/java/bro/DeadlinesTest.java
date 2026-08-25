package bro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import bro.command.Command;
import bro.command.Parser;
import bro.exception.BroException;
import bro.task.Deadlines;
import org.junit.jupiter.api.Test;

/** Verifies that deadline input becomes typed date/time data and displays clearly. */
class DeadlinesTest {
    /** ISO dates are stored as a date-only value and use the friendly display format. */
    @Test
    void parsesIsoDateAsDateOnlyDeadline() throws BroException {
        Deadlines deadline = new Parser().parseDeadline(
                new Command("deadline", "submit report /by 2019-10-15"));

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getDueDateTime());
        assertFalse(deadline.hasDueTime());
        assertEquals("[D] [ ] submit report(by: Oct 15 2019)", deadline.toString());
    }

    /** Day/month/year input with a 24-hour time is stored and printed with a 12-hour clock. */
    @Test
    void parsesDayMonthYearTimeAsDateTimeDeadline() throws BroException {
        Deadlines deadline = new Parser().parseDeadline(
                new Command("deadline", "return book /by 2/12/2019 1800"));

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
        assertTrue(deadline.hasDueTime());
        assertEquals("[D] [ ] return book(by: Dec 2 2019 6:00PM)", deadline.toString());
    }

    /** Invalid calendar values and invalid 24-hour times are rejected instead of being stored. */
    @Test
    void rejectsInvalidDateAndTimeInput() {
        Parser parser = new Parser();

        assertThrows(BroException.class, () -> parser.parseDeadline(
                new Command("deadline", "tax return /by 2019-02-29")));
        assertThrows(BroException.class, () -> parser.parseDeadline(
                new Command("deadline", "return book /by 2/12/2019 2460")));
    }
}
