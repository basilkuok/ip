# Jarvis User Guide

// Product screenshot goes here

Jarvis Level-3 is an intelligent chatbot that helps you keep track of things by storing the items you typed, listing them back to you on request,
and letting you designate tasks as done/undone.

## Getting Started

Run the app (`Jarvis.java`), then type commands into the terminal as standard input.

To exit, type `bye`.

## Below are what you can ask Jarvis Level-3 to do

### Add an item 

Type any text that is not `list` or `bye` to store it.

Example input by you:
```
read book
```

Example output by Jarvis Level-3:
```
____________________________________________________________
 added: read book
____________________________________________________________
```

### List all stored items

Use `list` to ask Jarvis Level-3 to show all stored items, numbered from `INDEX` 1.

Example input by you:
```
list
```

Example output by Jarvis Level-3:
```
____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[ ] return book
____________________________________________________________
```

### Mark a task as done

Use `mark INDEX` to mark the task at `INDEX` as done.

Example input by you:
```
mark 2
```

Example output by Jarvis Level-3:
```
____________________________________________________________
 Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
```

### Mark a task as not done

Use `unmark INDEX` to mark the task at `INDEX` as not done.

Example input by you:
```
unmark 2
```

Example output by Jarvis Level-3:
```
____________________________________________________________
 OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
```

### Exit

Use `bye` to exit the chat.

Example output by Jarvis Level-3:
```
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Notes

- Items are stored in memory only (they are not saved to disk).
- Maximum number of stored items is 100.







