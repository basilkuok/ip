import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs Jarvis Level-6, an intelligent chatbot that supports todos, deadlines, events, and deleting tasks.
 */
public class Jarvis {
    private static final int MAX_TASKS = 100;
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Sorry, I don't know what that means. Valid command starts with: "
                    + "todo, deadline, event, list, mark, unmark, delete, bye.";

    /**
     * Starts Jarvis, reads commands from standard input, and exits on {@code bye}.
     */
    public static void main(String[] args) {
        String horizontalLine = "____________________________________________________________";
        System.out.println(horizontalLine);
        System.out.println(" Hello! I'm Jarvis");
        System.out.println(" I am your super-intelligent friend.");
        System.out.println(" What can I do for you?");
        System.out.println(" Tell me what's in your mind and I will echo back to you.");
        System.out.println(horizontalLine);

        ArrayList<Task> tasks = new ArrayList<>();

        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNextLine()) {
                String fullCommand = input.nextLine();
                String trimmedCommand = fullCommand.trim();

                if (trimmedCommand.equalsIgnoreCase("bye")) {
                    System.out.println(horizontalLine);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(horizontalLine);
                    return;
                }

                System.out.println(horizontalLine);

                try {
                    executeCommand(trimmedCommand, tasks);
                } catch (JarvisException exception) {
                    System.out.println(" " + exception.getMessage());
                }

                System.out.println(horizontalLine);
            }
        }
    }

    private static void executeCommand(String command, ArrayList<Task> tasks) throws JarvisException {
        if (command.isEmpty()) {
            throw new JarvisException("Please enter a command.");
        }

        String lower = command.toLowerCase();
        if (lower.equals("list")) {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
            return;
        }

        if (lower.startsWith("mark")) {
            Task task = getTaskForIndex(command, tasks);
            task.markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("  " + task);
            return;
        }

        if (lower.startsWith("unmark")) {
            Task task = getTaskForIndex(command, tasks);
            task.markAsNotDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("  " + task);
            return;
        }

        if (lower.startsWith("delete")) {
            int taskNumber = parseTaskNumber(command);
            if (!isValidTaskNumber(taskNumber, tasks.size())) {
                throw new JarvisException("Please enter a valid task number.");
            }

            Task removed = tasks.remove(taskNumber - 1);
            System.out.println(" Noted. I've removed this task:");
            System.out.println("  " + removed);
            int remainingTasks = tasks.size();
            System.out.println(" Now you have " + remainingTasks + " "
                    + pluralize("task", remainingTasks) + " in the list.");
            return;
        }

        if (lower.startsWith("todo")) {
            String description = parseTodoDescription(command);
            Task task = new Todo(description);
            addTask(tasks, task);
            return;
        }

        if (lower.startsWith("deadline")) {
            ParsedDeadline parsed = parseDeadline(command);
            Task task = new Deadline(parsed.description, parsed.by);
            addTask(tasks, task);
            return;
        }

        if (lower.startsWith("event")) {
            ParsedEvent parsed = parseEvent(command);
            Task task = new Event(parsed.description, parsed.from, parsed.to);
            addTask(tasks, task);
            return;
        }

        throw new JarvisException(UNKNOWN_COMMAND_MESSAGE);
    }

    private static Task getTaskForIndex(String command, ArrayList<Task> tasks) throws JarvisException {
        int taskNumber = parseTaskNumber(command);
        if (!isValidTaskNumber(taskNumber, tasks.size())) {
            throw new JarvisException("Please enter a valid task number.");
        }
        return tasks.get(taskNumber - 1);
    }

    private static void addTask(ArrayList<Task> tasks, Task task) throws JarvisException {
        if (tasks.size() >= MAX_TASKS) {
            throw new JarvisException("Sorry, I can only store " + MAX_TASKS + " tasks.");
        }

        tasks.add(task);

        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        int numberOfTasks = tasks.size();
        System.out.println(" Now you have " + numberOfTasks + " " + pluralize("task", numberOfTasks) + " in the list.");
    }

    private static int parseTaskNumber(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static boolean isValidTaskNumber(int taskNumber, int taskCount) {
        return taskNumber >= 1 && taskNumber <= taskCount;
    }

    private static String pluralize(String word, int count) {
        if (count == 1) {
            return word;
        }
        return word + "s";
    }

    private static String parseTodoDescription(String command) throws JarvisException {
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
        return description;
    }

    private static ParsedDeadline parseDeadline(String command) throws JarvisException {
        if (command.equalsIgnoreCase("deadline")) {
            throw new JarvisException("Please use: deadline <description> /by <time>");
        }

        if (!command.toLowerCase().startsWith("deadline ")) {
            throw new JarvisException("Please use: deadline <description> /by <time>");
        }

        String payload = command.substring("deadline ".length()).trim();
        int byIndex = payload.indexOf(" /by ");
        if (byIndex == -1) {
            throw new JarvisException("Please use: deadline <description> /by <time>");
        }

        String description = payload.substring(0, byIndex).trim();
        String by = payload.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new JarvisException("Please use: deadline <description> /by <time>");
        }

        return new ParsedDeadline(description, by);
    }

    private static ParsedEvent parseEvent(String command) throws JarvisException {
        if (command.equalsIgnoreCase("event")) {
            throw new JarvisException("Please use: event <description> /from <start> /to <end>");
        }

        if (!command.toLowerCase().startsWith("event ")) {
            throw new JarvisException("Please use: event <description> /from <start> /to <end>");
        }

        String payload = command.substring("event ".length()).trim();
        int fromIndex = payload.indexOf(" /from ");
        int toIndex = payload.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new JarvisException("Please use: event <description> /from <start> /to <end>");
        }

        String description = payload.substring(0, fromIndex).trim();
        String from = payload.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = payload.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new JarvisException("Please use: event <description> /from <start> /to <end>");
        }

        return new ParsedEvent(description, from, to);
    }

    private static class ParsedDeadline {
        private final String description;
        private final String by;

        private ParsedDeadline(String description, String by) {
            this.description = description;
            this.by = by;
        }
    }

    private static class ParsedEvent {
        private final String description;
        private final String from;
        private final String to;

        private ParsedEvent(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }
    }
}
