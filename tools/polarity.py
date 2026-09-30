"""Orientation for every tracked metric, so that a HIGHER oriented value is always better for us
and a POSITIVE correlation with the result is therefore always good.

  +1  higher is better for us        -1  lower is better for us        0  unoriented (reported, never ranked)
A metric of theirs is the negation. A gap (us - them) is multiplied by the metric's own sign.
STUDY_COLS is the single list of per-team study columns (tools/scrim-study.sh reads it); the names match
tools/replaydump/ReplayDump.java --metrics.
"""
STUDY_COLS = ['alive', 'hp', 'crumbs', 'captured', 'carrying', 'deaths', 'kills', 'attacks', 'heals', 'traps_built',
              'traps_expl', 'traps_water', 'traps_stun', 'traps_hit', 'digs', 'fills', 'pickups', 'level_sum', 'moves', 'spawned']
POLARITY = {
    'alive': +1, 'hp': +1, 'crumbs': 0,          # banked crumbs: floating is bad, saving for traps is fine -> unoriented
    'captured': +1, 'carrying': +1, 'deaths': -1, 'kills': +1, 'attacks': +1, 'heals': +1,
    'traps_built': +1, 'traps_expl': +1, 'traps_water': +1, 'traps_stun': +1,
    'traps_hit': +1,                             # our traps the enemy set off
    'digs': 0, 'fills': 0, 'pickups': +1, 'level_sum': +1, 'moves': +1,
    'spawned': 0,                                # respawns follow deaths: unoriented
    'net': +1,                                   # derived: kills - deaths
    'cov': +1, 'meanMoves': +1, 'aba': -1, 'still': -1,
}
def orient(metric, value, side):
    """side: 'us', 'th', or 'gap'. Returns the oriented value, or None if unoriented."""
    p = POLARITY.get(metric, 0)
    if p == 0: return None
    if side in ('gap', 'us'): return p * value
    return -p * value
def label(metric, side):
    p = POLARITY.get(metric, 0)
    tag = '' if p == +1 else (' [inverted]' if p == -1 else ' [unoriented]')
    base = metric + (' (us-them)' if side == 'gap' else ('' if side == 'us' else ' (theirs)'))
    return base + tag
