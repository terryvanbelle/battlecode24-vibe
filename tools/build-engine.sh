#!/usr/bin/env bash
# Build the Battlecode 2024 engine from source and stage it under engine/.
#
# WHY: the official maven artefacts (releases.battlecode.org/maven/org/battlecode/battlecode24/*)
# return 403 (checked 2026-09-30). The engine source is public (battlecode/battlecode24), so we
# build it with JDK 8 (the engine and the instrumenter require Java 8) and Gradle 7.6.
# Rotted dependency: net.sf.jsi:jsi:1.1.0-SNAPSHOT (sonatype snapshots purged); we reuse the jar
# compiled from aled/jsi for the 2021 project, or compile it from source.
#
#   tools/build-engine.sh                  # clone (if needed), patch, build, stage
#   ENGINE_REF=<sha|tag> tools/build-engine.sh
#
# Output: engine/engine.jar, engine/lib/*.jar (runtime deps), engine/maps/*.map24, engine/VERSION,
# tools/bc24-maps.txt. engine/ is gitignored; re-run on a fresh checkout.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC="${ENGINE_SRC:-$HOME/projects/vibe/reference/battlecode24}"
JSI_SRC="${JSI_SRC:-$HOME/projects/vibe/reference/jsi}"
ENGINE_REF="${ENGINE_REF:-3.0.6}"          # final 2024 release tag (spec 3.0.6)
export JAVA_HOME="${JAVA_HOME:-$HOME/jdk/jdk8u504-b01}"
export PATH="$JAVA_HOME/bin:$PATH"
java -version 2>&1 | grep -q '1\.8' || { echo "!! need JDK 8 at $JAVA_HOME" >&2; exit 1; }

[ -d "$SRC/.git" ] || git clone -q https://github.com/battlecode/battlecode24.git "$SRC"
( cd "$SRC" && git checkout -q "$ENGINE_REF" 2>/dev/null || { echo "!! cannot checkout $ENGINE_REF" >&2; exit 1; } )

# --- jsi (rotted snapshot) -------------------------------------------------
LIBS="$SRC/engine/libs"; mkdir -p "$LIBS"
if [ ! -f "$LIBS/jsi-1.1.0-SNAPSHOT.jar" ]; then
  for cand in "$HOME/projects/vibe/reference/battlecode21/engine/libs/jsi-1.1.0-SNAPSHOT.jar" \
              "$HOME/projects/vibe/reference/battlecode20/engine/libs/jsi-1.1.0-SNAPSHOT.jar"; do
    [ -f "$cand" ] && { cp "$cand" "$LIBS/"; break; }
  done
fi
if [ ! -f "$LIBS/jsi-1.1.0-SNAPSHOT.jar" ]; then
  mvn_get () { local f="$LIBS/$2-$3.jar"; [ -f "$f" ] || curl -fsSL -o "$f" "https://repo1.maven.org/maven2/$1/$2/$3/$2-$3.jar"; }
  mvn_get net/sf/trove4j trove4j 3.0.3; mvn_get org/slf4j slf4j-api 1.7.21
  [ -d "$JSI_SRC/.git" ] || git clone -q https://github.com/aled/jsi.git "$JSI_SRC"
  tmp="$(mktemp -d)"
  javac -nowarn -d "$tmp" -cp "$LIBS/trove4j-3.0.3.jar:$LIBS/slf4j-api-1.7.21.jar" $(find "$JSI_SRC/src/main/java" -name '*.java')
  ( cd "$tmp" && jar cf "$LIBS/jsi-1.1.0-SNAPSHOT.jar" net ); rm -rf "$tmp"
fi

# --- patch the build (idempotent) --------------------------------------------
cd "$SRC"
python3 - <<'PY'
import os
def patch(p, subs):
    s = open(p).read(); o = s
    for a, b in subs: s = s.replace(a, b)
    if s != o: open(p, 'w').write(s)
# Per-game seed override so a random draw of (map, side) is a different game while a fixed seed
# replays the same one: -Dbc.game.seed=<int>. Same patch as the 2020/2021 projects.
patch('engine/src/main/battlecode/world/LiveMap.java', [('    public int getSeed() {\n        return seed;',
      '    public int getSeed() {\n        return Integer.getInteger("bc.game.seed", seed);')])
patch('engine/build.gradle', [
    ('  mavenCentral()\n  // Java Spatial Index for RTree\n  maven {url "https://oss.sonatype.org/content/repositories/snapshots/"}\n  maven {url "https://mvnrepository.com/artifact/net.sf.trove4j/trove4j"}\n',
     '  mavenCentral()\n  flatDir { dirs "libs" }\n'),
    ("[group: 'net.sf.jsi', name: 'jsi', version: '1.1.0-SNAPSHOT'],", "[name: 'jsi-1.1.0-SNAPSHOT'],"),
])
s = open('engine/build.gradle').read()
if 'printClasspath' not in s:
    open('engine/build.gradle', 'a').write('\ntask printClasspath {\n  doLast { println sourceSets.main.runtimeClasspath.getAsPath() }\n}\n')
patch('example-bots/build.gradle', [('  maven {url "https://oss.sonatype.org/content/repositories/snapshots/"}\n', '')])
gp = open('gradle.properties').read() if os.path.exists('gradle.properties') else ''
if 'org.gradle.jvmargs' not in gp: open('gradle.properties', 'a').write('\norg.gradle.jvmargs=-Xmx600m\n')
PY

# --- build -------------------------------------------------------------------
./gradlew --no-daemon -q :engine:build -x test -x javadoc 2>&1 | grep -v '^warning\|^Note:' || true
[ -f engine/build/libs/engine.jar ] || { echo "!! engine build failed" >&2; exit 1; }
CP="$(./gradlew --no-daemon -q :engine:printClasspath | tail -1)"

# --- stage -------------------------------------------------------------------
OUT="${ENGINE_OUT:-$REPO/engine}"; rm -rf "$OUT"; mkdir -p "$OUT/lib" "$OUT/maps"
cp engine/build/libs/engine.jar "$OUT/engine.jar"
echo "$CP" | tr ':' '\n' | grep '\.jar$' | grep -v 'tools.jar' | while read -r j; do cp "$j" "$OUT/lib/"; done
cp engine/src/main/battlecode/world/resources/*.map24 "$OUT/maps/"
ls engine/src/main/battlecode/world/resources/*.map24 | xargs -n1 basename | sed 's/\.map24$//' | sort > "$REPO/tools/bc24-maps.txt"
{ echo "engine source: battlecode/battlecode24 @ $(git rev-parse HEAD) (ref $ENGINE_REF)"; echo "built: $(date -u +%FT%TZ) with $(java -version 2>&1 | head -1)"; } > "$OUT/VERSION"
cat "$OUT/VERSION"; echo "libs: $(ls "$OUT/lib" | wc -l)  maps: $(wc -l < "$REPO/tools/bc24-maps.txt")"
