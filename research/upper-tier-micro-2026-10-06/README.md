# Upper-tier micro study (2026-10-06)

A research agent read 15 g_iter6 control replays (12 upper-tier games, 10 of them losses, one map per opponent; 3 lower-band
wins as reference) with a scratch per-robot analyser (positions, HP, action and move cooldowns, actions; end-of-round
state). No repo files edited, no games run.

**Finding: we lose the action-cooldown tempo, against the upper tier only** (upper: us / them; lower: us / them):

| measure | upper | lower |
|---|---|---|
| contact turns (enemy within dist2 4 at turn start) where we were ready and struck | 0.27 / 0.44 (12 of 12) | 0.33 / 0.32 |
| their hits landing on a robot still on cooldown (>= 20) | 0.49 / 0.36 (12 of 12) | 0.41 / 0.40 |
| robots that stepped into enemy reach and were hit next round | 0.48 / 0.30 (12 of 12) | 0.36 / 0.35 |
| heals with an enemy within dist2 10 | 0.45 / 0.25 | 0.39 / 0.46 |
| robots near an enemy holding a ready action (9 games vs bots that beat us) | 0.11 / 0.34 (9 of 9) | parity |

- Heals under threat by opponent: jmerle 3%, andli28 8%, andrewgopher 12%, hsmalladi 14%; Gymhgy 40% and Cyril 46% heal
  under threat like us, and they are the two upper bots we beat about half the time (ready-held share vs Gymhgy 0.11 / 0.11).
- We heal 1.50 times per attack, they 0.98; a heal costs 30 cooldown; 9.8% of their attacks land on our robots that just
  healed (5.5% the other way).
- Confound: losing produces some of this. Against it: our win over jmerle (TreeSearch) still shows ready-held 0.08 vs 0.36
  and heals under threat 44% vs 3%; the lower band is at parity.
- No gap in stun follow-up (enemies caught in our stuns die within 4 rounds 15.8%, ours in theirs 13.9%).

**Proposed arms:** (1) HEAL_HOLD: no heal with an enemy within dist2 10 unless the target carries a flag (built as g6heal);
(2) RECHARGE_CLOSE: a robot one turn from ready ends its turn just outside reach instead of at dist2 11-20 (riskier; stack on
HEAL_HOLD if it delivers); (3) SPAWN_SAFE: avoid spawn zones with enemies near (59% of our spawns end next to an enemy vs
their 24%; 17% of our deaths within 10 rounds of spawning vs 2%; partly an outcome of losing the field).
New census columns healThreat10 and readyHeld20 test the signature over the 720-game control before the 5(a) is read.
