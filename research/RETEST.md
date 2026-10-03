# RETEST: closed directions to re-test on g_iter2

Written 2026-10-03 (owner prompt 150: "now that you've fixed some bugs, old approaches that you discarded are worth revisiting"). Three reviews were merged into one queue: bug interaction, statistical noise and strategic fit. Nothing in this file has been run yet. Every switch below was checked against `src/bot/C.java` and `src/bot/Sym.java`. Name, type and default are as stated. Every delivery column was checked against the census header (`ReplayDump --capabilities`) or the survey header (`--survey`).

## Why these verdicts can be re-opened

- **Exact-harness verdicts still stand as measurements.** These are the arch_rush10 mirror SPRTs from `paired.sh`: they shared the seed, and the z1copy identity control read 0-0. They are re-tested only where a fixed defect changed what the switch does. That applies to d1def2, d2alert, d3fort and n1flagstun, all through A1.
- **Unseeded verdicts are diluted, not biased.** This covers every band, filler and delivery block before 2026-10-02 23:00 UTC. Identical code flipped 15-29% of cells, so the sign tests stay valid but have little power.
  - A result at |z| >= 2.5 stands.
  - A result at |z| < 2 is no evidence either way.
  - Absolute 24-game bars were dominated by noise. b1z2b's mini-block read 9.2 and then 4.83 on the same cells.
- **Single diagnostic games, step 5(a), carry no statistical weight.**
- **The defects fixed in g_iter2 sat on most of these paths:**
  - A1: false own-flag alert. It switched off defend() and parked ducks on flag tiles.
  - A3: guessed symmetry, wrong on 32% of map-sides.
  - A4: an unreachable enemy took the whole turn.
  - A5/A6: stale enemy-flag registry.
  - A7: nav lost its bug state whenever the target moved.
- **Seeded band measurements, g_iter1 -> g_iter2:**

  | column | g_iter1 | g_iter2 |
  |---|---|---|
  | alertNoThreat | 467 | 22 |
  | maxParkOnHome | 325 | 29 |
  | efStaleCarry | 169 | 15 |
  | stillPost | 36.0 | 28.1 |
  | pickups | 7.76 | 13.39 |
  | carrierDeaths | 6.60 | 11.70 |
  | captured | 1.05 | 1.53 |

  The fixes did not move these targets: enemyRegrabs 10.00 -> 10.81, chasers20 2.99 -> 2.90, enemyFirstGrabs 6.96 -> 6.72.

## Protocol (every arm)

1. **Build.** The package is a copy of `src/bot`, whose defaults play g_iter2, with only the listed constants changed.
   - Add one line per constant to `tools/arm-intent.txt`, because `test_tools.py` checks them.
   - Run `tools/unit-tests.sh` (rule 6).
   - Arms are independent and all sit on g_iter2. Combining arms that advance is the stack's job.
2. **Step 5(a), diagnostic (rule 5).** Use `tools/diag-batch.sh` against our own builds only: the g_iter2 mirror, plus arch_rush10 (our rush build) for the flag-defence arms.
   - The counter named in the arm's section must fire.
   - No 5(b) block runs before it does.
3. **Step 5(b), delivery.** Run `BASE=g_iter2 tools/delivery-gate.sh <arm> '<checks>'` at the defaults N=24, SEED=909090. These cells are seeded, and the g_iter2 base block is played once and cached.
   - **Too few shared cells (audit B6):** the result line prints "on K shared cells". If K < 20 of 24, the block is void and is re-run.
   - **Overrun or exception:** the block is void, not failed. Fix it and re-run; it does not count toward the two-failure close.
   - **Second attempt:** allowed only after a 5(a) trace explains the first failure. It runs on a fresh SEED (for example 919191), because the same seed replays the same games.
   - **Bars are fixed now.** Where an arm had pre-registered bars, they are kept unchanged and only guards are added. Never move a bar after seeing a number.
   - **PASS is keyed by package name (audit B4).** Any code change after a PASS voids it.
   - **ColtG5 blocks (DGPOOL / DGTAG):** these overwrite `gauntlet/delivery-<arm>.PASS/FAIL` and `dg-census-<arm>.csv`, because neither file name carries DGTAG. Run them first and copy their outputs aside. The band block runs last, since `band-test.sh` reads that file.
