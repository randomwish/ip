package bro;

/** Represents a task that occurs between a start and end time. */
public class Events extends Task {
    protected String dateline;
    protected String startTime;

    /** Creates an event task with its start time, end time, and description. */
    public Events(String startTime, String deadline, String description) {
        super(description, TaskType.EVENT);
        this.dateline = deadline;
        this.startTime = startTime;
    }

    /** Returns the event task in Bro's list format. */
    @Override
    public String toString() {
        return formatTask(description + "(from: " + startTime + " to: " + dateline + ")");
    }

}
