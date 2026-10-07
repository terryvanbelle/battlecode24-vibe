# LEARNINGS

What one week of training a Battlecode 2024 bot taught (battlecode24-vibe, 2026-09-30 to 2026-10-07). The bot was trained
by Claude Code against the public 2024 field (55 bots) on a simulated Elo ladder. Written at shutdown (PROMPTS 192).
Readers: the owner, and the agent that runs the next season or a similar project.

How to read it. Each lesson is one or two sentences, then its evidence with n and a reference. "PROMPTS n" is an entry in
PROMPTS.md. TRAINING_LOG entries are cited by date. Lessons are year-agnostic unless marked **[2024]**. Ratings come from
the final fit in progress/ELO.md (45,389 games, each pair counted at most 200 times) unless marked "at the time". Dates
and times are UTC (TRAINING_LOG's header says PDT, but its entries use UTC; PROMPTS.md dates are PDT). A few figures come
from the session transcript, not from repo files; they are marked (session).

## 1. Outcome

The final bot is g_iter7 (src/g_iter7, incumbent since 2026-10-06). It rates 2113 +- 25 and ranks 6 of 88 players (55
ladder bots plus 33 of our builds), with a field score of 88.6% and 30.5% against the five bots above it. Six bots that
ranked above us now rate below it: the targets ColtG5 (1718), winkelmantanner.waffle (1855), CyrilSharma (1967) and Gymhgy
(1979), plus hsmalladi (2052) and andrewgopher (2092). NotLLeon.v3 is level at 2113, and g_iter7 leads it 48-40 head to
head. The last target, andli28.v9_USQuals_angle (2187, rank 5), was not cracked: g_iter7 is 1739-4109 (30%) against it.
Above that sit kuma (2258) and the top three (Strequals, chenyx512, jmerle, about 2280), against whom g_iter7 wins 9-15 of
48 games each. The week had two phases. From 09-30 to 10-02 the foundation bot g_iter0 (1257) became g_iter1 (1652) within
a day. Then about 80 builds (arms, ablations, sweeps and controls) followed, and none beat g_iter1. On 10-02 a correctness
audit broke that plateau, and six promotions followed in four days, from 1652 to 2113 (+461). Plain bug fixes from the two
audits gave +185 (g_iter2 +169, g_iter4 +16). Levers that audit findings motivated added +77 (g_iter5 +51, the owner's
stack, and g_iter6 +26). The heal hold found by a contrast study gave +144 and the waffle crack +55.

| build | promoted | what changed | rating | rank of 88 | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter0 | 09-30 | foundation bot | 1257 | 53 | 52.2% | 9.5% |
| g_iter1 | 09-30 | symmetry guess, trap rings, advance, float spend, carrier chase | 1652 | 41 | 73.0% | 16.1% |
| g_iter2 | 10-03 | first audit's basics fixes (A1-A7, A9; observed symmetry) | 1821 | 27 | 79.0% | 20.4% |
| g_iter3 | 10-03 | carrier stun + relocation V2 (the waffle crack) | 1876 | 19 | 80.8% | 19.3% |
| g_iter4 | 10-04 | C.FLAG_LOST (second audit, BOT1) | 1892 | 18 | 81.4% | 20.7% |
| g_iter5 | 10-05 | stack: centre crumbs + pick-after-move (BOT4, BOT5) | 1943 | 15 | 83.1% | 25.4% |
| g_iter6 | 10-06 | relocation climb (BOT3(a)) | 1969 | 13 | 83.9% | 25.6% |
| g_iter7 | 10-06 | heal hold (TACTICS T16) | 2113 | 6 | 88.6% | 30.5% |

Ratings read at the time were higher (g_iter2 1918, g_iter3 1977, g_iter7 peaked at 2150): about 100 points above the
final fit for g_iter1-g_iter3 and 30-40 for g_iter5-g_iter7. The eight incumbents still rate in promotion order in the
final fit. Read the steps within the final fit.

## 2. Lessons

### Measurement

**M1. Put the engine seed in every paired cell, and prove the harness with an identity control on the exact tool path that
produces verdicts.** A byte-identical copy of the incumbent must read 0 discordant pairs.
Evidence: determinism was verified on 09-30, and the mirror harness read 0-0 for a copy. But tools/scrim.sh, which produced
the band and ladder verdicts, drew random engine seeds until 10-02 23:00 UTC. Identical code flipped 15-29% of cells
(audit B2), the same as most arms' discordance (15-20%), so about 2.5 days of gained/lost tallies were mostly noise. After the
fix, g_iter1 vs the copy g1copy read 0 of 80 discordant (TRAINING_LOG 2026-10-02; research/AUDIT-2026-10-02.md B2).

**M2. Seeding removes noise only for code that changes nothing. Size blocks from measured discordance.**
Evidence: arms that change behaviour still disagree with the control on 14-38% of pairs (MEAS2; band arms mostly 16-20%,
g3escrg2 on the Cyril filler 29%, g2cr on its waffle filler 38%). On 234 band pairs, 1 SE is about 6.5 net games, or about
20 Elo, so only effects of about 40 Elo or more were visible.

**M3. Test the most informative per-pair number, and simulate the promotion rule's power before relying on it.** Wins are
a weak signal when most games are foregone conclusions.
Evidence: on the 110-game full-field instrument every arm landed within +-4 of 84/110 (10-01). Per pair, wins carry 2.2-5.6x
less information than the capture difference. The old rule promoted a g4ship1-sized gain (+30 Elo) 17% of the time as
written and 34% as practised. The adopted rule (t_all >= 2.3 or t_up >= 2.6 with wins net >= 0, looks at 240/480/720 pairs)
does so 63-65% of the time, at 2.0% false promotion against the old 2.4% (research/criteria-review-2026-10-05/verdict.md;
PROMPTS 186, 188). Before it there was no promotion for 45 hours. The new rule gave g4ship1 the third look that shipped it
as g_iter5 (t_all 3.25 at 720 pairs). g_iter6 (t 3.44 at look 2) and g_iter7 (t 6.46 at look 1) would have passed the old
criterion too; the rule only shortened their path (no separate confirmation run).

**M4. Replace point bars with three-way verdicts, and never close a line on INCONCLUSIVE.** PASS when the interval clears
the bar by 1 SE, FAIL when it is 2 SE short, otherwise extend the block.
Evidence: audit MEAS1 re-read all 43 delivery files. 16 of 28 FAILs (including 3 of the 5 Cyril closures) and 7 of 15
PASSes sat within 1 SE of their bar. g2cr's waffle PASS cleared its bar by 0.00 SE. b1z2b read 9.2 and then 4.83 on the same
24 cells. After the fix (24 -> 48 -> 96 cells, later 192), g3tether was a powered FAIL at -2.3 SE, and g7ehp went from
INCONCLUSIVE at 96 cells to PASS at 192.

**M5. First looks and small blocks regress toward zero.** Treat a 5(a) block (8-16 cells) and band look 1 as firing checks,
and let the pre-registered looks decide.
Evidence: g4relay2 raised captures 54% on 8 cells and 2% at 96 (-2.0 SE). g4gym1 was +31 over 400 pairs against Gymhgy
(+2.5 SE), then +12 over 1,880 (+0.4 SE). g7kite read t_all 2.03 at look 1 and 0.32 pooled over 480 pairs. The very large
effects showed at once (g1basics +33 over 234 pairs, g6heal t 6.46 at look 1), but shipped arms also started modest:
g4ship1 read t 1.2 at look 1 and 2.2 at 480 pairs before shipping at 720, and g5climb2 read 2.20 at look 1, close to
g7kite's 2.03.

**M6. Verify mechanically that a refactor is inert and that each arm's switch is on in the build that plays. Show every
check failing on a known-bad input.**
Evidence: a trap rewrite claimed to be default-equivalent read 18-32 discordant as an "inert" control and contaminated
c4bank, e1aggr and e2aggr (09-30). g1sym verification 4 ran with its switch off: a moved comment made the flipping sed match
nothing, and only bytecode equal to g_iter1's (14.9k) gave it away. tools/arm-intent.txt now asserts every flip. ContactTest
passed while javac had dropped its hooks (a static final false switch); tests now also run with switches on. The census
once reported carrier deaths as always 0. Still open: the absolute "exceptions" bar is structurally 0 (MEAS7), and
log-scan.sh can never produce output (MEAS17).

**M7. Do not price a fix from a correlational split such as "we lose more when the defect fires".** Defects cluster on hard
maps. Price the fix on identical cells against the same control.
Evidence: g_iter1's wrong symmetry guess went with 25.0% vs 39.9% wins against ColtG5 (144 vs 371 games), yet g1sym vs
g_iter1 was 63-57 head to head and net -6 on the band. In the waffle delivery blocks every arm "gained" (7-8 vs 5 of 24),
because the cached base scored 21% on those cells against 43% overall.

**M8. Absolute ladder Elo is not comparable across fits, and uneven sampling distorts a Bradley-Terry fit when matchups are
not transitive.** Compare builds within one fit, check convergence, cap games per pair, and calibrate bots rated on few games.
Evidence: g_iter1 was quoted at 1756, 1853, 1811 and 1762 before settling at 1652. The fit stopped early and read 30-44
points low (MEAS11). About 1,500 filler games against andli28 (a 30% matchup) pulled g_iter7 from 2150 to 2116, below
NotLLeon, which it beats head to head. A cap of 200 games per pair fixed it (PROMPTS 191). uravt rated 2274 +- 348 on 26
day-one games, fell to 1920 after 40 more, and had been left out of the band (PROMPTS 184-185).

**M9. Measure against the opponents that define the gap, not against yourself.** Mirror games and peers cannot price a
field-facing change, and wins on cells you almost always lose carry little signal.
Evidence: relocation's mirror gate (gate2) read 36-40 after 160 pairs on 09-30: "the mirror cannot price a defence against
rushes". The 10-02 diagnosis found the instruments aimed at the wrong place: g1drift doubled ground in games against
g_iter1 and gained none on the band. The band did hold 11 of the 12 bots rated 2050+, but we won about 9% of those cells, so
both arms lost them alike; the capture difference became the measure there (M3).

**M10. Treat bytecode headroom as a basic, and keep the near-limit check able to see.** A feature that spends spare
bytecode on purpose hides real near misses.
Evidence: observed symmetry plus the A4 reachability search left g1basics a peak of 24.7k, and g2nonav had 1 overrun (max
25.0k) in its 234-pair block (10-03). A cheaper search (C.REACH_FAST: play identical on all 234 cells, peak 23.0k) was
folded in. But Sym.update fills undecided turns until 2,500 bytecodes are left, so 92% of turns at 22.5k or more were those
fills and maxBcK (median 22.6k) saturated. A real near miss (BOT16: a 23,591-bytecode relocation scan, 95% of the limit)
hid among them (MEAS7).

### Process and gates

**P1. Before spending test budget, show that the intended behaviour occurs, and make the tooling refuse a band test
otherwise.** First in diagnostic games (step 5a), then in a pre-registered paired delivery block (5b). A firing counter is
not delivery, and one game is a filter, not a verdict.
Evidence: 10 of 11 adoption arms had been judged on wins without reproducing the opponent's behaviour (PROMPTS 66-67).
tools/delivery-gate.sh and the band-test refusal followed (commit 762970e, CLAUDE rule 13). After that, 5(a) or 5(b) stopped
z1hold, b2dig5, g1icpt (intercepts fired up to 88 per robot while chasers moved +0.09), g1esc2, b2rgc, g2rgh, g4contact8,
g7fc, g7ring and others before a 240-game band test. arch_rush won its first diagnostic game and then went 5 of 32.

**P2. A rule kept only in prose is followed in letter, not in spirit. Enforce each rule with a tool that refuses to go on.**
Evidence: rule 5 (a diagnostic before any gate) existed from day one, yet step 5 shrank to "a counter fires" (P1). 17 filler
blocks (about 2,000 games) were played and never recorded until the owner asked (PROMPTS 72-73). Each fix became a tool: the
delivery refusal, collect-fillers.sh at every task check, the arm-intent check, and tools/deadcode.py in the unit tests.

**P3. Do not stack changes that passed only a "not worse" test.** Small negative layers accumulate, and the stack drifts
below the incumbent.
Evidence: bases B1 (b1v2, 24-20), B2 (b1z2b) and B3 (b2fs, net -1 of 234) each advanced on non-inferiority. The paired
filler then put B1 at -30 over about 1,870 pairs vs g_iter1 (-1.8 SE). Final fit: b1v2 1626, b1z2b 1652, b2fs 1638, against
g_iter1 1652. The stack was dropped after the 10-02 diagnosis (PROMPTS 111).

**P4. Stacking near-misses can ship when each part has a positive estimate and the mechanisms are independent.** Judge the
stack as a new arm, because stacks can also cancel.
Evidence: g4ship1 = g4crumb (band net +4, t 1.0) + g4pick (+6 over 473 pairs, t 1.3), stacked under PROMPTS 180 and 187.
Pooled over 720 pairs: t_all 3.25, net +23; it became g_iter5 (1892 -> 1943). g4gym1 (crumbs + dam line) ended +12 over
1,880 pairs against Gymhgy, against +30 over 640 for crumbs alone; the dam line may cancel the crumbs, but this was never
tested directly. g4ship2's farm barely fired (levelGain1500 17.9 vs 17.5).

**P5. Budget filler games by expected information.** An idle filler keeps the VM busy, but it should not decide what gets
measured.
Evidence: 36,440 of the 45,389 ladder games (80%) were filler games, mostly target matchups (Gymhgy 12,450, andli28 6,770,
Cyril 6,170; run names containing "fill" in progress/games.csv). They distorted the fit until the pair cap (M8). The filler
kept pairing after two reads were logged as closed (10-05), and most target reads ended without a crack (g4gym1 +12 over
1,880 pairs against Gymhgy; g4econ2 against Cyril stopped for futility at 120 pairs).

### Strategy and the game

**S1. Target studies found levers, but only the ones that were also band-positive climbed the ladder.** Use the target to
find levers, ship on the band, and count a target as beaten when the ladder ranks it below you.
Evidence: g2cr was built to crack waffle and shipped band-wide as g_iter3 (+55); g4crumb, built from crumb accounting
against Cyril, delivered on the Gymhgy study's map class and became half of g_iter5. About 15 arms against Cyril and about
10 against Gymhgy cracked neither; both fell to band-wide promotions (g_iter7 now 35-13 and 39-9), as did hsmalladi and
andrewgopher, and NotLLeon is now level. Gains specific to one target did not climb: g7ehp was +28 over 240 paired games
against andli28 (+3.3 SE) but net +2 on band look 1 (capture t -1.12), and g4gym1 was band-neutral (net +1). A
pre-registered 60% bar kept work on ColtG5 (71/120 = 59.2%) after the ladder had ranked g_iter2 above it (1918 vs 1877 at
the time), until the owner declared it beaten (PROMPTS 157).

**S2. To crack a stronger opponent, read its replays for how it actually wins and attack that mechanism. If the mechanism
cannot be stopped, slowing it may be enough.** Pre-register the victory read.
Evidence: waffle loses fights 2:1 but re-grabs 87% of its carriers' drops in our losses, and our flags near its spawn fall
first (of 72 flags: 88% of the 24 under 20 tiles, 50% of the 18 at 28-36 tiles). Denying the re-grab was physically
infeasible: 1-9 waffle robots sit within dist2 8 of each drop (g2z2w: re-grabs 21.1 vs 21.1). Slowing worked. Carrier stuns
plus relocation (g2cr) went 146-94 (60.8%, p 0.001) over the pre-registered 240 games, paired +38, and was band-positive
(+22 over 473 pairs, capture t 3.8): g_iter3.

**S3. Find a lever by contrasting a behaviour rate across two groups at once: the bots that beat us and the bots we beat.**
A trait that separates us from the first group and sits at parity with the second is a candidate. This gave the largest
single-look effect of the week (t_all 6.46) and the largest Elo step after the first audit (+144; g_iter2 was +169).
Evidence: one agent read 15 g_iter6 replays; the 720-game control census then confirmed the signal. With an enemy within
dist2 10 we healed at 0.445 against the upper tier's 0.257, and held a ready strike at 0.196 against their 0.318; against
the rest of the band both were near parity (0.467 vs 0.399, 0.244 vs 0.242). g6heal (C.HEAL_HOLD) went 136 -> 164 wins over
240 band pairs (36-8, t_all 6.46, t_up 5.31). As g_iter7 its band control was 514-206 (71.4%) against g_iter6's 413-307
(57.4%, on different look seeds, so not paired), and the ladder moved 1969 -> 2113 (research/upper-tier-micro-2026-10-06;
TACTICS T16).

**S4. Eight economy and level arms did not ship. Break a gap down by source before building against it.** The economic
lever that worked took free resources without taking actions or crumbs from fights.
Evidence: five stopped at their 5(a) (g4farm, g4farm3, g4builder in two attempts, g7dig, g7bank). Three reached a delivery
block and are parked: g4farm2 (INCONCLUSIVE at 96 cells, +23% against a +25% bar), g4econ2 (Cyril delivery PASS, then
band-neutral, net +3) and g7fc (it fires in 17 of 17 games where its trigger holds; a stack candidate). Only some copied an
opponent (g4builder, g4econ2, g7dig, g7bank); g4farm and g7fc were study levers for the level-sum tiebreak. The andli28
study found our level gap is mostly heal XP (-27.6 of -35.3 levels) plus attack XP lost while in jail; digs explain about
13 levels. g7bank's design projected +22 levels and delivered +7. Centre crumbs (g4crumb) went +30 over 640 paired games
against Gymhgy (+2.3 SE) and became half of g_iter5.

**S5. [2024] First-half findings: the ATTACK upgrade first and HEALING at r1200 are load-bearing; healers, tempo and spread
beat specialisation, cohesion and static lines. Combat stun traps only lean load-bearing.**
Evidence: capture-first upgrade order went 14-33 (p about 0.005) and heal-first 22-26. CAPTURING instead of HEALING at r1200
cost late captures (b2u 0 vs 3 over 3 cells; g2up3 0.33 vs 0.44 and kills -2.9 SE over 48 cells). Specialisation (sp7)
12-30; cohesion (gr8) 4-18; hold line 0/7 and 1/7 at stage A; smooth-score micro 1-25 and 1-26. Removing combat stuns went
16-26 (p about 0.16, unseeded, on the pre-audit base): a lean, not a finding. The dam and float trap ablations are void
(A8), and trap quantity and aim variants went 20-22, 16-18 and 15-23.

**S6. [2024] Arms that added offensive presence (grabs, re-grabs, relays, escorts, dives, late all-ins) failed again and
again.** More attempts traded more carriers. Presence at enemy flags is an outcome of winning the fight, not a lever.
Evidence: b2rg went 11-26 (carrier deaths +5.9 SE). The g3escrg2 convoy against Cyril: pickups 20 vs 12.7, carrier deaths
15.8 vs 8.8, band k/d 2.13 vs 3.11. g4pick raised re-grabs 5-10x but alone moved captures +0.08 (t 1.3). g4relay2 +2% at 96
cells; the g4contact8 dive made screening worse (-2.7 SE). LATE_ALLIN was refuted before it was built: andli28 converts 3x
our rate late. The one exception is a timing fix, not presence: pick-after-move (BOT5, g4pick's switch) shipped inside
g_iter5, where the log credits the crumbs with the stack's Gymhgy gain.

**S7. [2024] Defensive flag distance worked band-wide.** A greedy climb over visible, reachable tiles beat walking to a fixed
precomputed spot.
Evidence: chains on our flags converted 0.50 within 20 tiles of Gymhgy's spawn and 0.13 at 36-47 tiles. 23% of the fixed
relocation spots were unreachable (BOT3(a)). g5climb2 moved our flags +2.53 tiles on the band and cut enemy captures 1.83 ->
1.33 in its 24-cell delivery block, then read t_all 3.44 over 480 pairs: g_iter6. A larger bound (g5climb3) moved flags on
only 3 of 12 cells.

**S8. [2024] Saving robots alone did not raise captures on the band.** Some deaths are the price of defending.
Evidence: g7ehp cut deaths 12% (377 vs 429) and lethal step-ins from 364 to 1.5 a game (+19 SE), but band look 1 (240
pairs) read capture t -1.12, and it was parked. g7kite cut ending turns in reach from 0.22 to 0.00 (+17.9 SE); pooled t
0.32, parked. g7spawn cut our respawns that end next to an enemy (spawnNear20 0.49 -> 0.26) and raised enemy captures
1.00 -> 1.50 on 24 cells: that flag lost its reinforcements; closed. The saving-plus-strike stack g7kiterc is untested on
the band.

**S9. [2024] Against the upper tier the economic gap was the kill reward, not the map's crumbs.** A kill pays the killer
30 crumbs only on enemy territory, so the side that fights in the other's half funds its traps from the other's deaths.
Evidence: g_iter6's control census, 360 upper-tier games (10-06): map crumbs equal (6,963 vs 6,984), but 66% of
our deaths fell on our territory (about 10,200 crumbs a game to them) and only 20% of our kills on theirs (about 2,400 to
us). They built 359 traps to our 216 and triggered 323 stuns to our 182. Against the rest of the band the flow reverses
(about 8,700 to us) and we win 89%. Territory-aware micro (g6terr) could not move it (R5; TACTICS T15).

### Research method: studies and critics

**R1. When a plateau holds, first run a read-only correctness audit of the bot and the measurement tools, with several
lenses and an adversarial verifier, before more tactic arms or a redesign.**
Evidence: none of about 80 builds from 09-30 to 10-02 beat g_iter1. The plan's escalation steps (ablation, an upgrade sweep,
re-reading games and advice, an 8-agent rewrite design) found defects (for example Sym.observe never called, 10-01) but
treated them as minor and deferred them (F2); only a dedicated correctness audit fixed them as a class. The 10-02 audit
(five lenses, two verifiers, 41 of 42 findings confirmed) found an own-flag alert fresh in 1057-1686 of 1800 post-setup
rounds that switched off every defender (A1), robots freezing on an enemy behind a wall (A4), a flag registry stale for up
to 1393 rounds (A5/A6), symmetry wrong on 32.4% of map-sides (A3) and unseeded pairs (B2). The fixes (g1basics) went +33
(44-11) over 234 seeded band pairs, +29 on confirmation seeds: g_iter2, +169. The second audit (10-03, 56 of 56 findings
reproduced) fed g_iter4 (BOT1), g_iter5 (BOT4, BOT5) and g_iter6 (BOT3(a)). A bundle of three fixes (BOT8 fill-step, BOT5
pick-after-move, BOT3(b) stall fallback; g4bundle) went net +2 and was parked: better against the rest (t 2.2), worse
against the upper tier. The method is in AUDIT_PROMPT.md and AUDIT_PLAYBOOK.md (PROMPTS 169).

**R2. Measure how often a code path runs before tuning, ablating or adding a lever behind it.** Every switch needs a firing
census.
Evidence: dam traps need a bank of 700 in r185-200, and the bank stayed below 700 on 64 of 75 maps; the bank reached the
1500 idle-trap threshold by r250 in 0 of 235 survey games. The ablations ab1nodam (20-16) and ab3nofloat (19-20) were
therefore void (A8). After setup our robots had an enemy in view in about 95% of their still turns, so levers in the
no-fight branch (carrier chase, DEST_CAMP) rarely ran.

**R3. A mechanism that delivers its proxy can still lose. Estimate the proxy-to-outcome link before building, and pick the
delivery signature from a traced causal path, including when the effect lands.** The reverse also holds: a bar on a
downstream outcome can block a mechanism that wins. Pair a bar on the mechanism's own signature with an outcome guard.
Evidence: b2rg raised re-grabs (+9.1 SE) and went 11-26: each extra re-grab ended in a dead carrier. b3own raised levels at
r200 (+16.5 SE) and went net -13: each dig was a stun not built. g1drift80 cut stillness 4 points and went 11-24. g7ehp and
g7kite (S8): g7kite's premise check had forecast P(ships alone) of about 0.05 because the deaths-to-captures link is near
zero. In reverse, g2cstun froze 3x as many carriers and won 13 of 14 discordant pairs, but failed both pre-registered
capture bars: whole game (-7% vs -15%) and by r600 (-11% vs -20%). Paired with relocation (g2cr), it passed the r600 bar at
exactly the bar (0.00 SE) and became g_iter3. The owner question on judging delivery by the mechanism's own signature
(BRIEFING) was never answered.

**R4. A conditional mechanism needs a delivery measure conditioned on its trigger and measured on its own object.**
Evidence: g7fc fired in 17 of 17 games level at r1950 and changed nothing before (identical through r1940 on 24 of 24 cells),
but its delivery averaged digs over all games: 3.7 against a bar of 4 (andli28) and 1.9 against 2 (band). g4z1's chasers20
counted robots near the enemy carrier, while the mechanism put robots on our dropped flag.

**R5. Statistics shaped by the outcome are consequences, not levers.** Check whether a metric just follows the game's flow,
and drop contrasts that restate the win condition.
Evidence: g6terr scored tiles on enemy territory to earn the kill reward (S9), yet its 5(a) failed: paidKillShare rose on 5
of 12 cells and the mean moved the wrong way (-0.086). One cell (Cyril Backslash) swung 0.04 -> 0.59 between the arm and its
g_iter6 twin, so where kills happen follows the game's flow, not a tile preference. The Gymhgy critic found that 92.4% of
Gymhgy's captures are "unopposed" because a relay's last leg always is, and that "third flag lost 0% in wins" is true by
definition.

**R6. Multi-lens study syntheses were over-optimistic; critics were better calibrated but over-trusted correlations between
maps.** Discount synthesis estimates, run premise checks, and test even demoted levers cheaply on identical cells.
Evidence: the andli28 synthesis valued FINAL_COMPLETE at +2.2 pp x 0.65 and ENGAGE_HP at +2-4 pp x 0.4; the critic cut the
odds to 0.6 and 0.25, and neither shipped. The level-dump design projected +22 levels and got +7. The Gymhgy critic demoted
centre crumbs (Spearman -0.06 between maps), yet a chosen-cell paired 5(a) won 7 of 16 cells to the base's 1 (6-0
discordant) and the arm became half of g_iter5.
Critics erred too: a symOk=0 "basics hole" was a one-tile artefact (symWrong 0 in 1,520 games). Premise checks corrected
figures from scratch analysers (step-ins 1,545, not 1,075; stranded XP 32%, not 45%). Opponents that replay identically under
a fixed seed (Gymhgy) make such paired twins exact and reusable.