4. **Band test.** Run `tools/band-test.sh <arm>`: seeds 515151 and 616161, 120 games each, seeded. Then evaluate:
   `python3 tools/eval-paired.py research/census-g1basics-seeded.csv gauntlet/census-<arm>.csv 20261003-002104-scrim-g1basics,<arm run 515151> 20261003-003405-scrim-g1basics,<arm run 616161>`
   The g1basics seeded band runs are g_iter2's control, because the code is identical.
5. **Decision rule.** SE(net) = sqrt(gained + lost), as in TRAINING_LOG (for example, +33 on 55 discordant = +4.4 SE).
   - **Adopt as a stack candidate** if the all-cell net is >= +2 SE, or if the upper-tier capture delta is >= +2 SE (its t) with an all-cell net >= -5.
   - **Close for good** if the delivery gate fails twice, or if the band all-cell net is <= -2 SE.
   - **Otherwise park it.** A parked arm gets no more solo runs and can come back only as a stacking partner.
   - **Confirmation:** before it joins the stack, a candidate runs `SEEDS="717171 818181" TAG=-conf tools/band-test.sh <arm>`. It is compared with the g1basics confirmation runs (20261003-014247 and 20261003-015453; `research/census-g1basics-conf-seeded.csv`). The pooled result must still meet (a) or (b), as g1basics did.
6. **Order and throughput.** Delivery blocks are cheap (24 games against a cached base), so enqueue the 5(a) batches and deliveries in list order. Band tests (240 games each) run in list order as PASS files arrive. Never run two experiments side by side (rule 12). g2z2, g2bc and g2nonav are already queued.

## Queue

Order: the sum of the three lens priorities, with ties going to the cheaper arm.

| # | package | switches on g_iter2 | original verdict | lens scores (bug/noise/fit) |
|---|---|---|---|---|
| 1 | g2z2 | Z2ESCORT=true, Z2_ESC_R2=8 | g1z2 delivery 0.74 vs bar 0.70 (unseeded); b1z2b -7.5 SE re-grabs | 8 / 9 / 9 |
| 2 | g2icamp | INTERCEPT=true, DEST_CAMP=true | g1icpt delivery FAIL (unseeded); g1camp/g1icamp not reproduced on single games | 9 / 8 / 8 |
| 3 | g2fstun | FLAG_TILE_STUN=true | exact 20-25 (z -0.75); band 19-23 unseeded | 6 / 7 / 6 |
| 4 | g2escrg | ESCORT_CARRIER, ESCORT_BEHIND, REGRAB =true | delivery FAIL on 2 of 4 bars (unseeded) | 7 / 6 / 6 |
| 5 | g2rgh | REGRAB=true, REGRAB_HALF=true | parked after 2 games; parent b2rg 11-26 | 5 / 4 / 7 |
| 6 | g2alert400 | ALERT_RADIUS2=400 | exact 12-31 (z -2.9), under A1 | 7 / 3 / 5 |
| 7 | g2water | WATER_WHEN_WEAK=true | 49-38 unseeded (p ~ 0.24), kept off | 3 / 7 / 4.5 |
| 8 | g2up3 | UPGRADE_ORDER=3 | closed on 3 single games | 4 / 6 / 3 |
| 9 | g2def2 | DEFENDERS_PER_FLAG=2, Sym.SCOUT_FIRST=6 | exact 9-25 (z -2.7), under A1 | 5 / 4 / 3.5 |
| 10 | g2fort | RING_RADIUS2=13, DEF_TRAP_RESERVE=0 | exact 16-29 (z -1.94), under A1 | 4 / 5 / 2.5 |
| 11 | g2csafe | CARRY_SAFE=true, CARRIER_HEAL=true | parked after 2 games on the b2rg base | 2 / 5 / 4 |
| 12 | g2z1 | Z1HOLD=true | absolute-bar delivery FAIL (unseeded) | - / 4 / 5 |

