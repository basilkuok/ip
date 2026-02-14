package jarvis;

/**
 * Centralizes user-facing messages shared across Jarvis entry points.
 */
public final class Messages {
    public static final String EMPTY_COMMAND_MESSAGE = "Please enter a command.";

    public static final String UNKNOWN_COMMAND_MESSAGE =
            "Sorry, I don't know what that means. Valid command starts with: "
                    + "todo, deadline, event, list, mark, unmark, delete, find, priority, bye.";

    private Messages() {
        // Utility class; do not instantiate.
    }
}
