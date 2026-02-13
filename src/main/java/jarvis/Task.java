package jarvis;

/**
 * A class to represent a task with a description and a completion status.
 */
public class Task {
    private enum Status {
        TODO,
        DONE
    }

    private static final String DONE_ICON = "X";
    private static final String TODO_ICON = " ";

    protected final String description;
    private Status status;

    /**
     * Creates a task with the given description.
     */
    public Task(String description) {
        assert description != null : "description should not be null";
        this.description = description;
        this.status = Status.TODO;
    }

    /**
     * Returns the completion marker for this task.
     */
    public String getStatusIcon() {
        return (status == Status.DONE) ? DONE_ICON : TODO_ICON;
    }

    /**
     * Returns whether this task is completed
     */
    public boolean isDone() {
        return status == Status.DONE;
    }

    /**
     * Returns the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = Status.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        status = Status.TODO;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
