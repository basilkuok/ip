import java.time.LocalDateTime;

/**
 * Represents a task that starts at a specific time and ends at a specific time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean hasTimeFrom;
    private final boolean hasTimeTo;

    /**
     * Creates an event with the given description, start time, and end time.
     */
    public Event(String description, LocalDateTime from, boolean hasTimeFrom, LocalDateTime to, boolean hasTimeTo) {
        super(description);
        this.from = DateTimeParser.normalize(from);
        this.to = DateTimeParser.normalize(to);
        this.hasTimeFrom = hasTimeFrom;
        this.hasTimeTo = hasTimeTo;
    }

    /**
     * Returns the start date/time of the event.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns whether the start includes a time component.
     */
    public boolean hasTimeFrom() {
        return hasTimeFrom;
    }

    @Override
    public String toString() {
        String fromText = DateTimeParser.formatForDisplay(from, hasTimeFrom);
        String toText = DateTimeParser.formatForDisplay(to, hasTimeTo);
        return "[E]" + super.toString() + " (from: " + fromText + " to: " + toText + ")";
    }

    /**
     * Returns the end date/time of the event.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns whether the end includes a time component.
     */
    public boolean hasTimeTo() {
        return hasTimeTo;
    }
}
