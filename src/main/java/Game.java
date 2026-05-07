import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Game implements ActionListener {
    public static final int GRID_WIDTH = 59;
    public static final int GRID_HEIGHT = 33;

    // Grid type constants
    public static final int WALL = 0, PATH = 1, START_POS = 2, END_POS = 3;
    public static final int BASIC_TOWER = 4, SNIPER = 5;

    private GameViewer frontend;
    private Square[][] grid;
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private Timer timer;

    public Game() {
        // 1. Initialize the grid objects first
        grid = new Square[GRID_HEIGHT][GRID_WIDTH];
        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {
                grid[r][c] = new Square();
            }
        }

        enemies = new ArrayList<>();
        towers = new ArrayList<>();


        // 2. Start the UI
        frontend = new GameViewer(this);

        // 3. Start the game loop (30ms per frame)
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // This runs 33 times per second
        for (Enemy en : enemies) {
            en.move(grid); // Requires updating your Enemy.java move method
        }
        frontend.repaint();
    }
    // Inside Game.java
    public void addTower(Tower t) {
        // This assumes you have: private ArrayList<Tower> towers;
        towers.add(t);
    }
    public Square[][] getGrid() { return grid; }
    public ArrayList<Enemy> getEnemies() { return enemies; }

    public static void main(String[] args) {
        new Game();
    }
}