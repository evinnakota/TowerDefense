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

        // Wave 1 — stronger opening
        waves.add(new WaveDefinition(1, 125, "Scout Swarm", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 50, 4, 8, 24)
        )));

        // Wave 2 — more pressure
        waves.add(new WaveDefinition(2, 225, "First Assault", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 65, 4, 8, 22),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 120, 4, 4, 28)
        )));

        // Wave 3 — serious mixed threat
        waves.add(new WaveDefinition(3, 325, "Mixed Forces", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 80, 5, 7, 18),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 180, 4, 6, 24)
        )));

        // Wave 4 — aggressive scaling
        waves.add(new WaveDefinition(4, 450, "Tanks & Flankers", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 95, 6, 9, 15),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 260, 4, 7, 20)
        )));

        // Wave 5 — near survival mode
        waves.add(new WaveDefinition(5, 650, "Final Onslaught", false, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 120, 6, 12, 12),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 375, 5, 10, 16)
        )));

        // Wave 6 — much harder boss wave
        waves.add(new WaveDefinition(6, 1200, "THE BOSS AWAKENS", true, List.of(
                new SpawnEntry(Enemy.TYPE_MONSTER2, 100, 6, 10, 14),
                new SpawnEntry(Enemy.TYPE_BOSS,    3000, 3, 1, 45),
                new SpawnEntry(Enemy.TYPE_MONSTER1, 350, 5, 8, 18)
        )));

        return waves;
    }

    /**
     * Generates a procedural wave beyond the defined list.
     * waveNum is 1-indexed (e.g. wave 7, 8, ...).
     */
    public static WaveDefinition generateInfiniteWave(int waveNum) {
        int extra = waveNum - 6;
        boolean isBossWave = (extra % 4 == 0); // bosses more often

        int reward = 1000 + extra * 175;
        String desc = isBossWave ? "Elite Boss Surge" : "Endless Horde — Wave " + waveNum;

        List<SpawnEntry> spawns = new ArrayList<>();

        if (isBossWave) {
            spawns.add(new SpawnEntry(
                    Enemy.TYPE_MONSTER2,
                    140 + extra * 18,
                    6 + Math.min(extra / 4, 4),
                    10 + extra,
                    Math.max(8, 16 - extra / 3)
            ));

            spawns.add(new SpawnEntry(
                    Enemy.TYPE_BOSS,
                    4000 + extra * 450,
                    3 + Math.min(extra / 10, 2),
                    2 + extra / 4,
                    Math.max(25, 45 - extra)
            ));

            spawns.add(new SpawnEntry(
                    Enemy.TYPE_MONSTER1,
                    450 + extra * 35,
                    5 + Math.min(extra / 5, 3),
                    8 + extra,
                    Math.max(10, 18 - extra / 4)
            ));

        } else {
            spawns.add(new SpawnEntry(
                    Enemy.TYPE_MONSTER2,
                    120 + extra * 12,
                    6 + Math.min(extra / 3, 5),
                    10 + extra * 2,
                    Math.max(6, 14 - extra / 4)
            ));

            spawns.add(new SpawnEntry(
                    Enemy.TYPE_MONSTER1,
                    400 + extra * 40,
                    5 + Math.min(extra / 4, 4),
                    8 + extra * 2,
                    Math.max(8, 18 - extra / 5)
            ));
        }

        return new WaveDefinition(waveNum, reward, desc, isBossWave, spawns);
    }
}