**R7. Coin-flip tie-breaks in your own policy are randomized experiments.** Mine them for the causal effect of a decision,
split by state, before designing micro arms.
Evidence: the kite score's random ties (about 2.5k decisions per HP bin in upper-tier games, 10k in the rest) showed that
ending a turn in reach below 300 HP raised 3-round deaths by 0.16-0.21, while at 700 HP and above it raised strikes by
0.07-0.13 for 0.01-0.02 more deaths. That set g7kite's gate at HP < 700. The critic projected that the ungated arm would
lose band-wide (in rest games, about 24 strikes lost to save 1.1 deaths a game at HP >= 700). The limit: the effect was
causal over 3 rounds, but captures still did not move.

**R8. A direction closed on a noisy or bug-confounded measurement is not refuted, but re-testing closed arms wholesale did not
pay.** Reopen a direction when a new premise explains why it should work now.
Evidence: flag relocation was closed on 09-30 at 81 vs 84 of 110 on unseeded cells. With waffle's distance gradient as its
premise it came back in g2cr (g_iter3) and g5climb2 (g_iter6). Centre crumbs (t10crumbs) were closed at 21-22 on unseeded
pairs on the buggy base. On the fixed base, measured on the centre-crumb map class with exact twins, the same idle detour
delivered (gathered201to400 x1.9) and became half of g_iter5; the in-fight crumb step barely fired. The RETEST queue
(PROMPTS 150) ran 8 closed arms on g_iter2 and none delivered.

