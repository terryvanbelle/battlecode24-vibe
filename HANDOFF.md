# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter5** (since 2026-10-05; code in `src/g_iter5`, and `src/bot` with its defaults plays the same).
  It is g_iter4 plus the owner's stack g4ship1 (PROMPTS 180): centre crumbs (C.CRUMB_STEP, C.POST_SETUP_CRUMBS) and
  pick-after-move (C.PICKUP_AFTER_MOVE). Shipped under the new rule (PROMPTS 186-188) at look 3: 720 seeded band pairs vs
  g_iter4, capture delta +0.17 +- 0.05 (t_all 3.25), upper t 1.98, wins net +23 (sign p 0.033); basics PASS; vs Gymhgy
  +84 over 1,200 pairs (+3.9 SE). Ladder **1985 +- 15, rank 11** (g_iter4 1939). Control on the refreshed band
  (tools/band.txt = band-20261005, look seeds in tools/band-seeds.txt): 387-333 over 720 games; new arms pair against it. Before it: g_iter4 (+ C.FLAG_LOST), g_iter3, g_iter2, g_iter1.
- **Ladder target: Gymhgy.v10official** (2011, rank 10; g_iter5 603-669, 47%; CLAUDE rule 14, research/CRACK-GYMHGY.md)
  since 2026-10-04, when the CyrilSharma lines ran out (owner PROMPTS 177; research/CRACK-CYRIL.md). Defeated: ColtG5
  (declared by the owner, PROMPTS 157), winkelmantanner.waffle.
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- In flight (2026-10-06): **Gymhgy on the g_iter5 base.** g_iter5's losses to it are early: before r1000 302-123, almost
  all three-flag captures; from r1000 on we win 470-341. Closed or parked against Gymhgy: Z1HOLD, Z2ESCORT, relay, level
  farm (g4farm2/3), convoy response (g4contact/8), dam line (g4dam; it cancelled the crumbs inside g4gym1). Delivered and
  shipped: centre crumbs and pick-after-move (g_iter5). Next: the recall premise check (ReplayDump --recall-d0,
  tools/recall-d0.py, routes pinned: free robots near a big grab -> L1 recall; robots fighting far away -> disengage
  design; else not the lever) on the g_iter5 filler's Gymhgy replays, and a flag-spread census (flagSpreadMin/Max) of the
  band: do the bots that beat us keep their flags together? Cyril (research/CRACK-CYRIL.md): all lines neutral.
  Open audit items: BOT3(a) reachable relocation spots, BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; tools MEAS4, MEAS6-MEAS10,
  MEAS13-MEAS17.
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=Gymhgy.v10official tools/filler-pair.sh g_iter5 - 40` (the g_iter5
  vs Gymhgy baseline), collected with `tools/collect-fillers.sh g_iter5 -`. tools/vm-prune.sh keeps the newest 25 filler
  runs of each build in tools/keep-replays.txt (older filler replays go after an hour once the disk passes 80%).
- The band is refreshed at each promotion (owner PROMPTS 184-185): tools/band.txt points at the current band file,
  tools/band-seeds.txt holds the incumbent's three look seeds, tools/upper-tier.txt the bots rated above it.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
