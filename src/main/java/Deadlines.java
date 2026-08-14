public class Deadlines extends Task{
    protected String dateline;
    protected String modifier = "D";
    public Deadlines(String deadline, String description) {
        super(description);
        this.dateline = deadline;
    }

    @Override
    public String toString() {
        return "[" + this.modifier + "] " + "[" + this.showDone() + "] " + this.description;
    }
}