### Tooling and infrastructure

**T1. Compute is lost between runs, not during them.** A standing job queue with an idle filler recovers it, but only if
filler results are collected automatically.
Evidence: the 10-01 throughput audit found the 8 vCPUs saturated during runs (load about 19) but the VM idle about 29% of
its uptime between runs (PROMPTS 36). vm-queue.sh and the filler followed (CLAUDE rule 12). 17 filler blocks went unrecorded
until PROMPTS 72-73. Filler games became 80% of the ladder data (P5).

**T2. Key every result by (code, opponent, map, side, seed).** Name-based keys silently overwrite, drop or mislabel games.
Evidence: replay names without the seed dropped 1.6-2.5% of band games and 11.5% of ColtG5 filler games (B3), and 12% of
g_iter3's filler games were missing from the census (MEAS3). Seedless names also collided on repeated cells: in a 17-map
class only 17 of 24 and 26 of 48 cells paired, which, with an nw-guard defect, voided g4crumb's 96-cell gate. g_iter2's 600
games were recorded under the arm name g1basics, so the charts did not show it (PROMPTS 154-155). diag-batch output names
without the opponent let the arch_rush10 games overwrite the mirror games of the same bot, map and seed. Twice, a delivery
block without a tag was caught before it overwrote the band PASS file.

