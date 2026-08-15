public class Events extends Task {
    protected String dateline;
    protected String startTime;
    protected String modifier = "E";
    public Events(String startTIme, String deadline, String description) {
        super(description);
        this.dateline = deadline;
        this.startTime = startTIme;
    }

    @Override
    public String toString() {
        return "[" + this.modifier + "] " + "[" + this.showDone() + "] " + this.description + "(from: " + this.startTime
                + " to: " + this.dateline + ")";
    }

}
