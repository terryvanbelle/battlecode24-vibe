"""Orientation for every tracked metric, so that a HIGHER oriented value is always better for us
and a POSITIVE correlation with the result is therefore always good.

  +1  higher is better for us        -1  lower is better for us        0  unoriented (reported, never ranked)
A metric of theirs is always the negation: more for them is worse for us. A gap (us - them) is
multiplied by the metric's own sign: being further ahead on something good is good, being further
ahead on deaths or oscillation is bad. (The 2021 project shipped gaps unoriented for three days.)
"""
POLARITY = {
    'soup': +1, 'hq': +1, 'hqBuried': -1, 'hqElev': 0, 'worth': +1,
    'miners': +1, 'landscapers': +1, 'drones': +1, 'refineries': +1, 'vaporators': +1, 'schools': +1, 'centers': +1, 'netguns': +1,
    'units': +1,                          # derived: miners + landscapers + drones (the tiebreak counts every robot; buildings too)
    'robots': +1,                         # derived: all living robots, the actual tiebreak quantity
    'spawned': +1, 'spawnCost': +1,
    'died': -1, 'drowned': -1, 'shot': -1, 'buriedDeaths': -1,
    'mines': +1, 'soupDeps': +1, 'refines': +1, 'digs': +1, 'dirtDeps': +1, 'pickups': +1, 'drops': +1, 'shots': +1,
    'moves': +1, 'meanMoves': +1, 'cov': +1, 'aba': -1, 'bcOver': -1,
    'firstHQ': 0,                         # round of first contact with the enemy HQ: direction unclear
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
