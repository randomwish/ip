import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Verifies that deadline input becomes typed date/time data and displays clearly. */
class DeadlinesTest {
    /** ISO dates are stored as a date-only value and use the friendly display format. */
    @Test
    void parsesIsoDateAsDateOnlyDeadline() throws BroException {
        Deadlines deadline = Bro.createDeadline("2019-10-15", "submit report");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getDueDateTime());
        assertFalse(deadline.hasDueTime());
        assertEquals("[D] [ ] submit report(by: Oct 15 2019)", deadline.toString());
    }

    /** Day/month/year input with a 24-hour time is stored and printed with a 12-hour clock. */
    @Test
    void parsesDayMonthYearTimeAsDateTimeDeadline() throws BroException {
        Deadlines deadline = Bro.createDeadline("2/12/2019 1800", "return book");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDueDateTime());
        assertTrue(deadline.hasDueTime());
        assertEquals("[D] [ ] return book(by: Dec 2 2019 6:00PM)", deadline.toString());
    }

    /** Invalid calendar values and invalid 24-hour times are rejected instead of being stored. */
    @Test
    void rejectsInvalidDateAndTimeInput() {
        assertThrows(BroException.class, () -> Bro.createDeadline("2019-02-29", "tax return"));
        assertThrows(BroException.class, () -> Bro.createDeadline("2/12/2019 2460", "return book"));
    }
}
