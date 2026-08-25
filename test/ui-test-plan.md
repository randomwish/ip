# Console UI test plan

This plan is the source of truth for console UI behavior. The runner starts a
fresh application process for each test case, compares output exactly, and
stops as soon as one case fails.

## Test environment

- **Setup command:** `javac -d out/production src/main/java/*.java`
- **Timeout seconds:** `5`
- **Output matching:** Exact, including blank lines and spaces. Line-ending
  differences between Windows and Unix are ignored.
- **Path placeholders:** `{workspace}` expands to the repository's absolute path and
  `{test_dir}` expands to an isolated temporary working directory for that case.
- **Restart inputs:** An optional fenced block runs the same command again in the
  same temporary directory so saved data can be checked after a restart.

## Test case: greeting-and-exit

**Aim:** Verify that the application shows its greeting and exits cleanly.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Aim:** Verify that a deadline command stores an ISO date and displays it in a friendly format.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline submit report /by 2019-10-15
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

[D] [ ] submit report(by: Oct 15 2019)
Now you have 1 tasks in the list
1. [D] [ ] submit report(by: Oct 15 2019)
Goodbye!
```

## Test case: deadline-date-time-add-and-list

**Aim:** Verify that a day/month/year deadline time is parsed and displayed in a friendly format.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
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

[D] [ ] return book(by: Dec 2 2019 6:00PM)
Now you have 1 tasks in the list
1. [D] [ ] return book(by: Dec 2 2019 6:00PM)
Goodbye!
```

## Test case: deadline-leap-day

**Aim:** Verify that a valid leap-day deadline is accepted and formatted correctly.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline submit tax return /by 2020-02-29
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

[D] [ ] submit tax return(by: Feb 29 2020)
Now you have 1 tasks in the list
1. [D] [ ] submit tax return(by: Feb 29 2020)
Goodbye!
```

## Test case: deadline-midnight

**Aim:** Verify that 24-hour midnight is formatted as 12:00AM.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline reset password /by 2/12/2019 0000
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

[D] [ ] reset password(by: Dec 2 2019 12:00AM)
Now you have 1 tasks in the list
1. [D] [ ] reset password(by: Dec 2 2019 12:00AM)
Goodbye!
```

## Test case: deadline-persists-after-restart

**Aim:** Verify that a typed deadline and its completion state survive a chatbot restart.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
mark 1
bye
```

**Restart inputs:**
```text
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

[D] [ ] return book(by: Dec 2 2019 6:00PM)
Now you have 1 tasks in the list
Ok this item is marked!
[X] return book
Goodbye!
  ____                
 | __ )  _ __   ___   
 |  _ \ | '__| / _ \ 
 | |_) || |   | (_) | 
 |____/ |_|    \___/  

Hello, I'm Bro! What drink do you want?
1. [D] [X] return book(by: Dec 2 2019 6:00PM)
Goodbye!
```

## Test case: event-add-and-list

**Aim:** Verify that an event command stores and lists both event times.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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
     I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
    ____________________________________________________________
Goodbye!
```

## Test case: malformed-deadline

**Aim:** Verify that a deadline without a /by date gives a useful usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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
     Use: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ____________________________________________________________
Goodbye!
```

## Test case: invalid-deadline-date

**Aim:** Verify that an invalid calendar date is rejected without adding a task.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline return book /by 31/2/2019 1800
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
     Use: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ____________________________________________________________
Goodbye!
```

## Test case: invalid-iso-deadline-date

**Aim:** Verify that a non-leap-year ISO date is rejected.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline submit tax return /by 2019-02-29
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
     Use: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ____________________________________________________________
Goodbye!
```

## Test case: invalid-deadline-time

**Aim:** Verify that an invalid 24-hour time is rejected.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 2460
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
     Use: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ____________________________________________________________
Goodbye!
```

## Test case: malformed-event

**Aim:** Verify that an event missing its /to component gives a useful usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

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

## Test case: delete-middle-task

**Aim:** Verify that delete removes the selected task and renumbers the remaining list.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
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
Got it. I've added: 

[D] [ ] return book(by: Jun 6 2019)
Now you have 2 tasks in the list
Got it. I've added: 

[E] [ ] project meeting(from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list
Noted. I've removed:
[D] [ ] return book(by: Jun 6 2019)
Now you have 2 tasks in the list
1. [T] [ ] read book
2. [E] [ ] project meeting(from: Aug 6th 2pm to: 4pm)
Goodbye!
```

## Test case: delete-missing-index

**Aim:** Verify that delete without a task number gives a usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
todo read book
delete
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
     Use: delete <task number>.
    ____________________________________________________________
Goodbye!
```

## Test case: delete-out-of-range

**Aim:** Verify that delete rejects an index beyond the current task list.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
todo read book
delete 2
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

## Test case: delete-empty-list

**Aim:** Verify that delete on an empty list reports the problem without crashing.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production Bro`

**Inputs:**
```text
delete 1
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
     There are no tasks to delete yet.
    ____________________________________________________________
Goodbye!
```
