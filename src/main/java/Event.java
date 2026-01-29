/**
 * Represents a task that starts at a specific time and ends at a specific time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Constructor to create an event with the given description, start time, and end time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of the event.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time of the event.
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}