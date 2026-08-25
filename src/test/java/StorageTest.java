package bro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies that Storage persists and restores typed task data. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Saving and loading preserves task order, completion, descriptions, and deadlines. */
    @Test
    void saveAndLoad_preservesTaskData() throws BroException {
        Path filePath = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("read book"));
        tasks.add(new Deadlines(LocalDate.of(2019, 10, 15), "submit report"));
        tasks.getTask(0).isDone = true;

        storage.save(tasks);
        TaskList restoredTasks = storage.load();

        assertTrue(Files.exists(filePath));
        assertEquals(2, restoredTasks.size());
        assertEquals("[T] [X] read book", restoredTasks.getTask(0).toString());
        assertEquals("[D] [ ] submit report(by: Oct 15 2019)", restoredTasks.getTask(1).toString());
    }

    /** Loading a path that has not been created returns an empty task list. */
    @Test
    void load_missingFile_returnsEmptyTaskList() throws BroException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        TaskList tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }
}
