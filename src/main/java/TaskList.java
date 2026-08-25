package bro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Owns Bro's tasks and provides operations for managing their order and membership. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks in their current order. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(Objects.requireNonNull(task));
    }

    /** Returns the task at the supplied zero-based position. */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the supplied zero-based position. */
    public Task removeTask(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns a read-only view for operations such as saving the task list. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
