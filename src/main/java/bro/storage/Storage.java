package bro.storage;

import bro.exception.BroException;
import bro.task.Deadlines;
import bro.task.Events;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads tasks from a file and saves task lists in Bro's persistent format. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads all saved tasks, or an empty list when the storage file does not exist. */
    public TaskList load() throws BroException {
        if (!Files.exists(filePath)) {
            return new TaskList();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(fromFileLine(line));
                }
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new BroException("I could not load your saved tasks.");
        }
    }

    /** Saves the complete task list, creating its parent directory when necessary. */
    public void save(TaskList tasks) throws BroException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                lines.add(toFileLine(task));
            }

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new BroException("I could not save your tasks.");
        }
    }

    /** Converts a task to one line in Bro's saved task-file format. */
    private String toFileLine(Task task) {
        String done = task.isDone() ? "1" : "0";

        if (task instanceof ToDos) {
            return "T | " + done + " | " + task.getDescription();
        }

        if (task instanceof Deadlines deadline) {
            return "D | " + done + " | " + task.getDescription() + " | "
                    + deadline.getDueDateTime() + " | " + (deadline.hasDueTime() ? "1" : "0");
        }

        Events event = (Events) task;
        return "E | " + done + " | " + task.getDescription()
                + " | " + event.getStartTime() + " | " + event.getEndTime();
    }

    /** Recreates one task from a line in Bro's saved task-file format. */
    private Task fromFileLine(String line) throws BroException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
            throw new BroException("A saved task has an invalid format.");
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3) {
            task = new ToDos(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 5) {
            task = readDeadline(parts);
        } else if (parts[0].equals("E") && parts.length == 5) {
            task = new Events(parts[3], parts[4], parts[2]);
        } else {
            throw new BroException("A saved task has an invalid format.");
        }

        task.setDone(parts[1].equals("1"));
        return task;
    }

    /** Recreates a deadline from its ISO-8601 date-time and time-presence flag. */
    private Deadlines readDeadline(String[] parts) throws BroException {
        if (!parts[4].equals("0") && !parts[4].equals("1")) {
            throw new BroException("A saved task has an invalid format.");
        }

        try {
            LocalDateTime dueDateTime = LocalDateTime.parse(parts[3]);
            return new Deadlines(dueDateTime, parts[4].equals("1"), parts[2]);
        } catch (DateTimeParseException exception) {
            throw new BroException("A saved task has an invalid format.");
        }
    }
}
