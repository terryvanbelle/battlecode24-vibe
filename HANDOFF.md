# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter4** (since 2026-10-04; code in `src/g_iter4`, and `src/bot` with its defaults plays the same).
  It is g_iter3 plus C.FLAG_LOST (second audit, research/AUDIT-2026-10-03.md, BOT1: captured own flags are recognised, so
  their defender, alerts and respawns stop serving an empty home). Seeded band pairs vs g_iter3, pooled over 473: upper-tier
  capture delta +0.24 +- 0.06 (t 3.8), all +0.14 +- 0.05 (t 3.1), net +9; basics PASS. Ladder: **1932 +- 39, rank 12**.
  g_iter3 (carrier stun + flag relocation V2, the waffle crack; research/CRACK-WAFFLE.md), g_iter2 (first audit), g_iter1.
- **Ladder target: CyrilSharma.finalBot** (2020, rank 11; g_iter3 10-14; CLAUDE rule 14). Defeated: ColtG5 (declared by
  the owner, PROMPTS 157), winkelmantanner.waffle (g_iter3 ranks above it; paired waffle read finishing on the filler).
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- In flight: the Cyril target (research/CRACK-CYRIL.md; g_iter4 27-37 so far); the filler pairs g_iter4 with g_iter3
  against Cyril. Second-audit follow-through: tools fixed (three-way delivery verdicts, discordance printed, basics on
  unmeasured bases, replay keep list); BOT1 promoted (g_iter4); BOT2 tether closed (powered FAIL); next BOT3
  (relocation abandons ~20% of flags), BOT5 (pickup timing), BOT4 (post-setup economy). Open tool findings: MEAS3
  (replay names lack the seed), MEAS4-MEAS17 (see the report).
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=CyrilSharma.finalBot tools/filler-pair.sh g_iter4 g4pick 40`
  (finishing the waffle read; next: the CyrilSharma pool), collected with `tools/collect-fillers.sh g_iter2 g_iter3`.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
