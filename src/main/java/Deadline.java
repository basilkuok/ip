import java.time.LocalDateTime;

/**
 * Represents a task that needs to be done before a specific time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates a deadline with the given description and deadline time.
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = DateTimeParser.normalize(by);
        this.hasTime = hasTime;
    }

    /**
     * Returns the deadline time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether this deadline includes a time component.
     */
    public boolean hasTime() {
        return hasTime;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(by, hasTime) + ")";
    }
}
