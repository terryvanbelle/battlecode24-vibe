# Briefing for the morning discussion (owner prompt 115): the new approach, what's working and what's not

Updated at every task check. Protocol: research/REWRITE_EVAL.md; numbers: progress/REWRITE.md; design:
research/REWRITE_DESIGN.md (when the design panel returns).

## Status
- 2026-10-02 13:45 UTC: design panel running (four angles, three judges, synthesis). Evaluation protocol and tools in
  place (tools/eval-paired.py with all / upper / rest and a descriptive rung slice). g_iter1 control on the
  confirmation seeds queued. No rewrite code yet.
- 14:15 UTC: four designs written, judges scoring. g_iter1 confirmation seed 717171 done, 818181 running.
  Archetype partners can start from builds we already have, each an early, weak copy of one enemy component:
  g1escrg (convoy with re-grabs), arch_rush / arch_rush10 (early raid), g1drift80 (ground push). Rejected as our
  strategy, useful as sparring opponents.
- 14:40 UTC: design done (research/REWRITE_DESIGN.md): persistent per-flag tracks predicted toward their spawn, a capped
  auction of free or jailed responders that cut the predicted path; every other duck plays g_iter1. Stage S0a (premise
  from g_iter1 band replays, no bot code) being implemented by a workflow with three-lens review. g_iter1 control on the
  confirmation seeds finished. Replays for the premise are on the VM (2 pinned runs; 3 more for a descriptive check).

## Working

## Not working

## Open questions for the owner
- The design picks one bet (unseen carriers walking home) and closes cheaply if its premise fails in S0a. Is a single
  focused bet the right shape, or should a second line (e.g. economy, the +2000 crumbs) run in parallel?
