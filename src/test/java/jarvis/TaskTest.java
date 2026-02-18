package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void toString_priorityNone_hasNoPrioritySuffix() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_prioritySet_includesPrioritySuffix() {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);
        assertEquals("[T][ ] read book (p:HIGH)", todo.toString());
    }

    @Test
    public void toString_doneTask_showsDoneMarker() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void setPriority_null_resetsToNone() {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);

        todo.setPriority(null);

        assertEquals(Priority.NONE, todo.getPriority());
        assertEquals("[T][ ] read book", todo.toString());
    }
}
