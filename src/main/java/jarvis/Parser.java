package jarvis;


/**
 * Parses user commands into structured data for Jarvis.
 */
public class Parser {
    private static final String VALID_TASK_NUMBER_MESSAGE = "Please enter a valid task number.";

    /**
     * Types of supported commands.
     */
    public enum CommandType {
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        PRIORITY,
        TODO,
        DEADLINE,
        EVENT,
        UNKNOWN
    }

    /**
     * Returns the command type based on the first keyword of the command.
     */
    public CommandType parseCommandType(String command) {
        assert command != null : "command should not be null";
        String trimmedCommand = command.trim();
        assert !trimmedCommand.isEmpty() : "command should not be empty";
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
        case "priority":
            return CommandType.PRIORITY;
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
     * Represents a priority update command.
     */
    public record PriorityUpdate(int taskNumber, Priority priority) {
    }

    /**
     * Returns a task number parsed from commands like {@code mark 2}.
     */
    public int parseTaskNumber(String command) throws JarvisException {
        assert command != null : "command should not be null";
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            throw new JarvisException(VALID_TASK_NUMBER_MESSAGE);
        }

        try {
            return Integer.parseInt(parts[1]);
        } 
        catch (NumberFormatException exception) {
            throw new JarvisException(VALID_TASK_NUMBER_MESSAGE);
        }
    }

    /**
     * Returns the task number for commands like {@code mark 2}, disallowing extra arguments.
     */
    public int parseSingleIndexCommand(String command, String keyword) throws JarvisException {
        String usageMessage = "Please use: " + keyword + " <taskNumber>";
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase(keyword)) {
            throw new JarvisException(usageMessage);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } 
        catch (NumberFormatException exception) {
            throw new JarvisException(VALID_TASK_NUMBER_MESSAGE);
        }

        if (taskNumber < 1) {
            throw new JarvisException(VALID_TASK_NUMBER_MESSAGE);
        }
        return taskNumber;
    }

    /**
     * Returns a parsed priority update from a command like {@code priority 2 high}.
     */
    public PriorityUpdate parsePriorityUpdate(String command) throws JarvisException {
        String usageMessage = "Please use: priority <taskNumber> <low|medium|high|none>";
        if (command.equalsIgnoreCase("priority")) {
            throw new JarvisException(usageMessage);
        }

        String[] parts = command.trim().split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("priority")) {
            throw new JarvisException(usageMessage);
        }

        int taskNumber = parseTaskNumber(command);
        Priority priority = Priority.parseUserInput(parts[2]);
        return new PriorityUpdate(taskNumber, priority);
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
        String[] parts = payload.split("\\s+/by\\s+", -1);
        if (parts.length != 2) {
            throw new JarvisException(usageMessage);
        }

        String description = parts[0].trim();
        String byText = parts[1].trim();
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
        int toIndex = payload.toLowerCase().indexOf(" /to ");
        if (toIndex == -1) {
            throw new JarvisException(usageMessage);
        }

        String toText = payload.substring(toIndex + " /to ".length()).trim();
        if (toText.toLowerCase().contains(" /to ")) {
            throw new JarvisException(usageMessage);
        }

        String beforeTo = payload.substring(0, toIndex).trim();
        String[] beforeToParts = beforeTo.split("\\s+/from\\s+", -1);
        if (beforeToParts.length != 2) {
            throw new JarvisException(usageMessage);
        }

        String description = beforeToParts[0].trim();
        String fromText = beforeToParts[1].trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new JarvisException(usageMessage);
        }

        DateTimeParser.ParsedDateTime from = DateTimeParser.parseUserDateTime(fromText, usageMessage);
        DateTimeParser.ParsedDateTime to = DateTimeParser.parseUserDateTime(toText, usageMessage);
        if (!from.getValue().isBefore(to.getValue())) {
            throw new JarvisException("Event start must be earlier than end.\n" + usageMessage);
        }
        return new Event(description, from.getValue(), from.hasTime(), to.getValue(), to.hasTime());
    }
}
