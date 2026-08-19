---
name: test-ui
description: Run repeatable end-to-end tests for this project's console user interface from test/ui-test-plan.md. Use when adding, checking, or debugging console commands and their exact expected output, especially when a test session must stop at the first failure and show its input/output transcript.
---

# Test UI

Use this skill to run the console UI tests documented in
`test/ui-test-plan.md`. The plan is the source of truth: keep its test cases
up to date whenever the console interface changes.

## Test-plan format

Keep the following information in `test/ui-test-plan.md`:

- A test-environment section with an optional setup command and timeout.
- One `## Test case: <id>` section for every independent session.
- For each test case: an aim, the command that launches the program, the
  console inputs, and the exact expected console output.

Use fenced code blocks for inputs and expected output. Each case starts the
program in a fresh process, so do not rely on state from an earlier case.

```markdown
## Test environment

- **Setup command:** `javac -d out/production src/main/java/*.java`
- **Timeout seconds:** `5`
- **Output matching:** Exact, including blank lines and spaces.

## Test case: greeting-and-exit

**Aim:** Verify that the application greets the user and exits cleanly.

**Run command:** `java -cp out/production Bro`

**Inputs:**
```text
bye
```

**Expected output:**
```text
... exact console output ...
```
```

## Run the tests

1. Ensure Java 25 is active before running the setup or application command.
2. Update the plan before testing if commands or output changed.
3. Run the harness from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

   Pass `--plan path/to/plan.md` to use a different plan.

4. Show the harness transcript in the response. It records each program
   command, the input lines, and the actual console output.

The harness compares output exactly (apart from Windows versus Unix line
endings). It stops at the first failing case and prints the actual output,
expected output, exit status, and a unified diff. Do not continue with later
test cases after a failure.

## Maintain the plan

Use a descriptive test-case id and an aim that states the behavior under test.
Keep expected output intentionally exact, including banners, prompts, blank
lines, and task-list formatting. If the user experience is intentionally
changed, update both the implementation and the expected output in the same
change.
