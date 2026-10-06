# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter6** (since 2026-10-06; code in `src/g_iter6`, and `src/bot` with its defaults plays the same).
  It is g_iter5 plus g5climb2: C.RELOC_CLIMB (audit BOT3(a)): in setup the flag carrier steps each turn to the visible
  passable tile farthest from the nearest live enemy spawn (within 20 tiles of its spawn centre, clear of our other flags by
  priority) and drops at a local maximum, instead of walking to a fixed spot that was unreachable 23% of the time. 5(a) +3.56
  tiles on 12 of 12 cells vs Gymhgy; band delivery +2.5 tiles, enemy captures -27%; shipped at look 2: 480 seeded band pairs
  vs g_iter5, capture delta +0.23 +- 0.07 (t_all 3.44), upper +0.33 +- 0.10 (t_up 3.34; upper-tier wins 39 -> 60), wins net
  +20 (sign p 0.045); basics PASS. Ladder **1999 +- 23, rank 11** (Gymhgy 2000; g_iter5 1974); control on the band 413-307 over 720 games. Before it: g_iter5
  (the owner's stack: centre crumbs + pick-after-move), g_iter4, g_iter3, g_iter2, g_iter1. Before it: g_iter4 (+ C.FLAG_LOST), g_iter3, g_iter2, g_iter1.
- **Ladder target: Gymhgy.v10official** (2001, rank 10; g_iter5 46% over 1,912 games; CLAUDE rule 14, research/CRACK-GYMHGY.md)
  since 2026-10-04, when the CyrilSharma lines ran out (owner PROMPTS 177; research/CRACK-CYRIL.md). Defeated: ColtG5
  (declared by the owner, PROMPTS 157), winkelmantanner.waffle.
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- In flight (2026-10-06): **Gymhgy on the g_iter6 base.** g_iter5's losses to it were early (before r1000 302-123, almost
  all three-flag captures; from r1000 on 470-341). Ruled out by pinned premise checks: recall (R3: the fight at our flag is
  lost ~28 to 16), a flag-fight stun bank (F2: near-flag kills even; captured flags differ in contact), flag clustering (no
  pattern among the bots that beat us). Closed or parked: Z1HOLD, Z2ESCORT, relay, level farm (g4farm2/3), convoy response
  (g4contact/8), dam line (g4dam), the climb dose (g5climb3: local maxima bind, not the radius). Shipped: centre crumbs and
  pick-after-move (g_iter5), the relocation climb (g_iter6). Now: g_iter6's control on the refreshed band; the filler
  pairs g_iter6 with g_iter5 against Gymhgy (the climb's Gymhgy read). Cyril (research/CRACK-CYRIL.md): all lines neutral.
  Open audit items: BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; tools MEAS4, MEAS6-MEAS10,
  MEAS13-MEAS17.
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=Gymhgy.v10official tools/filler-pair.sh g_iter5 g_iter6 40` (g_iter6
  paired with g_iter5 against Gymhgy), collected with `tools/collect-fillers.sh g_iter5 g_iter6`. tools/vm-prune.sh keeps the newest 25 filler
  runs of each build in tools/keep-replays.txt (older filler replays go after an hour once the disk passes 80%).
- The band is refreshed at each promotion (owner PROMPTS 184-185): tools/band.txt holds the current band (a copy of tools/band-<date>.txt),
  tools/band-seeds.txt holds the incumbent's three look seeds, tools/upper-tier.txt the bots rated above it.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
