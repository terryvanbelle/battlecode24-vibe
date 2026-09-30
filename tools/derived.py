"""Metrics derived from a study table rather than read from the replay. `net` = kills - deaths."""
def add_derived(rows):
    """Add us_net/th_net to each row in place. Returns rows."""
    for r in rows:
        for side in ('us', 'th'):
            def g(c):
                try: return float(r[side + '_' + c])
                except (KeyError, TypeError, ValueError): return 0.0
            r[side + '_net'] = g('kills') - g('deaths')
    return rows
DERIVED_COLS = ['net']
