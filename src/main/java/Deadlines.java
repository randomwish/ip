/** Represents a task that must be completed by a specified date or time. */
public class Deadlines extends Task {
    protected String dateline;

    /** Creates a deadline task with its due date and description. */
    public Deadlines(String deadline, String description) {
        super(description, TaskType.DEADLINE);
        this.dateline = deadline;
    }

    /** Returns the deadline task in Bro's list format. */
    @Override
    public String toString() {
        return formatTask(description + "(by: " + dateline + ")");
    }
}
