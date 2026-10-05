# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter4** (since 2026-10-04; code in `src/g_iter4`, and `src/bot` with its defaults plays the same).
  It is g_iter3 plus C.FLAG_LOST (second audit, research/AUDIT-2026-10-03.md, BOT1: captured own flags are recognised, so
  their defender, alerts and respawns stop serving an empty home). Seeded band pairs vs g_iter3, pooled over 473: upper-tier
  capture delta +0.24 +- 0.06 (t 3.8), all +0.14 +- 0.05 (t 3.1), net +9; basics PASS. Ladder: **1954 +- 23, rank 12**
  (converged fit since 2026-10-04, audit MEAS11: every rating ~46 higher than the old capped fit; gaps unchanged).
  g_iter3 (carrier stun + flag relocation V2, the waffle crack; research/CRACK-WAFFLE.md), g_iter2 (first audit), g_iter1.
- **Ladder target: Gymhgy.v10official** (2099, rank 9; g_iter4 78-103, 43%, over 181 filler games; CLAUDE rule 14,
  research/CRACK-GYMHGY.md) since 2026-10-04, when the CyrilSharma lines ran out (owner PROMPTS 177; g_iter4 36% over 2,098
  games; research/CRACK-CYRIL.md). Defeated: ColtG5 (declared by the owner, PROMPTS 157), winkelmantanner.waffle.
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- In flight: the Cyril target (research/CRACK-CYRIL.md; g_iter4 vs Cyril 456-803, 36%, on 1,259 filler games). In
  those games a Cyril capture decides most losses (MORE_FLAG_CAPTURES 406, CAPTURE 292) and we win flag ties on level
  sum 188-105. Second-audit follow-through: tools fixed; BOT1 promoted (g_iter4); closed or parked against Cyril: BOT2
  tether, BOT4 econ, BOT5 pick, BOT7 ahead, BOT8 fill, BOT9 alert (g4alert INCONCLUSIVE at 96), BOT10 pred, the g4bundle
  combination; BOT16 fixed behind C.RELOC_SPREAD (small effect, for a later combined build). **Cyril's stun economy
  (TACTICS T14, research/CRACK-CYRIL.md)**: it builds 475 traps a game to our 247 (census of 1,520 games), ~54 crumbs a stun
  to our ~77 (build-level discount), and takes 3x our centre crumbs in r201-400. g4front (placement) FAIL; g4wary/2
  (avoidance), g4crumb (INCONCLUSIVE at 96), g4builder/2 (dedicated builders) parked; **g4econ2** (centre crumbs + builders
  on top of everyone's stuns) passed Cyril delivery (fast stun victims +54%) but its band test met no criterion (net +3,
  upper -0.20, t -1.6; kills -17%), and its paired Cyril filler found no crack (net +1/120, stopped for futility).
  **Gymhgy.v10official is the target** (research/CRACK-GYMHGY.md; g_iter4 ~40% over 2,400+ games). Delivered: g4crumb
  (centre crumbs; 7-1 on chosen centre-crumb cells, +20/560 on random maps), g4dam (dam-line budget), g4gym1 (both; closed
  at +12/1,880, no measurable effect). Closed or parked: Z1HOLD, Z2ESCORT, pick, relay, level farm (g4farm2/3), convoy
  response (g4contact/g4contact8: fired, but screened rounds rose). **Now: g4ship1** (g4crumb + g4pick; owner PROMPTS 180):
  band delivery PASS once the kills guard was removed (PROMPTS 183), band test running, Gymhgy paired filler running.
  Open: BOT3(a) reachable relocation
  spots, BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; tools MEAS4, MEAS6-MEAS10, MEAS13-MEAS17 (MEAS3 fixed 2026-10-04: seeds in replay names).
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=CyrilSharma.finalBot tools/filler-pair.sh g_iter4 - 40`
  (the g_iter4 vs Cyril baseline), collected with `tools/collect-fillers.sh g_iter4 -`.
- **At the next promotion, refresh the band** (owner PROMPTS 184-185): re-derive the band list from the converged ratings
  so it includes uravt.Version18Final (absent since 2026-10-01; in tools/upper-tier.txt but never band-tested), and play
  the new incumbent's control on it.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
