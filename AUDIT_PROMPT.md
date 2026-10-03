# Correctness audit, then follow-through

Run the correctness audit. The full procedure is in `AUDIT_PLAYBOOK.md`, next to `TRAINING_ALGORITHM.md`. If that file is missing, follow this prompt.

**When.** A basic has turned out to be broken, or progress has stalled. Treat the defect as a sample of others at the same level. If something can be known exactly after a bounded number of observations, guessing it is a bug. Compute that bound offline over every map and side, and set the bar from it.

**Audit (read-only: no edits, commits or new games).** Scope: the incumbent, the working bot, every live arm, any fix already started, and every tool a verdict rests on. If replays are thin, queue a diagnostic batch first. Five parallel lens auditors:
1. dead code;
2. rules and engine API;
3. shared state and coordination;
4. in-game behaviour from replays;
5. the measurement pipeline.

Each finding needs an ID, evidence (file:line, replay output, rule line or harness output), quantified impact, a fix and a regression test. New verifiers try to refute every finding and reject any they cannot reproduce; match verdicts by ID, and report a finding with no verdict as UNVERIFIED, never drop it. A synthesizer writes a dated report ranked by impact: bot defects, tool defects, and systematic tests for the class. Commit and push it.

**Follow-through, in order:**
1. Fix the tool defects that decide verdicts first, each with a test. Prove the fix with an identity control: a byte-identical copy of the incumbent must read 0 discordant pairs on every tool path and opponent pool that produces verdicts (a pass on a sibling harness proves nothing). Mark VOID the verdicts that rested on broken tools or on mechanisms that never fired.
2. Triage by severity: every critical and major bot defect (and any minor one touching a basic) gets its own switch, off by default, with a census column and tests, and a diagnostic game showing it firing; the other minor ones go on the open list.
3. Build one combined arm from those fixes, pass the delivery gate, and judge it against the incumbent with exact seed-paired tests. Confirm on fresh seeds before promoting; on promotion relabel the arm's games under the incumbent's name and refit.
4. Install guards:
   - a basics battery on every build; every relative check, there and in the delivery gate, fails only when the build is worse by more than 2 (paired) SE, never on a point ratio;
   - dead-code and switch-intent checks in the unit tests;
   - shared-state contract columns.
5. Track open findings in `HANDOFF.md` until each is fixed or waived.
6. Re-test discarded ideas on the fixed base under a pre-registered rule (one second attempt after a traced failure, on a fresh seed; beware a small block where the base happened to score low).
7. Amend `TRAINING_ALGORITHM.md` in the same pass: the audit is the first step of plateau escalation; paired cells share one engine seed for both builds; day one runs the identity control and the basics battery.

**Hard rules.** Follow the project's rules in `CLAUDE.md`, and name the forbidden paths in every agent's prompt. Never read other teams' code or this year's post-mortems.