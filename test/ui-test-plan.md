# Console UI test plan

This plan is the source of truth for console UI behavior. The runner starts a
fresh application process for each test case, compares output exactly, and
stops as soon as one case fails.

## Test environment

- **Setup command:** `javac -d out/production src/main/java/*.java`
- **Timeout seconds:** `5`
- **Output matching:** Exact, including blank lines and spaces. Line-ending
  differences between Windows and Unix are ignored.

## Test case: greeting-and-exit

**Aim:** Verify that the application shows its greeting and exits cleanly.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Goodbye!
```

## Test case: todo-add-and-list

**Aim:** Verify that a todo command adds a task and list displays it.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo read book
list
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[T] [ ] read book
Now you have1 tasks in the list
1. [T] [ ] read book
Goodbye!
```

## Test case: deadline-add-and-list

**Aim:** Verify that a deadline command stores and lists its description and due time.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
deadline submit report /by Friday
list
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[D] [ ] submit report(by: Friday)
Now you have 1 tasks in the list
1. [D] [ ] submit report(by: Friday)
Goodbye!
```

## Test case: event-add-and-list

**Aim:** Verify that an event command stores and lists both event times.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
event project meeting /from 2pm /to 4pm
list
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[E] [ ] project meeting(from: 2pm to: 4pm)
Now you have 1 tasks in the list
1. [E] [ ] project meeting(from: 2pm to: 4pm)
Goodbye!
```

## Test case: mark-a-todo

**Aim:** Verify that mark changes the selected todo and list shows it as complete.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo read book
mark 1
list
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[T] [ ] read book
Now you have1 tasks in the list
Ok this item is marked!
[X] read book
1. [T] [X] read book
Goodbye!
```

## Test case: empty-todo

**Aim:** Verify that an empty todo is rejected and the session continues.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo
list
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
    ____________________________________________________________
     A todo needs a description. Try: todo <description>.
    ____________________________________________________________
Goodbye!
```

## Test case: unknown-command

**Aim:** Verify that an unrecognised command is rejected without terminating Bro.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
blah
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
    ____________________________________________________________
     I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
    ____________________________________________________________
Goodbye!
```

## Test case: malformed-deadline

**Aim:** Verify that a deadline without a /by date gives a useful usage hint.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
deadline submit report
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
    ____________________________________________________________
     Use: deadline <description> /by <date>.
    ____________________________________________________________
Goodbye!
```

## Test case: malformed-event

**Aim:** Verify that an event missing its /to component gives a useful usage hint.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
event team sync /from 2pm
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
    ____________________________________________________________
     Use: event <description> /from <start> /to <end>.
    ____________________________________________________________
Goodbye!
```

## Test case: invalid-mark-index

**Aim:** Verify that a non-numeric mark index is reported clearly.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo read book
mark abc
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[T] [ ] read book
Now you have1 tasks in the list
    ____________________________________________________________
     Task number must be a positive whole number.
    ____________________________________________________________
Goodbye!
```

## Test case: out-of-range-mark

**Aim:** Verify that a mark index outside the task list is rejected.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo read book
mark 2
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[T] [ ] read book
Now you have1 tasks in the list
    ____________________________________________________________
     Task number must be between 1 and 1.
    ____________________________________________________________
Goodbye!
```

## Test case: invalid-unmark-index

**Aim:** Verify that unmark validates its task index too.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
todo read book
unmark two
bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
Got it. I've added: 

[T] [ ] read book
Now you have1 tasks in the list
    ____________________________________________________________
     Task number must be a positive whole number.
    ____________________________________________________________
Goodbye!
```

## Test case: blank-command

**Aim:** Verify that a blank line is handled as invalid input rather than a task.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text

bye
```

**Expected output:**
```text
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
    ____________________________________________________________
     Please enter a command.
    ____________________________________________________________
Goodbye!
```