### 1. g2z2: escort-first targeting (already built and queued; do not queue again)
- **Switches:** `C.Z2ESCORT=true` (boolean, default false) and `C.Z2_ESC_R2=8` (int, default 2). Built as `src/g2z2`, which differs from `src/bot` only in these two lines and is listed in `arm-intent.txt`.
- **Original verdict:**
  - On B1, b1z2b band: opponent re-grabs -4.79 +- 0.64 (-7.5 SE), wins 17-18. Filler B2 vs B1: 83-56 over 757 pairs (+2.3 SE).
  - On g_iter1, g1z2 delivery FAIL (unseeded): enemyRegrabs 8.33 vs 11.25, ratio 0.74 against a bar of 0.70. The miss was about 0.2 SE of its +-2.3. postPickups passed.
  - Closed with the flag-pressure family under the 3-rejects rule.
- **Why re-test:**
  - **A3 sat on the rule itself.** `Micro.nearTheirSpawn` (Micro.java:14) exempts a carrier within dist2 36 of a `Sym.enemyCenters()` centre, and that centre was a guess. g_iter2's symWrong is 0.
  - **The g_iter1 miss was inside unseeded noise.**
  - **A1 kept field ducks parked on our flag tile,** away from the fights over our dropped flags.
  - **The target is larger on g_iter2.** enemyRegrabs is 10.81 a game on the seeded band; hsmalladi 28.2, NotLLeon 27.5 and winkelmantanner 24.6 a game (12 games each). 64% of enemy captures come from re-grab or relay chains (B3 band).
- **5(a):** the `es` counter (Micro.escortHits) fires on at least 2 maps.
- **Delivery (as queued, unchanged):** `BASE=g_iter2 tools/delivery-gate.sh g2z2 'rel:enemyRegrabs<=0.7 rel:postPickups>=0.8'`. These are the bars b1z2b passed and g1z2 missed.

### 2. g2icamp: carrier interception in fights plus destination camping
- **Switches:** `C.INTERCEPT=true` and `C.DEST_CAMP=true` (both boolean, default false). `C.INTERCEPT_R2` 225, `C.CARRY_FRESH` 5 and `C.CHASE_RADIUS2` 225 (int) stay at their defaults.
- **Original verdict:**
  - g1icpt delivery FAIL (unseeded): chasers20 2.40 vs 2.31 (+0.09 +- 0.43). Its bar of x1.2 sat about 1 SE above the noise.
  - g1camp (4 diagnostic cells) and g1icamp (6 cells): enemy captures 2 vs 2, "not reproduced", never gated.
  - Family closed: "local rules that only see a fight do not reach the carriers nobody sees".
- **Why re-test:** four fixed defects sat on this path.
  - **A3:** `carrierTarget` and `campTarget` (Duck.java:404, 426) send far ducks to `G.nearest(sighting, Sym.enemyCenters())`, which was the wrong centre on about a third of map-sides, Tunnels included. Tunnels was one of the 5(a) maps.
  - **A4:** the Tunnels trace that sank g1camp shows the ducks near the pickup "fighting" enemies they could not reach. On Tunnels, 36% of enemy-in-view robot-rounds were such locks. With REACH_FIX those turns reach fieldTarget, where `carrierTarget` and `campTarget` run.
  - **A7:** a chase is `Nav.moveTo` toward a moving point, which is the audit's named caller.
  - **A1:** responders walked onto the flag tile.
- **The target is still open on g_iter2:**
  - On the band, chasers20 went 2.99 -> 2.90 and unopposedCaps 1.09 -> 0.97.
  - On 105 seeded ColtG5 pairs, enemyCaptured went 1.57 -> 1.43 and longCaps25 1.49 -> 1.42.
  - ColtG5's unopposed carriers convert 88% of their trips, against 51% when contacted; 56% of its captured trips are unopposed (CRACK.md).
  - This is the cheapest stand-in for CUT.
- **Residual risk:** A11(a) is not fixed. OF_CARRY and OF_LOC are never cleared, so `campTarget`, which is also the in-fight intercept goal, can hold ducks for up to 2·dist+10 rounds after our flag is home.
  - 5(a) must check for this.
  - If camps outlive the flag's return, fix A11(a) behind `C.DEST_CAMP` before 5(b). Play at g_iter2 defaults is unchanged by that fix.
  - Audit B7: the `ch` and `camps` counters also count jailed turns, so read intercepts and robot traces instead.
