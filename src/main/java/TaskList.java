import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages the list of tasks.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList with initial tasks.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the list.
     */
    public void add(Task task) throws JarvisException {
        if (tasks.size() >= MAX_TASKS) {
            throw new JarvisException("Sorry, I can only store " + MAX_TASKS + " tasks.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the given 1-based index.
     */
    public Task get(int taskNumber) throws JarvisException {
        if (!isValidTaskNumber(taskNumber)) {
            throw new JarvisException("Please enter a valid task number.");
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task at the given 1-based index.
     */
    public Task remove(int taskNumber) throws JarvisException {
        if (!isValidTaskNumber(taskNumber)) {
            throw new JarvisException("Please enter a valid task number.");
        }
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the internal tasks list for storage.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    Task getRaw(int zeroBasedIndex) {
        return tasks.get(zeroBasedIndex);
    }

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }
}
