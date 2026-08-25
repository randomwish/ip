package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /** An empty list reports its state and becomes non-empty after a task is added. */
    @Test
    void isEmpty_newListThenAdd_reportsCurrentState() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        tasks.add(new ToDos("read book"));
        assertFalse(tasks.isEmpty());
    }

    /** The task list rejects null tasks rather than storing invalid entries. */
    @Test
    void add_nullTask_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.add(null));
    }

    /** The exposed task collection cannot be mutated outside TaskList. */
    @Test
    void getTasks_returnedList_isUnmodifiable() {
        TaskList tasks = new TaskList(List.of(new ToDos("read book")));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.getTasks().add(new ToDos("write notes")));
        assertEquals(1, tasks.size());
    }
}
