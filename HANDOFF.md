# HANDOFF.md

## State (update at every accept)

- **Project shut down 2026-10-07 ~13:10 UTC** (owner, PROMPTS 192). Final: g_iter7 2113 +- 25, rank 6 of 88, field score
  88.6%, 30.5% against the 5 bots above it. The target andli28 was not cracked (2187; g_iter7 1739-4109). Lessons of the
  week: LEARNINGS.md. Resume steps at the end of this section.
- Incumbent: **g_iter7** (since 2026-10-06; code in `src/g_iter7`, and `src/bot` with its defaults plays the same).
  It is g_iter6 plus g6heal: C.HEAL_HOLD (TACTICS T16, research/upper-tier-micro-2026-10-06): with an enemy within dist2 10 a
  robot keeps its action for a strike instead of healing (flag carriers still healed). The upper tier healed under threat
  26% of the time to our 45% and held a ready strike 0.32 to our 0.20 (720-game census). Band delivery: heals under threat
  0.13 vs 0.44, ready held 0.33 vs 0.22; shipped at look 1: 240 seeded band pairs vs g_iter6, wins 136 -> 164 (net +28,
  36-8, p < 0.001), capture delta +0.57 +- 0.09 (t_all 6.46), upper +0.72 +- 0.14 (t_up 5.31); basics PASS. Final
  ladder **2113 +- 25, rank 6 of 88** (field score 88.6%, 30.5% vs the 5 bots above; control on the band 514-206 over 720;
  g_iter6 1969; each pair counts at most 200 games in the fit since PROMPTS 191). Before it: g_iter6 (the relocation
  climb, audit BOT3(a)), g_iter5 (the owner's stack: centre crumbs + pick-after-move), g_iter4, g_iter3, g_iter2, g_iter1.
- **Ladder target at shutdown: andli28.v9_USQuals_angle** (2187, rank 5, the bot just above g_iter7; g_iter7 1739-4109
  against it, 30%). Not cracked (research/CRACK-ANDLI28.md).
  Defeated: ColtG5 (declared by the owner, PROMPTS 157), winkelmantanner.waffle, and on 2026-10-06 Gymhgy.v10official,
  CyrilSharma.finalBot, hsmalladi.finalbot and andrewgopher.player22 (all still below g_iter7: andrewgopher 2092, hsmalladi
  2052, Gymhgy 1979, Cyril 1967, waffle 1855, ColtG5 1718). NotLLeon.v3 was passed on 2026-10-06 and is level at shutdown:
  2113 +- 36 (rank 7) to g_iter7's 2113; g_iter7 48-40 head to head.
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC.
  That makes a pair exact only for code that changes no decision (identity control 0 of 80 discordant); arms that change
  behaviour still disagree with the control on ~17-29% of pairs (audit 2026-10-03 MEAS2), so size blocks from that.
  Delivery checks are three-way since the second audit: PASS 1 SE beyond the bar, FAIL 2 SE short, else INCONCLUSIVE
  (auto-extended 24 -> 48 -> 96 cells, never a closure). Open findings of research/AUDIT-2026-10-03.md are tracked
  below.
- Nothing is in flight (shutdown). Last results, 2026-10-07:
  - **g7kiterc** (g7kite's kite out of reach below 700 HP + C.RC_BAND 150, a supported one-step hold at HP >= 700 so the
    next strike lands; research/upper-tier-study-2026-10-07): diagnostic PASS, then its 12-cell 5(a) FIRED on every
    pre-registered bar. Its band delivery (`BASE=g7kite DGTAG=-kiterc tools/delivery-gate.sh g7kiterc '...'`, bars in
    TRAINING_LOG) and band test are registered but were never run. It is the open bot lever.
  - **Vertical-map penalty** against andli28: we win 0.19 on vertical-symmetry maps vs 0.38 on rotational ones (5,720 games,
    replicated out of sample); against the band not. Its study (workflow) was stopped at shutdown before any lens finished;
    research/vertical-study-2026-10-07 holds only maps.txt. The mechanism is unidentified; it is the top open lead.
  - Parked 2026-10-06/07: g7kite alone (band pooled t 0.32; andli28 +6 over 240 pairs), g7ehp (step-in gate: +28 over 240
    paired games against andli28, 49-21, +3.3 SE; band look 1 STOP, capture t -1.12), g7ehp2 (band delivery INCONCLUSIVE at
    192 cells, no band test), g7fc (final level completion, rare trigger; stack candidate). Closed: g7dig, g7bank, g7ring,
    g7spawn. Saved robots do not become captures on their own (g7ehp, g7kite).
  - Open audit items: BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; tools MEAS4, MEAS6-MEAS10, MEAS13-MEAS17.
- VM: `battlecode-dev2` in us-west2-a, **stopped at shutdown** (the original `battlecode-dev` in us-west1-b is stopped
  and untouched; its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue at shutdown: the queue runner is stopped and the /loop task check is cancelled. The filler job
  (`FILLPOOL=andli28.v9_USQuals_angle tools/filler-pair.sh g_iter7 - 40`, g_iter7's baseline against the target) is parked
  as queue/filler.job.stopped on the VM. When running, `queue/pending/*.job` run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`) and fillers are collected with `tools/collect-fillers.sh g_iter7 -`. tools/vm-prune.sh
  keeps the newest 25 filler runs of each build in tools/keep-replays.txt (older filler replays go after an hour once the
  disk passes 80%).
- The band is refreshed at each promotion (owner PROMPTS 184-185): tools/band.txt holds the current band (a copy of tools/band-<date>.txt),
  tools/band-seeds.txt holds the incumbent's three look seeds, tools/upper-tier.txt the bots rated above it.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.
- To resume: (1) `source tools/vm.sh && ensure_vm` starts the VM (tools/vm.sh is sourced, not run); (2) on the VM, in
  ~/projects/vibe/2024, `mv queue/filler.job.stopped queue/filler.job`; (3) `tools/vm-run.sh queue-runner
  'tools/vm-queue.sh'`; (4) restart the task-check loop. The next registered step is g7kiterc's band delivery, then its
  band test.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