**T3. Replay storage needs a retention policy keyed to the analyses that still need the files, a disk check before writes,
and job status that fails loudly.**
Evidence: the driver disk filled by 10-02 01:56 UTC (2.6 GB of replays). The VM disk filled on 10-02 at 12:12 UTC (49 GB
disk, 35 GB of replays), and the runner spun on failing fillers for 18 minutes with no back-off, while vm-queue logged every
job as "exit 0" because $? held a date substitution's status (B12). Census CSVs for b2fs were committed empty. The first
prune rule then deleted step-5(a) replays an hour after they ran, and all 880 g_iter5 filler replays that a queued premise
check needed. vm-prune now keeps the newest 25 filler runs per build.

**T4. Tool defects were frequent and silently changed verdicts.** Pin every fix with a regression test, void any gate that
rested on a defect, review new census columns and gate commands adversarially before games, and keep the analysers studies
rely on as repo tools.
Evidence: side-indsum read the wrong team, so g4pick's after-move pickups showed 0 when they were 5-99 a game. The nw guard's
INCONCLUSIVE rule ignored the direction of the difference. capability-census died under pipefail. The 64-char indicator
string overflowed on 70-75% of turns.

**T5. Shared machines need a lock and a concurrency cap.**
Evidence: the unit suite takes about 10 minutes on the 2-vCPU driver and ran after every change (about 220 times; session).
A parallel-compile race lost 2 of the B3 band's 234 games (10-02), and on 10-06 overlapping unit runs raced on the same
build directories and shared a log (session); tools/unit-tests.sh still has no lock. 24 diagnostic games at once on 8 vCPUs
starved the VM's sshd (10-02); diag-batch.sh now caps at 8. On 09-30 the e2-standard-8 type was unavailable in us-west1-b,
so the VM moved to us-west2-a.

