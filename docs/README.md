# Jarvis User Guide

// Product screenshot goes here

Jarvis Level-9 is an intelligent chatbot that helps you keep track of your tasks.
It supports todos, deadlines, and events, and lets you mark tasks as done or not done.

## Getting Started

Run the app (`src/main/java/Jarvis/Jarvis.java`), then type commands into the terminal as standard input.

To exit, type `bye`.

## Below are what you can ask Jarvis Level-9 to do

### Add an item, as one of following three categories:
#### Todo

Use `todo DESCRIPTION` to add a todo.

Example input by you:
```
todo borrow book
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 Got it. I've added this task:
  [T][ ] borrow book
 Now you have 1 task in the list.
____________________________________________________________
```

#### Deadline

Use `deadline DESCRIPTION /by DATE [TIME]` to add a deadline.

Example input by you:
```
deadline return book /by 2019-12-02 1800
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 Got it. I've added this task:
  [D][ ] return book (by: Dec 2 2019, 18:00)
 Now you have 2 tasks in the list.
____________________________________________________________
```

#### Event

Use `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` to add an event.

Example input by you:
```
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 Got it. I've added this task:
  [E][ ] project meeting (from: Dec 2 2019, 14:00 to: Dec 2 2019, 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
```

### List all stored items

Use `list` to ask Jarvis Level-9 to show all stored items, numbered from `INDEX` 1.

Example input by you:
```
list
```

Example output by Jarvis Level-9:
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

Example output by Jarvis Level-9:
```
____________________________________________________________
 Nice! I've marked this task as done:
  [D][X] return book (by: Dec 2 2019, 18:00)
____________________________________________________________
```

### Mark a task as not done

Use `unmark INDEX` to mark the task at `INDEX` as not done.

Example input by you:
```
unmark 2
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 OK, I've marked this task as not done yet:
  [D][ ] return book (by: Dec 2 2019, 18:00)
____________________________________________________________
```

### Exit

Use `bye` to exit the chat.

Example output by Jarvis Level-9:
```
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Notes

- Items are saved to disk automatically whenever the task list changes.
- Data file path (relative to project root): `data/Jarvis.txt`.
- Dates and times accept `yyyy-mm-dd` and optional `HHmm` (24-hour) after a space.
- Maximum number of stored items is 100.

## Jarvis Level-9 are also built to handle errors

If user enter an invalid command or an invalid format, Jarvis Level-9 prints an error message instead of crashing. It handles two type of errors as follows:

### Empty todo description

Example input by you:
```
todo
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 The description of a todo cannot be empty.
____________________________________________________________
```

### Unknown command

Example input by you:
```
blah
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 Sorry, I don't know what that means. Valid command starts with: todo, deadline, event, list, mark, unmark, delete, find, bye.
____________________________________________________________
```

## Delete task is also available in Jarvis Level-9

Use `delete INDEX` to remove the task at `INDEX`.

Example input by you:
```
delete 3
```

Example output by Jarvis Level-9:
```
____________________________________________________________
 Noted. I've removed this task:
  [E][ ] project meeting (from: Dec 2 2019, 14:00 to: Dec 2 2019, 16:00)
 Now you have 5 tasks in the list.
____________________________________________________________
```

## Find (Level 9)

Use `find KEYWORD` to list tasks whose descriptions contain `KEYWORD` (case-insensitive).

Example input:
```
find book
```

Example output:
```
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Dec 2 2019, 18:00)
____________________________________________________________
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.







