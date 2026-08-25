package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies that TaskList owns task collection operations without exposing mutable state. */
class TaskListTest {
    /** Adding a task makes it available at the next list position. */
    @Test
    void addTask_storesTaskInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task task = new ToDos("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.getTask(0));
    }

    /** Removing a task returns it and leaves the remaining tasks in order. */
    @Test
    void removeTask_returnsRemovedTaskAndPreservesRemainingOrder() {
        Task firstTask = new ToDos("read book");
        Task secondTask = new ToDos("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertSame(firstTask, tasks.removeTask(0));
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.getTask(0));
    }

    /** The task list rejects an out-of-range access instead of returning invalid data. */
    @Test
    void getTask_rejectsOutOfRangeIndex() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.getTask(0));
    }
}