### Working with the owner

**O1. The owner's short, basic questions were the cheapest audits of the week.** Answer them by checking the data, not by
explaining the design, and turn each correction into a tool-enforced rule.
Evidence: each of these exposed a real defect: PROMPTS 17 (the four tactics T1-T4 had all been traced from hsmalladi games;
TACTICS has 19 rows now), 36 (VM 29% idle), 66 (arms skipped delivery), 72 (fillers unrecorded), 111 (stacking on
non-inferiority), 125 (symmetry guessed), 154 (front page stale), 181 (stale ELO column), 184 (uravt under-sampled), 186 (a
shipping rule with 17% power). CLAUDE rules 12, 13 and 15 came out of them; rule 14 came from owner directives (PROMPTS 73,
120, 157-159).

**O2. Bring the owner a rule's measured cost with a recommended option, and ask directly.** Do not quietly carry a stricter
reading of the owner's rules.
Evidence: each time a rule's cost was shown, the owner relaxed it: the kills guard (PROMPTS 183; g4ship1 then shipped with
k/d level at 3.09 vs 3.09), the shipping rule (186-188), the pair cap (191, which the agent raised itself). The day-one ban
on choosing maps or sides in diagnostics was the agent's own extension of the ladder clause in PROMPTS 1 (CLAUDE rule 4
later cited "PROMPTS 25" as its source, but entry 25 was a task check). It was carried for about 4 days (09-30 15:38 to
10-04 18:55 UTC) and never raised, until the owner relaxed it unprompted (PROMPTS 178). Chosen cells then measured the
centre-crumb lever on the Gymhgy study's map class: 7 wins to the base's 1 on 16 cells (6-0 discordant). An open question
parked in progress/BRIEFING.md was never answered (R3).

