import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game implements ActionListener {
    private static final int SLEEP_TIME = 20;
    private GameViewer frontend;
    private Square[][] Grid;
    public static int GRID_HEIGHT = 33;
    public static int GRID_WIDTH  = 59;
    public static final int WALL        = 1;
    public static final int PATH        = 2;
    public static final int START_POS   = 3;
    public static final int END_POS     = 4;
    public static final int BASIC_TOWER = 5;
    public static final int SNIPER      = 6;
    public static final int CROSSBOW    = 7;

    private ArrayList<int[]>    globalPath;
    private ArrayList<Enemy>    enemies    = new ArrayList<>();
    private ArrayList<Tower>    towers     = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    // Wave state
    private List<WaveDefinition> waveDefinitions;
    private int  currentWave        = 1;
    private boolean waitingForNextWave = true;
    private ArrayList<int[]> spawnQueue = new ArrayList<>();
    private int spawnQueueIndex = 0;
    private int spawnCounter    = 0;

    public Game() {
        try {
            Grid = new Square[GRID_HEIGHT][GRID_WIDTH];
            File myObj = new File("src/main/resources/maze1.txt");
            Scanner myReader = new Scanner(myObj);
            for (int row = 0; row < GRID_HEIGHT && myReader.hasNextLine(); row++) {
                String line = myReader.nextLine();
                for (int col = 0; col < GRID_WIDTH && col < line.length(); col++) {
                    Grid[row][col] = new Square();
                    char c = line.charAt(col);
                    if      (c == '#') Grid[row][col].addImage(WALL);
                    else if (c == ' ') Grid[row][col].addImage(PATH);
                    else if (c == 'S') Grid[row][col].addImage(START_POS);
                    else if (c == 'E') Grid[row][col].addImage(END_POS);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        globalPath       = computePath();
        waveDefinitions  = WaveDefinition.buildWaves();
        frontend         = new GameViewer(this);

        Timer clock = new Timer(SLEEP_TIME, this);
        clock.start();
        frontend.repaint();
    }

    // ── Spawn queue ────────────────────────────────────────────────────────────

    private void buildSpawnQueue(int waveNum) {
        spawnQueue.clear();
        spawnQueueIndex = 0;
        spawnCounter    = 0;

        WaveDefinition def = (waveNum <= waveDefinitions.size())
                ? waveDefinitions.get(waveNum - 1)
                : WaveDefinition.generateInfiniteWave(waveNum);

        for (WaveDefinition.SpawnEntry entry : def.spawns) {
            for (int i = 0; i < entry.count; i++) {
                spawnQueue.add(new int[]{entry.enemyType, entry.health,
                        entry.speed, entry.spawnIntervalTicks});
            }
        }
        // Shuffle so types are interleaved, but keep boss entries last
        ArrayList<int[]> bosses   = new ArrayList<>();
        ArrayList<int[]> regulars = new ArrayList<>();
        for (int[] e : spawnQueue) {
            if (e[0] == Enemy.TYPE_BOSS) bosses.add(e);
            else regulars.add(e);
        }
        java.util.Collections.shuffle(regulars);
        spawnQueue.clear();
        // Regulars first, boss(es) in the middle-ish
        int mid = regulars.size() / 2;
        spawnQueue.addAll(regulars.subList(0, mid));
        spawnQueue.addAll(bosses);
        spawnQueue.addAll(regulars.subList(mid, regulars.size()));
    }

    public void spawnEnemy(int enemyType, int health, int speed) {
        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {
                if (Grid[r][c].isStart()) {
                    enemies.add(new Enemy(Grid[r][c].getX_cord(), Grid[r][c].getY_cord(),
                            health, speed, globalPath, enemyType));
                    return;
                }
            }
        }
    }

    // ── Wave control ───────────────────────────────────────────────────────────

    /** Called for the very first wave. */
    public void startFirstWave() {
        buildSpawnQueue(currentWave);
        waitingForNextWave = false;
    }

    /** Called by the "Start Next Wave" button after wave 1. */
    public void startNextWave() {
        currentWave++;
        buildSpawnQueue(currentWave);
        waitingForNextWave = false;
        System.out.println("Wave " + currentWave + " started! (" + spawnQueue.size() + " enemies)");
    }

    public boolean isNextWaveBoss() {
        int next = currentWave + 1;
        if (next <= waveDefinitions.size()) {
            return waveDefinitions.get(next - 1).isBossWave;
        }
        // infinite waves: boss every 5 past wave 6
        return ((next - 6) % 5 == 0);
    }

    public boolean isCurrentWaveBoss() {
        if (currentWave <= waveDefinitions.size()) {
            return waveDefinitions.get(currentWave - 1).isBossWave;
        }
        return ((currentWave - 6) % 5 == 0);
    }

    // ── Main loop ──────────────────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        if (waitingForNextWave) return;

        // Spawn
        if (spawnQueueIndex < spawnQueue.size()) {
            int[] next = spawnQueue.get(spawnQueueIndex);
            spawnCounter++;
            if (spawnCounter >= next[3]) {
                spawnEnemy(next[0], next[1], next[2]);
                spawnQueueIndex++;
                spawnCounter = 0;
            }
        } else if (enemies.isEmpty()) {
            // Wave complete
            waitingForNextWave = true;
            int reward = getWaveReward(currentWave);
            frontend.updateStats(frontend.getHealth(), frontend.getMoney() + reward, currentWave);
            frontend.showWaveCompleteOverlay(currentWave, reward);
            return;
        }

        // Towers fire
        for (Tower tower : towers) {
            Projectile p = tower.attack(enemies);
            if (p != null) projectiles.add(p);
        }

        // Projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.move();
            for (Enemy enemy : enemies) {
                if (p.hits(enemy)) { enemy.takeDamage(p.getDamage()); p.deactivate(); break; }
            }
            if (!p.isActive() || p.getX() < 0 || p.getX() > GRID_WIDTH * Square.SQUARE_WIDTH
                    || p.getY() < 0 || p.getY() > GRID_HEIGHT * Square.SQUARE_HEIGHT) {
                projectiles.remove(i);
            }
        }

        // Enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.move(Grid);
            if (!enemy.isAlive()) {
                int kill = (enemy.getEnemyType() == Enemy.TYPE_BOSS)   ? 200 + currentWave * 20
                        : (enemy.getEnemyType() == Enemy.TYPE_MONSTER2) ?  10 + currentWave * 2
                        :                                                    25 + currentWave * 5;
                frontend.updateStats(frontend.getHealth(), frontend.getMoney() + kill, currentWave);
                enemies.remove(i);
            } else if (enemy.reachedEnd(Grid)) {
                int dmg = (enemy.getEnemyType() == Enemy.TYPE_BOSS)    ? 30 + currentWave
                        : (enemy.getEnemyType() == Enemy.TYPE_MONSTER2) ?  3
                        :                                                   5 + currentWave;
                frontend.updateStats(frontend.getHealth() - dmg, frontend.getMoney(), currentWave);
                enemies.remove(i);
            }
        }

        frontend.getGamePanel().repaint();
    }

    private int getWaveReward(int wave) {
        if (wave <= waveDefinitions.size())
            return waveDefinitions.get(wave - 1).reward;
        return 600 + (wave - 6) * 100;
    }

    // ── Path ───────────────────────────────────────────────────────────────────

    private ArrayList<int[]> computePath() {
        boolean[][] visited  = new boolean[GRID_HEIGHT][GRID_WIDTH];
        int[][] parentRow    = new int[GRID_HEIGHT][GRID_WIDTH];
        int[][] parentCol    = new int[GRID_HEIGHT][GRID_WIDTH];
        for (int[] row : parentRow) java.util.Arrays.fill(row, -1);

        int startR = -1, startC = -1, endR = -1, endC = -1;
        for (int r = 0; r < GRID_HEIGHT; r++)
            for (int c = 0; c < GRID_WIDTH; c++) {
                if (Grid[r][c].isStart()) { startR = r; startC = c; }
                if (Grid[r][c].isEnd())   { endR   = r; endC   = c; }
            }

        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            if (r == endR && c == endC) break;
            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < GRID_HEIGHT && nc >= 0 && nc < GRID_WIDTH
                        && !visited[nr][nc]
                        && (Grid[nr][nc].getImage() == PATH
                        || Grid[nr][nc].getImage() == END_POS
                        || Grid[nr][nc].getImage() == START_POS)) {
                    visited[nr][nc] = true;
                    parentRow[nr][nc] = r; parentCol[nr][nc] = c;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        ArrayList<int[]> path = new ArrayList<>();
        int r = endR, c = endC;
        while (r != -1 && c != -1) {
            path.add(0, new int[]{Grid[r][c].getX_cord(), Grid[r][c].getY_cord()});
            int pr = parentRow[r][c]; int pc = parentCol[r][c];
            if (pr == -1) break;
            r = pr; c = pc;
        }
        return path;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public ArrayList<Enemy>      getEnemies()    { return enemies; }
    public ArrayList<Projectile> getProjectiles(){ return projectiles; }
    public ArrayList<int[]>      getGlobalPath() { return globalPath; }
    public Square[][]            getGrid()       { return Grid; }
    public int                   getCurrentWave(){ return currentWave; }

    public void addTower(Tower tower) { towers.add(tower); }

    public Tower getTowerAt(int pixelX, int pixelY) {
        for (Tower tower : towers) {
            if (Math.abs(tower.getX() - pixelX) <= Square.SQUARE_WIDTH &&
                    Math.abs(tower.getY() - pixelY) <= Square.SQUARE_HEIGHT)
                return tower;
        }
        return null;
    }

    public void restartGame() {
        frontend.dispose();
        // Reset static Square counter so grid positions are recalculated
        Square.resetCount();
        new Game();
    }

    public static void main(String[] args) { new Game(); }
}