package jarvis;

import java.util.Scanner;

/**
 * Handles all interactions with the user for Jarvis.
 */
public class Ui implements AutoCloseable {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";

    private final Scanner input;

    /**
     * Creates a Ui that reads user input from standard input.
     */
    public Ui() {
        input = new Scanner(System.in);
    }

    /**
     * Prints the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Jarvis");
        System.out.println(" I am your super-intelligent friend.");
        System.out.println(" What can I do for you?");
        System.out.println(" Tell me what's in your mind and I will echo back to you.");
        showLine();
    }

    /**
     * Prints the goodbye message.
     */
    public void showBye() {
        showLine();
        System.out.println(" Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Returns whether another command line is available.
     */
    public boolean hasNextCommand() {
        return input.hasNextLine();
    }

    /**
     * Reads a full command line from the user.
     */
    public String readCommand() {
        return input.nextLine();
    }

    /**
     * Prints an error message.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Prints an error message when loading fails.
     */
    public void showLoadingError(String message) {
        showError(message);
    }

    /**
     * Prints all tasks currently in the task list.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.getRaw(i));
        }
    }

    /**
     * Prints the response after adding a task.
     */
    public void showTaskAdded(Task task, int numberOfTasks) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + numberOfTasks + " " + pluralize("task", numberOfTasks) + " in the list.");
    }

    /**
     * Prints the response after marking a task as done.
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Prints the response after unmarking a task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Prints the response after deleting a task.
     */
    public void showTaskDeleted(Task task, int remainingTasks) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println(" Now you have " + remainingTasks + " "
                + pluralize("task", remainingTasks) + " in the list.");
    }

    private static String pluralize(String word, int count) {
        return (count == 1) ? word : word + "s";
    }

    @Override
    public void close() {
        input.close();
    }
}