- **5(a):** the `ch<chases>/<intercepts>/<camps>` counters, on Tunnels, Battlecode24, DefaultMedium and DefaultLarge. Trace one unseen carrier walk.
- **Delivery:**
  - **ColtG5 block first; it is recorded and does not gate:** `DGPOOL=ColtG5.Goob_final DGTAG=-colt BASE=g_iter2 tools/delivery-gate.sh g2icamp 'rel:chasers20>=1.2 rel:enemyUnseenRounds<=0.8 rel:unopposedCaps<=0.8 rel:kills>=0.9 mean:overruns<=0'`
  - **Gate:** `BASE=g_iter2 tools/delivery-gate.sh g2icamp 'rel:chasers20>=1.2 rel:enemyUnseenRounds<=0.8 rel:enemyFirstGrabs<=1.2 rel:kills>=0.9 mean:overruns<=0'`
  - chasers20 and enemyFirstGrabs keep g1icpt's bars. The kills guard is 0.9 because the mechanism takes ducks out of fights on purpose.

### 3. g2fstun: a stun trap on each home flag tile
- **Switches:** `C.FLAG_TILE_STUN=true` (boolean, default false).
- **Original verdict:** exact SPRT vs arch_rush10, INCONCLUSIVE 20-25 after 96 pairs (z -0.75). Band, 2 seeds, unseeded: 19-23, REJECT. Ledger: "bodies/traps at the flag", to be re-opened "on a stated difference from all four".
- **Why re-test:** the stated difference is A1.
  - The trap is built and rebuilt only in `defend()` (Duck.java:563).
  - Under A1 a defender ran `defend()` only while no flag had a fresh alert. Alerts were fresh in 1057-1686 of the 1800 post-setup rounds. On b2rg-Battlecode24, defenders were in `defend()` for 220 of 1800 rounds, and only 1 of 5 triggered ring explosives was ever rebuilt.
  - So both tests priced a trap that was effectively setup-only.
  - Under ALERT_FIX each defender gates on its own flag (Duck.java:65).
  - It costs no bodies and no bytecode.
  - Rules: a stun fires at the end of the raider's turn, usually after its pickup, and freezes the new carrier (cooldowns 40) beside our flag. Expect more carrier kills and fewer captures, not necessarily fewer first grabs.
