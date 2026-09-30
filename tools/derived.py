"""Metrics derived from a study table rather than read from the replay.

`units` = miners + landscapers + drones (mobile robots) and `robots` = units + every building,
which is the engine's second tiebreak (`setWinnerIfQuantity` counts all robots of the team).
"""
BUILDINGS = ('refineries', 'vaporators', 'schools', 'centers', 'netguns')
def add_derived(rows):
    """Add us_units/th_units and us_robots/th_robots to each row in place. Returns rows."""
    for r in rows:
        for side in ('us', 'th'):
            def g(c):
                try: return float(r[side + '_' + c])
                except (KeyError, TypeError, ValueError): return 0.0
            units = g('miners') + g('landscapers') + g('drones')
            r[side + '_units'] = units
            r[side + '_robots'] = units + g('hq') + sum(g(b) for b in BUILDINGS)
    return rows
DERIVED_COLS = ['units', 'robots']
