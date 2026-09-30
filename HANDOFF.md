# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter0** (foundation bot). Ladder grade: not yet rated.
- In flight: blind compile of 73 benchmark repos (driver); VM start retries (zone exhausted).
- Next: calibration block against every benchmark bot (two games each) once the VM is up; port the
  block-study pipeline (`scrim-study`) to 2024 metric columns and re-enable `test_metrics.py`.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