- **5(a):** src/bot's indicator has no `fs` counter (n1flagstun's had one). Add `" fs" + Duck.flagTileTraps` to the arm's indicator (instrument only), or read STUN builds on the home tiles with `replay-dump --from/--to`. Play vs arch_rush10 and the g_iter2 mirror. Rebuilds must show after r400.
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2fstun 'rel:enemyCarrierKills>=1.1 rel:enemyCaptured<=0.9 rel:kills>=0.95 mean:overruns<=0'`

### 4. g2escrg: convoy (escort behind our carrier, plus re-grab)
- **Switches:** `C.ESCORT_CARRIER=true`, `C.ESCORT_BEHIND=true` and `C.REGRAB=true` (all boolean, default false). `C.ESCORT_R2` 20 and `C.REGRAB_R2` 13 (int) stay at defaults, and `C.REGRAB_HALF` stays false.
- **Original verdict:**
  - 7 diagnostic cells: regrabs 86 vs 15, captured 5 vs 3, enemyCaptured 4 vs 2, wins 4/7 vs 6/7.
  - Delivery FAIL (unseeded): regrabs 9.08 vs 1.46 ok, escorts20 4.37 vs 4.46 FAIL, captured 1.04 vs 1.21 FAIL, enemyCaptured ok.
- **Why re-test:**
  - **A5:** every dead carrier left its flag marked "carried by us" (1052 rounds on b2rg-Battlecode24), which took the flag out of `fieldTargetFrom`. A re-grab arm multiplies carrier deaths, so it was hit hardest.
  - **A6:** the army walked to stale drop tiles.
  - **A4:** with only unreachable enemies in view, would-be escorts sat in the fight branch. Those turns now reach fieldTarget's own escort step, which NAV_FIX (A7) lets follow a moving carrier.
  - **A1:** parked ducks could not escort.
  - **Conversion is now the binding gap:** g_iter2 nearly doubled pickups and carrier deaths, but captures rose only 1.05 -> 1.53.
  - **The captured miss (-0.17)** was about 0.7 of the stated 24-game noise (+-0.25), on unseeded cells.
- **5(a):** the `et` and `rg` counters both fire, on the 7 g1escrg cells.
- **Delivery (original bars plus guards):** `BASE=g_iter2 tools/delivery-gate.sh g2escrg 'rel:regrabs>=2.0 rel:escorts20>=1.1 rel:captured>=1.0 rel:enemyCaptured<=1.3 rel:kills>=0.95 mean:overruns<=0'`. regrabs is kept at 2.0 even though g_iter2's base is 3.18: g1escrg added +7.6.

### 5. g2rgh: re-grab loose enemy flags on our half only
- **Switches:** `C.REGRAB=true` and `C.REGRAB_HALF=true` (boolean). `C.REGRAB_R2` 13 stays. This is built on g_iter2 directly; b2rgh also carried BUDGET_V1 and Z2ESCORT.
- **Original verdict:**
  - Parent b2rg (re-grab anywhere), band, unseeded: 11-26. regrabs +4.8 (+9.1 SE), carrierDeaths +4.8 (+5.9 SE), captured -0.04, kills -37 (-1.9 SE).
  - b2rgh: 2 diagnostic games. It never fired on DefaultLarge and got 0 captures on DefaultMedium; parked. The ledger names this variant as its re-open path.
- **Why re-test:**
  - **A3:** `Micro.ourHalf` (Micro.java:258) compares against `Sym.enemyCenters()`. With a wrong guess, "our half" let in flags deep in their base, which is exactly what sank b2rg.
  - **A5:** stale-carry rounds scale with carrier deaths, and b2rg doubled carrier deaths. That fits kills -37.
  - **The parent's evidence is confounded:** b2rg ran on B2, which carried BUDGET_V1 (-1.9 SE vs g_iter1 on the fillers).
  - **It has carriers to work with:** against ColtG5, g_iter2 picks up 5.66 flags and loses 3.84 carriers a game.
- **5(a):** the `rg` counter fires on our half (DefaultMedium, Tunnels, Battlecode24).
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2rgh 'rel:regrabs>=1.5 rel:captured>=1.0 rel:kills>=0.95 mean:efStaleCarry<=40 mean:overruns<=0'`. The efStaleCarry guard checks that the A5 fix holds (g_iter2: 15).

### 6. g2alert400: wider response net around a threatened flag
- **Switches:** `C.ALERT_RADIUS2=400` (int, default 100). `C.ALERT_FIX` stays true and `C.ALERT_THREAT_R2` stays 20.
- **Original verdict:** exact SPRT vs arch_rush10, REJECT 12-31 after 80 pairs (z -2.9). The verdict was real, but it was measured under A1.
- **Why re-test:** A1 changed what this constant means.
  - Under A1 the alert was fresh most of the game, and every non-defender within ALERT_RADIUS2 walked onto the flag tile and stayed (parks of up to 1238 rounds). At 400 the arm priced "park four times the area".
  - Under ALERT_FIX (Duck.java:375-379) the alert fires only for an enemy within dist2 20 of the home, and responders go to OF_THREAT, never onto the tile. maxParkOnHome went 325 -> 29.
  - The radius is now the size of a real-threat response net, and that was never tested.
  - The fixes did not move first grabs (6.96 -> 6.72).
- **5(a):** note `threat` in `--logs`, plus maxParkOnHome. Play vs arch_rush10 and the g_iter2 mirror.
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2alert400 'rel:enemyFirstGrabs<=0.85 rel:chasers20>=1.1 rel:inEnemy300>=0.85 mean:maxParkOnHome<=60 mean:overruns<=0'`

### 7. g2water: water trap instead of a stun when outnumbered
- **Switches:** `C.WATER_WHEN_WEAK=true` (boolean, default false).
- **Original verdict:** 4 unseeded band seeds plus a field block: 49-38 (sign p ~ 0.24), wins 175/480 vs 165/480. Kept off as "lean positive, below the bar".
- **Why re-test:**
  - **The case is mostly statistical.** It is the most consistent lean in the log (positive on the field and on 3 of 4 seeds). But 87 discordant pairs over about 590 unseeded cells is roughly the engine-flip floor alone. Seeded pairs settle it cheaply.
  - **The bug link is weak but real.** `placeCombatTrap` (Duck.java:44) runs at the top of the fight branch. Under A4 that branch ran against unreachable enemies, so the outnumbered test (allies+1 < enemies) counted enemies that could not arrive.
  - g_iter2 builds 0 water traps.
- **5(a):** the replay summary's trap counts (expl/water/stun). `Duck.waterTraps` is not in the indicator.
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2water 'fire:water400>0>=0.5 rel:stun400>=0.8 rel:kills>=0.95 mean:overruns<=0'`. The stun400 guard is there because each water trap replaces a combat stun, and combat stuns are load-bearing (ablation 16-26).