**O3. The owner sees the project through GitHub: README, HANDOFF and progress/.** Keep each state number in one generated file
and link to it, and update the front page in the same commit as a promotion.
Evidence: "I don't see g_iter2 in github" (PROMPTS 154-155): its games sat under g1basics, HANDOFF still named g_iter1, and
README named no bot. Later promotions updated HANDOFF and README in the same commit. Even so, at shutdown the incumbent's
rating and the target record disagreed across README, HANDOFF, CLAUDE rule 14 and a memory file (memory/target-opponent.md
still read g_iter7 2150, andli28 2174 and 14-34, and listed NotLLeon as defeated; it is now level). All four were corrected in the shutdown commit. PROMPTS.md missed one
owner prompt queued while the agent was busy (entry 128, 10-02 18:15 UTC, on symmetry; TRAINING_LOG cites "owner prompts
127-128"), and entry 135 is a paraphrase.

**O4. The owner valued results and reusable method, not volume.**
Evidence: praise went to the audits (PROMPTS 140, 175), the ColtG5 crack (157) and the overnight climb ("Good work, no notes",
190, after g_iter6 and g_iter7 in one night). The asks were to carry the method forward: a reusable audit prompt (169, now
AUDIT_PROMPT.md) and ADVICE cut to a short paragraph with no year-specific references (173).

### Agent failure modes

**F1. Escalating to an elaborate redesign before checking the basics.** Read a plateau first as a sign of broken basics, not
as an architecture ceiling.
Evidence: the answer to PROMPTS 111 named an "architecture ceiling" and launched an 8-agent rewrite design
(research/REWRITE_DESIGN.md) with two instrument stages built; its consumer was never built. The binding terms were basic
bugs. Sym.observe had no callers (the first run of tools/deadcode.py flagged it). Of g_iter3's ~900 dead lines of 2,807
(MEAS14), 528 were the rewrite's unused Track sensor. The rewrite's S0a premise tools did measure symmetry wrong in a third
of band games (symOk 0.662), just before the owner's symmetry question (PROMPTS 125).

**F2. Deferring a broken basic because its consumer looks minor.** One broken basic is a sample of a class: audit at once.
Evidence: on 10-01 at 15:55 UTC research/TACTIC_LEVELS.md recorded that Sym.observe is never called and moved it out of the
work block. 26 hours later the owner asked about symmetry (PROMPTS 125; 127: "This is basic stuff"). The symmetry fix alone
was neutral (63-57), but the audit it triggered ended the plateau (R1).

**F3. Not questioning the yardstick when positive arms keep falling short.**
Evidence: g4crumb, g4pick and g4econ2 sat positive but short for about 45 hours without a promotion. The agent did not test
the rule's power until the owner asked "Can you evaluate whether the shipping criteria are too strict?" (PROMPTS 186). The
review found the rule had 17% power for the gains this project produces (M3).

**F4. Building a study's ranked levers before following up its top open question.** A large unexplained split is cheap to
chase and can reframe the target.
Evidence: on 10-06 the andli28 study named the vertical-symmetry penalty its highest-value question (0.195 vs 0.366, z -2.9
with controls, ceiling about +6 pp). Four andli28 levers (g7fc, g7ehp, g7ehp2, g7ring) were built first. None shipped,
although g7ehp gained about +12 points against andli28 (+28 over 240 paired games). Two upper-tier arms followed (g7kite
parked; g7kiterc fired its 5(a) but was never band-tested). The vertical study started on 10-07, and no lens finished before
shutdown. The penalty is specific to andli28, so by S1 even a fix might not have climbed the band. Slice a target's games by
map symmetry and side on day one: it is cheap.

**F5. pkill -f and pgrep -f match the issuing shell's own command line.** Kill by PID from a ps listing or use a [b]racket
pattern, and wait on an output file rather than on pgrep.
Evidence (session): five times in the week, the last at shutdown: on 10-07 at 13:09 UTC a remote pkill -f "vm-queue" killed
its own ssh shell (exit 255), and the queue survived until it was killed by PID. Two "until ! pgrep -f" waits on 10-06 could
never end and spun for about 10 minutes. The repo tools were already safe (tools/lib.sh uses ps|awk; tools/vm.sh uses
'[b]attlecode'), and research/PRIOR_2021.md had the rule.

## 3. What worked and what did not

Worked:
- Correctness audits of our own bot and tools: their bug fixes gave 2 of the 6 promotions after g_iter1 (+185 Elo of
  +461), and their findings motivated 2 more (+77).
- Seeded paired cells checked by an identity control (0 of 80 discordant).
- Delivery gates enforced by tools before any band test.
- Three-way verdicts, and a shipping rule on the capture difference with sequential looks (63-65% power at 2.0% false).
- A two-group contrast study of behaviour rates: the heal hold, +144 Elo in one step.
- Slowing an opponent's mechanism when it could not be stopped (the waffle crack).
- Stacking positive near-misses with independent mechanisms (g_iter5).
- Paired 5(a) blocks on chosen cells with exact twins.
- A standing VM queue with an idle filler, collected at every task check.

Did not work:
- Tactic arms on a broken base: about 80 builds from 09-30 to 10-02, none accepted.
- Stacking on non-inferiority (bases B1-B3 ended at or below g_iter1).
- Economy and level-sum arms (eight: five stopped at 5(a), three parked).
- More offensive presence: re-grab volume, relays, convoys, dives (pick-after-move, a timing fix, did ship in g_iter5).
- Target-only gains as a way up the ladder (Cyril, Gymhgy and andli28 arms; g7ehp, g4gym1).
- Saving robots alone (g7ehp and g7kite parked, g7spawn closed; the saving-plus-strike stack g7kiterc untested).
- The large studies' top-ranked levers, and the rewrite design.
- Re-testing closed arms wholesale (RETEST arms 1-8).
- Rules kept only in prose.

## 4. Open leads at shutdown, ranked

1. **The vertical-symmetry penalty against andli28.** We win 0.187 on vertical maps (1,608 games) against 0.383 on
   rotational maps (1,724) and 0.338 on horizontal maps (868); this replicated out of sample (5,720 games in all). Against the
   band there is no penalty (0.753 vs 0.702, 684 games). On vertical maps against andli28 our first grabs fall by 2.7 a game,
   captures by 0.43, our kills on its territory from 119 to 72, and its stun triggers rise by 66. The mechanism is
   unidentified. The study was stopped before any lens finished; research/vertical-study-2026-10-07/maps.txt has the map
   table. Suggested first step (andli28 synthesis): trace 4 vertical-map losses against 4 rotational wins of the same kind at
   r250/400/600. Ceiling about +6 pp against andli28.
2. **g7kiterc's band delivery and band test.** g7kite plus RC_BAND (a supported one-step hold at HP >= 700) fired its 12-cell
   5(a) on every pre-registered bar (rcHold 0.94-0.97 vs 0.07-0.09; strikes within 2 rounds 2.0-2.2x). The registered next step:
   `BASE=g7kite DGTAG=-kiterc tools/delivery-gate.sh g7kiterc 'mean:rcHold>=0.5 rel:rcStrike2>=1.2 mean:reachEndFree<=0.03
   nw:enemyStunTrig<=1.1 nw:heal400>=0.85 nw:enemyCaptured<=1.1 mean:overruns<=0' && tools/band-test.sh g7kiterc`.
3. **Enemy stun volume.** The upper tier's stuns trigger 439 times a game on us against our 200 on them, and contact is about
   twice as lethal for us at equal local numbers (2-round hazard 0.082 vs 0.040). No lever on this has shipped; STUN_MIN13 was
   not built because its dose was too small to read. The kill reward funds part of it (S9).
4. **A band-safe step-in gate.** g7ehp was +28 over 240 paired games against andli28 (49-21, +3.3 SE) but band-neutral. A form
   that keeps the heal is untested.
5. **g7fc as a stack component**, re-gated with a delivery measure conditioned on games level at r1950.
6. **Open minor audit items**: BOT6, BOT11, BOT14, BOT15, BOT17-BOT20; MEAS4, MEAS6-MEAS10, MEAS13-MEAS17. Two make checks
   vacuous: the exceptions bar (MEAS7) and log-scan.sh (MEAS17).

To resume: `source tools/vm.sh && ensure_vm`; on the VM, move queue/filler.job.stopped back to queue/filler.job; run
`tools/vm-run.sh queue-runner 'tools/vm-queue.sh'`; restart the task-check loop (HANDOFF.md).

## 5. If starting again: five things to do differently

1. **Build the measurement first.** Before the first arm: seeded paired cells, an identity control on the verdict path,
   three-way verdicts, a shipping rule on the most informative per-pair number with simulated power, a pair-capped
   ladder fit, and filler budgeted by expected information.
2. **Audit before building arms.** Run AUDIT_PROMPT.md on the first working bot and at every plateau, with the dead-code
   check, the arm-intent check, a firing census per switch and a bytecode headroom check in the unit tests.
3. **Check the premise before any arm.** Estimate the proxy-to-outcome link (for example deaths to captures), mine our own
   random tie-breaks for causal effects, condition delivery on the trigger, and pair a signature bar with an
   outcome guard.
4. **Ship on the band and use targets as diagnostics.** Run a two-group contrast study of behaviour rates early, and slice
   each target's games by map symmetry and side on the first day.
5. **Enforce every rule with a tool and keep one source of state.** Results keyed by full cell, replay retention keyed to
   consumers, a lock on the test runner, a generated state file the front page links to, and any blocking rule raised with
   the owner at once, with its measured cost.
