package jarvis;

import java.nio.file.Path;
import java.util.List;

/**
 * Provides Jarvis responses for a GUI.
 */
public class JarvisGui {
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates an JarvisGui instance that reads/writes tasks from the given data file path.
     */
    public JarvisGui(Path dataFilePathFromProjectRoot) {
        storage = new Storage(dataFilePathFromProjectRoot);
        parser = new Parser();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (JarvisException exception) {
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Returns the welcome message shown when the GUI starts.
     */
    public String getWelcomeMessage() {
        return joinLines(
                "Hello! I'm Jarvis",
                "I am your super-intelligent friend.",
                "What can I do for you?"
        );
    }

    /**
     * Returns whether the input should cause the app to exit.
     */
    public boolean shouldExit(String input) {
        return input != null && input.trim().equalsIgnoreCase("bye");
    }

    /**
     * Returns Jarvis's response to a user input line.
     *
     * @param input User input.
     * @return Response string to display in the GUI.
     */
    public String getResponse(String input) {
        String trimmedInput = (input == null) ? "" : input.trim();
        if (trimmedInput.isEmpty()) {
            return Messages.EMPTY_COMMAND_MESSAGE;
        }

        if (trimmedInput.equalsIgnoreCase("bye")) {
            return "Bye. Hope to see you again soon!";
        }

        try {
            return executeCommand(trimmedInput);
        } catch (JarvisException exception) {
            return exception.getMessage();
        }
    }

    private String executeCommand(String command) throws JarvisException {
        Parser.CommandType commandType = parser.parseCommandType(command);
        switch (commandType) {
        case LIST:
            return formatTaskList();
        case MARK:
            return markTask(command);
        case UNMARK:
            return unmarkTask(command);
        case DELETE:
            return deleteTask(command);
        case FIND: {
            String keyword = parser.parseFindKeyword(command);
            return formatMatchingTasks(tasks.findByKeyword(keyword));
        }
        case TODO:
            return addTask(parser.parseTodo(command));
        case DEADLINE:
            return addTask(parser.parseDeadline(command));
        case EVENT:
            return addTask(parser.parseEvent(command));
        case UNKNOWN:
        default:
            throw new JarvisException(Messages.UNKNOWN_COMMAND_MESSAGE);
        }
    }

    private void persistTasks() throws JarvisException {
        storage.saveTasks(tasks.getTasks());
    }

    private String markTask(String command) throws JarvisException {
        int taskNumber = parser.parseTaskNumber(command);
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        persistTasks();
        return joinLines(
                "Nice! I've marked this task as done:",
                "  " + task
        );
    }

    private String unmarkTask(String command) throws JarvisException {
        int taskNumber = parser.parseTaskNumber(command);
        Task task = tasks.get(taskNumber);
        task.markAsNotDone();
        persistTasks();
        return joinLines(
                "OK, I've marked this task as not done yet:",
                "  " + task
        );
    }

    private String deleteTask(String command) throws JarvisException {
        int taskNumber = parser.parseTaskNumber(command);
        Task removed = tasks.remove(taskNumber);
        persistTasks();
        int remainingTasks = tasks.size();
        return joinLines(
                "Noted. I've removed this task:",
                "  " + removed,
                "Now you have " + remainingTasks + " " + pluralize("task", remainingTasks) + " in the list."
        );
    }

    private String addTask(Task task) throws JarvisException {
        tasks.add(task);
        persistTasks();
        return formatTaskAdded(task);
    }

    private String formatTaskAdded(Task task) {
        int numberOfTasks = tasks.size();
        return joinLines(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + numberOfTasks + " " + pluralize("task", numberOfTasks) + " in the list."
        );
    }

    private String formatTaskList() {
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(tasks.getRaw(i));
        }
        return sb.toString();
    }

    private static String formatMatchingTasks(List<Task> matchingTasks) {
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            sb.append("\n  (none)");
            return sb.toString();
        }

        for (int i = 0; i < matchingTasks.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(matchingTasks.get(i));
        }
        return sb.toString();
    }

    private static String pluralize(String word, int count) {
        return (count == 1) ? word : word + "s";
    }

    private static String joinLines(String... lines) {
        return String.join("\n", lines);
    }
}
