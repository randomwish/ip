public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String showDone() {
        return (isDone ? "X" : " "); // mark done task with X
    }
}
