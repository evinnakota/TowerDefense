import java.util.ArrayList;
import java.util.List;

public class WaveDefinition {

    public static class SpawnEntry {
        public final int enemyType;
        public final int health;
        public final int speed;
        public final int count;
        public final int spawnIntervalTicks;

        public SpawnEntry(int enemyType, int health, int speed, int count, int spawnIntervalTicks) {
            this.enemyType = enemyType;
            this.health = health;
            this.speed = speed;
            this.count = count;
            this.spawnIntervalTicks = spawnIntervalTicks;
        }
    }

    public final int waveNumber;
    public final int reward;
    public final List<SpawnEntry> spawns;
    public final String description;
    public final boolean isBossWave;

    public WaveDefinition(int waveNumber, int reward, String description,
                          boolean isBossWave, List<SpawnEntry> spawns) {
        this.waveNumber  = waveNumber;
        this.reward      = reward;
        this.description = description;
        this.isBossWave  = isBossWave;
        this.spawns      = spawns;
    }

    public static List<WaveDefinition> buildWaves() {
        List<WaveDefinition> waves = new ArrayList<>();

        // Wave 1 — only monster2 (weak scouts)
        waves.add(new WaveDefinition(1, 100, "Scout Swarm", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 30, 3, 6, 30)
        )));

        // Wave 2 — mostly monster2 with a couple monster1
        waves.add(new WaveDefinition(2, 175, "First Assault", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 40, 3, 5, 28),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 80, 3, 2, 35)
        )));

        // Wave 3 — mixed, monster1 gets tankier
        waves.add(new WaveDefinition(3, 250, "Mixed Forces", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 50, 4, 4, 25),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 120, 3, 4, 30)
        )));

        // Wave 4 — fast monster2 flankers + tough monster1 tanks
        waves.add(new WaveDefinition(4, 350, "Tanks & Flankers", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 55, 5, 5, 20),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 175, 3, 5, 28)
        )));

        // Wave 5 — heavy mixed swarm
        waves.add(new WaveDefinition(5, 500, "Final Onslaught", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 70, 5, 6, 18),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 250, 4, 6, 22)
        )));

        // Wave 6 — BOSS WAVE
        waves.add(new WaveDefinition(6, 800, "THE BOSS AWAKENS", true, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 60, 4, 4, 20), // minion escorts
                new SpawnEntry(Enemy.TYPE_BOSS,    1200, 2, 1, 60), // the boss
                new SpawnEntry(Enemy.TYPE_MONSTER1, 150, 3, 3, 25)  // rear guard
        )));

        return waves;
    }

    /**
     * Generates a procedural wave beyond the defined list.
     * waveNum is 1-indexed (e.g. wave 7, 8, ...).
     */
    public static WaveDefinition generateInfiniteWave(int waveNum) {
        int extra = waveNum - 6; // how many waves past the boss
        boolean isBossWave = (extra % 5 == 0); // boss every 5 infinite waves

        int reward = 600 + extra * 100;
        String desc = isBossWave ? "Elite Boss Surge" : "Endless Horde — Wave " + waveNum;

        List<SpawnEntry> spawns = new ArrayList<>();

        if (isBossWave) {
            spawns.add(new SpawnEntry(Enemy.TYPE_MONSTER2, 80 + extra * 10, 5, 5 + extra, 18));
            spawns.add(new SpawnEntry(Enemy.TYPE_BOSS, 1500 + extra * 200, 2, 1 + extra / 5, 55));
            spawns.add(new SpawnEntry(Enemy.TYPE_MONSTER1, 200 + extra * 20, 4, 4 + extra, 22));
        } else {
            spawns.add(new SpawnEntry(Enemy.TYPE_MONSTER2, 70 + extra * 8,  5 + Math.min(extra / 3, 3), 5 + extra, 16));
            spawns.add(new SpawnEntry(Enemy.TYPE_MONSTER1, 250 + extra * 25, 4 + Math.min(extra / 4, 2), 5 + extra, 20));
        }

        return new WaveDefinition(waveNum, reward, desc, isBossWave, spawns);
    }
}