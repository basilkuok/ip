package jarvis;

/**
 * Parses user commands into structured data for Jarvis.
 */
public class Parser {
    /**
     * Types of supported commands.
     */
    public enum CommandType {
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        TODO,
        DEADLINE,
        EVENT,
        UNKNOWN
    }

    /**
     * Returns the command type based on the first keyword of the command.
     */
    public CommandType parseCommandType(String command) {
        String trimmedCommand = command.trim();
        int separatorIndex = trimmedCommand.indexOf(' ');
        String keyword = (separatorIndex == -1)
                ? trimmedCommand
                : trimmedCommand.substring(0, separatorIndex);

        switch (keyword.toLowerCase()) {
        case "list":
            return CommandType.LIST;
        case "mark":
            return CommandType.MARK;
        case "unmark":
            return CommandType.UNMARK;
        case "delete":
            return CommandType.DELETE;
        case "find":
            return CommandType.FIND;
        case "todo":
            return CommandType.TODO;
        case "deadline":
            return CommandType.DEADLINE;
        case "event":
            return CommandType.EVENT;
        default:
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Returns a task number parsed from commands like {@code mark 2}.
     */
    public int parseTaskNumber(String command) throws JarvisException {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            throw new JarvisException("Please enter a valid task number.");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new JarvisException("Please enter a valid task number.");
        }
    }


    /**
     * Returns the keyword for the find command.
     */
    public String parseFindKeyword(String command) throws JarvisException {
        if (command.equalsIgnoreCase("find")) {
            throw new JarvisException("Please use: find <keyword>");
        }

        if (!command.toLowerCase().startsWith("find ")) {
            throw new JarvisException("Please use: find <keyword>");
        }

        String keyword = command.substring("find ".length()).trim();
        if (keyword.isEmpty()) {
            throw new JarvisException("Please use: find <keyword>");
        }

        return keyword;
    }

    /**
     * Returns a Todo parsed from the given command.
     */
    public Todo parseTodo(String command) throws JarvisException {
        if (command.equalsIgnoreCase("todo")) {
            throw new JarvisException("The description of a todo cannot be empty.");
        }

        if (!command.toLowerCase().startsWith("todo ")) {
            throw new JarvisException("Please use: todo <description>");
        }

        String description = command.substring("todo ".length()).trim();
        if (description.isEmpty()) {
            throw new JarvisException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Returns a Deadline parsed from the given command.
     */
    public Deadline parseDeadline(String command) throws JarvisException {
        String usageMessage = "Please use: deadline <description> /by <yyyy-mm-dd> [HHmm]";
        if (command.equalsIgnoreCase("deadline")) {
            throw new JarvisException(usageMessage);
        }

        if (!command.toLowerCase().startsWith("deadline ")) {
            throw new JarvisException(usageMessage);
        }

        String payload = command.substring("deadline ".length()).trim();
        int byIndex = payload.indexOf(" /by ");
        if (byIndex == -1) {
            throw new JarvisException(usageMessage);
        }

        String description = payload.substring(0, byIndex).trim();
        String byText = payload.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new JarvisException(usageMessage);
        }

        DateTimeParser.ParsedDateTime by = DateTimeParser.parseUserDateTime(byText, usageMessage);
        return new Deadline(description, by.getValue(), by.hasTime());
    }

    /**
     * Returns an Event parsed from the given command.
     */
    public Event parseEvent(String command) throws JarvisException {
        String usageMessage = "Please use: event <description> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd> [HHmm]";
        if (command.equalsIgnoreCase("event")) {
            throw new JarvisException(usageMessage);
        }

        if (!command.toLowerCase().startsWith("event ")) {
            throw new JarvisException(usageMessage);
        }

        String payload = command.substring("event ".length()).trim();
        int fromIndex = payload.indexOf(" /from ");
        int toIndex = payload.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new JarvisException(usageMessage);
        }

        String description = payload.substring(0, fromIndex).trim();
        String fromText = payload.substring(fromIndex + " /from ".length(), toIndex).trim();
        String toText = payload.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new JarvisException(usageMessage);
        }

        DateTimeParser.ParsedDateTime from = DateTimeParser.parseUserDateTime(fromText, usageMessage);
        DateTimeParser.ParsedDateTime to = DateTimeParser.parseUserDateTime(toText, usageMessage);
        return new Event(description, from.getValue(), from.hasTime(), to.getValue(), to.hasTime());
    }
}
