import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameViewer extends JFrame {
    private Game backend;

    // Collections
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Projectile> projectiles;

    // UI Components
    private JLabel healthLabel;
    private JLabel moneyLabel;
    private JLabel waveLabel;

    // Game State
    private int selectedTowerType = Game.BASIC_TOWER;
    private int health;
    private int money;
    private int wave;

    // Constants for Costs
    public static final int BASIC_TOWER_COST = 75;
    public static final int SNIPER_TOWER_COST = 150;

    public GameViewer(Game backend) {
        this.backend = backend;

        // Initialize State
        this.health = 100;
        this.money = 1500;
        this.wave = 1;

        // Initialize Collections
        this.enemies = new ArrayList<>();
        this.towers = new ArrayList<>();
        this.projectiles = new ArrayList<>();

        // UI Setup
        healthLabel = new JLabel("❤ " + health);
        moneyLabel = new JLabel("🪙 " + money);
        waveLabel = new JLabel("Wave: " + wave);

        JPanel statsPanel = new JPanel();
        statsPanel.add(healthLabel);
        statsPanel.add(moneyLabel);
        statsPanel.add(waveLabel);
        this.add(statsPanel, BorderLayout.NORTH);

        this.setTitle("Tower Defense");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the drawing canvas
        GamePanel panel = new GamePanel(this);
        this.add(panel);

        this.setSize(1500, 900);
        this.setVisible(true);
    }
    public int getSelectedTowerType() {
        return selectedTowerType;
    }
    // Fixed addTower with proper money logic
    public void addTower(int x, int y) {
        Square[][] grid = backend.getGrid();
        int col = x / Square.SQUARE_WIDTH;
        int row = (y - Square.bar) / Square.SQUARE_HEIGHT;

        if (row >= 0 && row < Game.GRID_HEIGHT && col >= 0 && col < Game.GRID_WIDTH) {
            if (grid[row][col].getImage() == Game.WALL) {

                // Determine cost based on selection
                int cost = (selectedTowerType == Game.SNIPER) ? SNIPER_TOWER_COST : BASIC_TOWER_COST;

                if (this.money >= cost) {
                    // Update visual tile
                    grid[row][col].addImage(selectedTowerType);

                    // Add functional tower to logic
                    backend.addTower(new Tower(
                            grid[row][col].getX_cord(),
                            grid[row][col].getY_cord(),
                            selectedTowerType
                    ));

                    // Deduct money and refresh UI
                    updateStats(this.health, this.money - cost, this.wave);
                }
            }
        }
    }

    // Required by GamePanel motion listener
    public void setMousePos(int x, int y) {
        // This can be used later to highlight squares or draw range circles
    }

    public void updateStats(int newHealth, int newMoney, int newWave) {
        this.health = newHealth;
        this.money = newMoney;
        this.wave = newWave;

        healthLabel.setText("❤ " + health);
        moneyLabel.setText("🪙 " + money);
        waveLabel.setText("Wave: " + wave);
    }

    public Game getBackend() {
        return backend;
    }

    public void drawGrid(Graphics g) {
        Square[][] grid = backend.getGrid();
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                grid[r][c].draw(g);
            }
        }
    }
}