### 8. g2up3: CAPTURING second (ATTACK > CAPTURING > HEALING)
- **Switches:** `C.UPGRADE_ORDER=3` (int, default 0).
- **Original verdict:** closed at 5(a) on 3 single games, capturedLate 0/0/0 vs 2/0/1, read as "HEALING at r1200 is load-bearing". It never had a delivery block or a band test.
- **Why re-test:**
  - **Three late captures in three games is no evidence.**
  - **A5/A6 blocked CAPTURING's main lever,** the 25-round return window for flags our dying carriers drop. The dead carrier's flag stayed "carried by us", so nobody targeted the drop. REG_FIX now keeps the drop tile as the target for 25 rounds when we hold CAPTURING (`Comms.expireDrops`).
  - **g_iter2 gives it about twice the events.** On the band it doubled regrabsLate (0.47 -> 0.96) and raised capturedLate (0.22 -> 0.34).
  - **Risk:** HEALING may really be load-bearing late; the kills guard reads that.
- **5(a):** CAPTURING bought at r1200, and a late re-grab inside the 25-round window.
- **Delivery:** `N=48 SEED=484848 BASE=g_iter2 tools/delivery-gate.sh g2up3 'rel:capturedLate>=1.2 rel:regrabsLate>=1.2 rel:kills>=0.95 rel:enemyCaptured<=1.1 mean:overruns<=0'`
  - N=48 because the builds are identical until r1200 and only about half the games get past it.
  - It needs its own SEED because the base cache key is BASE+SEED+DGTAG, not N. At 909090 the cached 24-game base would pair with only a few cells.

### 9. g2def2: two defenders per flag
- **Switches:** `C.DEFENDERS_PER_FLAG=2` (int, default 1) and `Sym.SCOUT_FIRST=6` (int, default 3; Sym.java:210).
  - With 2 defenders per flag, idx 0-5 are defenders, and the scouts at idx 3-5 would leave their flags after r200 while the symmetry is undecided.
  - SCOUT_FIRST=6 keeps the scouts as the first three field ducks, as in g_iter2.
- **Original verdict:** exact SPRT vs arch_rush10, REJECT 9-25 after 80 pairs (z -2.7). Real, but measured under A1.
- **Why re-test:**
  - Under A1 every defender dropped `defend()` while any flag's alert was fresh, which was 59-94% of post-setup rounds. Defenders fell through to `defendTarget()` and stood on the flag tile without ringing or rebuilding.
  - So the test priced three more bodies standing on tiles. Under ALERT_FIX they defend.
  - It ranks below g2fstun because the bodies still come out of the field army, and only one partner was measured.
- **5(a):** `--logs 'defend'` for idx 3-5, plus defend300.
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2def2 'rel:defend300>=1.25 rel:enemyFirstGrabs<=0.85 rel:enemyCaptured<=0.9 rel:inEnemy300>=0.85 mean:overruns<=0'`. defend300 counts our robots within dist2 20 of our flags at r300. It is 8.56 on g_iter2, and three more present defenders would make it x1.35.

### 10. g2fort: fortress ring
- **Switches:** `C.RING_RADIUS2=13` (int, default 8) and `C.DEF_TRAP_RESERVE=0` (int, default 100). `ringPost` walks two-tile laps whenever RING_RADIUS2 > 8.
- **Original verdict:** exact SPRT vs arch_rush10, INCONCLUSIVE 16-29 after 96 pairs (LLR -2.68, z -1.94). The ring ablation ab2noring read 20-19 (unseeded).
- **Why re-test:**
  - The ring is built, walked and rebuilt only in `defend()`, which A1 switched off for 42-100% of defender time.
  - Triggered ring traps were rarely rebuilt: on Battlecode24, 1 of 5, and none from r1300 even with the bank above 700.
  - So the test measured a ring that existed only in setup.
- **Unfixed risk:** a reserve of 0 lets defenders drain the shared bank that combat stuns draw on.
- **5(a):** ring trap builds within dist2 13 of the homes after r400 (replay trap events).
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2fort 'rel:trapsBuilt>=1.1 rel:enemyFirstGrabs<=0.85 rel:kills>=0.95 mean:overruns<=0'`. stun400 is not used as a guard here because ring stuns inflate it. kills catches starved combat stuns.

