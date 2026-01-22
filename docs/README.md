# Jarvis User Guide

// Product screenshot goes here

Jarvis Level-4 is an intelligent chatbot that helps you keep track of your tasks.
It supports todos, deadlines, and events, and lets you mark tasks as done or not done.

## Getting Started

Run the app (`Jarvis.java`), then type commands into the terminal as standard input.

To exit, type `bye`.

## Below are what you can ask Jarvis Level-4 to do

### Add an item, as one of following three categories:
#### Todo

Use `todo DESCRIPTION` to add a todo.

Example input by you:
```
todo borrow book
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 Got it. I've added this task:
  [T][ ] borrow book
 Now you have 1 task in the list.
____________________________________________________________
```

#### Deadline

Use `deadline DESCRIPTION /by TIME` to add a deadline.

Example input by you:
```
deadline return book /by Sunday
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 Got it. I've added this task:
  [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
```

#### Event

Use `event DESCRIPTION /from START /to END` to add an event.

Example input by you:
```
event project meeting /from Mon 2pm /to 4pm
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
```

### List all stored items

Use `list` to ask Jarvis Level-4 to show all stored items, numbered from `INDEX` 1.

Example input by you:
```
list
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
```

### Mark a task as done

Use `mark INDEX` to mark the task at `INDEX` as done.

Example input by you:
```
mark 2
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
```

### Mark a task as not done

Use `unmark INDEX` to mark the task at `INDEX` as not done.

Example input by you:
```
unmark 2
```

Example output by Jarvis Level-4:
```
____________________________________________________________
 OK, I've marked this task as not done yet:
  [D][ ] return book (by: Sunday)
____________________________________________________________
```

### Exit

Use `bye` to exit the chat.

Example output by Jarvis Level-4:
```
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Notes

- Maximum number of stored items is 100.







