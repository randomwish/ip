public class ToDos extends Task {
    protected String modifier = "T";
    public ToDos(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[" + this.modifier + "] " + "[" + this.showDone() + "] " + this.description;
    }
}