### 11. g2csafe: carrier protection
- **Switches:** `C.CARRY_SAFE=true` and `C.CARRIER_HEAL=true` (boolean). These go on g_iter2 directly, without b2rgc's REGRAB, BUDGET_V1 and Z2ESCORT.
- **Original verdict:** parked after 2 diagnostic games on the b2rg base. The counters fired (safeSteps up to 9, carrierHeals up to 4), but carrier deaths per pickup were 1.0 in all four games, with 0 captures.
- **Why re-test:**
  - It was measured only on b2rg's re-grab carriers deep inside the enemy respawn zone, and that base is itself closed.
  - g_iter2's carriers are mostly first grabs, and there are about twice as many: 13.4 pickups and 11.7 carrier deaths a band game; 5.66 and 3.84 against ColtG5.
  - The carrier walks to `G.nearest(G.me, G.spawns)`, whose random tie-break flipped the target and reset bug state (A7). NAV_FIX keeps the state for moves within dist2 8.
  - **It ranks last on bug link.** The reason for parking it (carriers die within about 3 moves of a pickup) does not depend on a fixed defect, and A7's own value is unproven (g2nonav is pending).
- **5(a):** the `cs<safeSteps>/<carrierHeals>` counter.
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2csafe 'rel:carrierMoves>=1.1 rel:captured>=1.1 rel:postPickups>=0.9 mean:overruns<=0'`

### 12. g2z1: hold our dropped flag through its return window
- **Switches:** `C.Z1HOLD=true` (boolean, default false). `C.Z1_RADIUS2` 20 (int) stays.
- **Original verdict:** b1z1 delivery FAIL on absolute bars over 24 unseeded cells: enemy re-grabs 9.0 against a bar of <= 6.4, our pickups 5.2 against a guard of >= 5.4. It was superseded by Z2ESCORT after one traced game.
- **Why re-test:**
  - The absolute bars were later shown to be dominated by noise and were replaced by paired rel: checks, but Z1HOLD was never re-gated.
  - Under A1, field ducks already parked on our flag tiles hid the hold's marginal effect.
  - It has the same target as g2z2.
- **When to run:** after g2z2's delivery verdict. It is an independent arm on g_iter2; if both advance, combine them through the stack.
- **5(a):** the `hl` counter (holdTurns).
- **Delivery:** `BASE=g_iter2 tools/delivery-gate.sh g2z1 'rel:enemyRegrabs<=0.7 rel:postPickups>=0.8 rel:kills>=0.95 mean:overruns<=0'`. These are block 4's bars, so the two re-grab levers are judged the same way.

## Not queued

### Reserve (run only if an arm above is dropped)
- **g2drift80** (`C.HOLD_DRIFT=80`, int, default 0):
  - Its band result of 11-24 is a valid sign test even unseeded (-2.2 SE), and kills -62 (-2.7 SE) is a direct fight cost.
  - A1 pulled its objective home only for ducks near an alerted flag.
  - g_iter2 already cut stillPost from 36.0 to 28.1, so the headroom is small.
  - Bars if it is run: `rel:stillPost<=0.92 rel:inEnemy300>=1.2 rel:enemyCaptured<=1.2 rel:kills>=0.95 mean:overruns<=0`.
- **g2camp** (`C.DEST_CAMP=true` alone): the decomposition of g2icamp. Run it only if g2icamp advances, to learn which half carries the effect, or if g2icamp fails delivery with INTERCEPT shown pulling ducks off our flags.
- **g2crumbs** (`C.POST_SETUP_CRUMBS=true`):
  - A4 starved the detour, which runs only when no enemy can be engaged (Duck.java:67), so its 21-22 carries no information.
  - Its prior is low.
  - Bars if it is run: `rel:gathered400>=1.05 rel:inEnemy300>=0.9 mean:overruns<=0`.
- **g2fl10** (`C.RUSHERS=10`, `C.RUSH_FLANK=true`, `C.RELAY=false`, `Sym.SCOUT_FIRST=13`):
  - A5/A6 removed raid targets after every carrier death.
  - But the closing reason is structural: their respawners appear on their flags, and carriers die on the way home.
  - RELAY is still defective.
- **CUT** (research/REWRITE_DESIGN.md S1): paused, not refuted, and it is a build rather than a switch. Obj.java and the CUT_* constants do not exist yet. Its prerequisite is g2trk (`C.TRACK=true` only) against g_iter2 on seeded cells: 0 discordant, `mean:overruns<=0`.

### Stay closed
| direction (switch) | closing number | why it stays closed |
|---|---|---|
| Heal-first / capture-first upgrades (UPGRADE_ORDER 1, 2) | 22-26; 14-33 (p ~ 0.005) | -2.8 SE even diluted by engine noise; no fixed defect touches the upgrade path; ATTACK first is load-bearing |
| Re-grab anywhere alone (REGRAB, b2rg) | 11-26; carrierDeaths +5.9 SE | the mechanism is real (a duck holding a flag cannot fight); g2rgh and g2escrg carry the open variants |
| Smooth tile scoring (MICRO_V2) | 1-25, 1-26 exact pairs | far outside noise; no fixed defect on its path |
| Iteration-4 trap placement (TRAP_PLACEMENT_V2) | 18-32 exact pairs | real cost on the exact harness |
| Hold a front line (HOLD_LINE, S1) | 0/7, 1/7 at stage A | premise failure: the line gave up our half (their inEnemy300 25-36) |
| Specialisation (ATTACKER_TENTHS) | sp7 12-30; sp3 19-22; sp5 18-17 | fewer healers means more deaths; no fixed defect involved |
| Cohesion (GROUP_MIN) | gr8 4-18; gr4 20-23 | dose-response: the harder it is enforced, the worse |
| Setup digging (SETUP_DIGS, DIG_SITE, NO_FILL_OWN) | b3own stun400 -20.8 (-14.6 SE) | real economic cost; no fix touched setup (A14 is still open) |
| Flag relocation (RELOCATE_FLAGS) | 81 vs 84/110; 20-21 vs arch_rush10 | blocked by the engine; A12 is unfixed |
| Relay (RELAY) | 81 vs 84/110 | defective (the dropper re-picks its own drop) and unfixed |
| Budget floor / smart fills (BUDGET_V1, FILL_SMART) | B1 vs g_iter1 -1.9 SE over 1790 filler pairs; B3 vs B2 -1.94 / -2.02 SE | negative over large samples; no fixed defect on their path |
| Combat-trap quantity and aim (STUN_ENEMIES_MIN, TRAP_RESERVE, TRAP_TOWARD_NEAREST, EXPLOSIVE_BANK) | 20-22, 16-18, 15-23 | no positive lean; A4 changed when they fire, not what they do |
| Retreat threshold (RETREAT_HP) | flat at <= 300, worse above | a symptom, with no defect link |
| Aggression (ADVANCE_MARGIN, ENGAGE_MAX_THREAT) | 83, 81 vs 84/110 (field, unseeded) | the numbers say nothing, but there is no bug link either: `Micro.fight` still counts every visible enemy (`nearEnemies = enemies.length`), so REACH_FIX changed whether a fight starts, not what the margin means. Re-open if stillPost regresses |
| Hint sweep (HINT_SWEEP) | 21-25; firstFlagSight -1 +- 10 | nothing measurable to gain (firstFlagSight is already 279) |
| Dam / float trap ablations (ab1nodam, ab3nofloat) | VOID (audit A8) | the mechanisms rarely fire (bank < 700 in setup on 64 of 75 maps), so a re-run measures a no-op |