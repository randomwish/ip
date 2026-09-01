package bro;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies the response API shared by Bro's console and graphical interfaces. */
class BroTest {
    @TempDir
    private Path temporaryDirectory;

    /** The GUI greeting is concise and does not include the console's ASCII banner. */
    @Test
    void getWelcomeMessage_newSession_returnsCompactGreeting() {
        Bro bro = new Bro(temporaryDirectory.resolve("tasks.txt").toString());

        String welcomeMessage = bro.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Hello, I'm Bro!"));
        assertFalse(welcomeMessage.contains("____"));
    }

    /** A GUI-style session can add, persist, and list a task through text responses. */
    @Test
    void getResponse_addThenList_returnsPersistedTask() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Bro bro = new Bro(filePath.toString());

        String addResponse = bro.getResponse("todo read JavaFX tutorial");
        String listResponse = bro.getResponse("list");

        assertTrue(addResponse.contains("[T] [ ] read JavaFX tutorial"));
        assertTrue(listResponse.contains("1. [T] [ ] read JavaFX tutorial"));
        assertTrue(Files.exists(filePath));
    }

    /** Invalid and exit commands are converted to responses instead of escaping as exceptions. */
    @Test
    void getResponse_invalidThenBye_returnsHelpfulMessages() {
        Bro bro = new Bro(temporaryDirectory.resolve("tasks.txt").toString());

        String invalidResponse = bro.getResponse("dance");
        String goodbyeResponse = bro.getResponse("  BYE  ");

        assertTrue(invalidResponse.contains("I don't recognize that command"));
        assertTrue(goodbyeResponse.contains("Goodbye!"));
        assertTrue(bro.isExitCommand("  BYE  "));
    }
}
