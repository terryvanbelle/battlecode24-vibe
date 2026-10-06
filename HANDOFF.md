# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter7** (since 2026-10-06; code in `src/g_iter7`, and `src/bot` with its defaults plays the same).
  It is g_iter6 plus g6heal: C.HEAL_HOLD (TACTICS T16, research/upper-tier-micro-2026-10-06): with an enemy within dist2 10 a
  robot keeps its action for a strike instead of healing (flag carriers still healed). The upper tier healed under threat
  26% of the time to our 45% and held a ready strike 0.32 to our 0.20 (720-game census). Band delivery: heals under threat
  0.13 vs 0.44, ready held 0.33 vs 0.22; shipped at look 1: 240 seeded band pairs vs g_iter6, wins 136 -> 164 (net +28,
  36-8, p < 0.001), capture delta +0.57 +- 0.09 (t_all 6.46), upper +0.72 +- 0.14 (t_up 5.31); basics PASS. Ladder **2150
  +- 29, rank 6** (960 games; control on the band 514-206 over 720; g_iter6 1992). Before it: g_iter6 (the relocation climb, audit BOT3(a)), g_iter5
  (the owner's stack: centre crumbs + pick-after-move), g_iter4, g_iter3, g_iter2, g_iter1.
- **Ladder target: andli28.v9_USQuals_angle** (2174, rank 5, the bot just above g_iter7; g_iter7 14-34 against it).
  Defeated: ColtG5 (declared by the owner, PROMPTS 157), winkelmantanner.waffle, and on 2026-10-06 Gymhgy.v10official,
  CyrilSharma.finalBot, hsmalladi.finalbot, andrewgopher.player22 and NotLLeon.v3 (all rated below g_iter7).
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- In flight (2026-10-06): **the new target andli28**; g7spawn (C.SPAWN_SAFE) 5(a) queued. Closed today: recall (R3),
  flag-fight stun bank (F2), flag clustering, the climb dose (g5climb3), territory-aware micro (g6terr, T15). Shipped today:
  the relocation climb (g_iter6) and the heal hold (g_iter7). Open proposals from the upper-tier micro study: RECHARGE_CLOSE
  (a robot one turn from ready ends just outside reach) and SPAWN_SAFE (avoid spawn zones with enemies near).
  Open audit items: BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; tools MEAS4, MEAS6-MEAS10,
  MEAS13-MEAS17.
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=andli28.v9_USQuals_angle tools/filler-pair.sh g_iter7 - 40` (g_iter7's
  baseline against the target), collected with `tools/collect-fillers.sh g_iter7 -`. tools/vm-prune.sh keeps the newest 25 filler
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
