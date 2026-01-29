/**
 * Represents a task that needs to be done before a specific time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline with the given description and deadline time.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline time.
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
