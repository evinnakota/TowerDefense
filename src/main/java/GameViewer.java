import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameViewer extends JFrame {
    private Game backend;
    private int wave;
    private static int WINDOW_WIDTH = 1920;
    private static int WINDOW_HEIGHT = 1080;
    private int selectedTowerType = Game.BASIC_TOWER;
    private int health = 100;
    private int money = 1500;
    private JLabel healthLabel;
    private JLabel moneyLabel;
    private int mouseX = -1;
    private int mouseY = -1;
    private GamePanel gamePanel;
    public static final int BASIC_TOWER_COST = 75;
    public static final int SNPIER_TOWER_COST = 150;

    public GameViewer(Game backend) {
        this.backend = backend;
        this.setTitle("State Tracker!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        this.setLayout(new BorderLayout());

        // Drawing panel
        gamePanel = new GamePanel(this);
        this.add(gamePanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(150, WINDOW_HEIGHT));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        healthLabel = new JLabel("❤ " + health, SwingConstants.CENTER);
        healthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        healthLabel.setForeground(Color.RED);
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        healthLabel.setMaximumSize(new Dimension(150, 60));

        moneyLabel = new JLabel ("$ " + money, SwingConstants.CENTER);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        moneyLabel.setForeground(new Color(0, 150, 0));
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        moneyLabel.setMaximumSize(new Dimension(150, 30));

        JPanel basicTowerPanel = new JPanel(new BorderLayout());
        basicTowerPanel.setMaximumSize(new Dimension(150, 200));
        JButton basicTowerBtn = new JButton("Basic Tower $" + BASIC_TOWER_COST);
        basicTowerBtn.addActionListener(e -> selectedTowerType = Game.BASIC_TOWER);
        basicTowerPanel.add(basicTowerBtn, BorderLayout.CENTER);

        JPanel sniperTowerPanel = new JPanel(new BorderLayout());
        sniperTowerPanel.setMaximumSize(new Dimension(150, 200));
        JButton sniperTowerBtn = new JButton("Sniper Tower $" + SNPIER_TOWER_COST);
        sniperTowerBtn.addActionListener(e -> selectedTowerType = Game.SNIPER);
        sniperTowerPanel.add(sniperTowerBtn, BorderLayout.CENTER);

        JPanel sidePane1 = new JPanel();
        sidePane1.setPreferredSize(new Dimension(150, WINDOW_HEIGHT));
        sidePane1.setLayout(new GridLayout(0, 1));

        sidePanel.add(healthLabel);
        sidePanel.add(moneyLabel);
        sidePanel.add(basicTowerPanel);
        sidePanel.add(sniperTowerPanel);

        this.add(sidePanel, BorderLayout.EAST);
        this.setVisible(true);
//        new Timer(300, e -> {
//            for (Enemy enemy : enemies) {
//                enemy.move(backend.getGrid());
//            }
//            for (Tower tower : towers) {
//                tower.attack(enemies);
//            }
//            repaint();
//        }).start();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void updateStats(int health, int money) {
        this.health = health;
        this.money = money;
        healthLabel.setText("❤ " + health);
        moneyLabel.setText("$ " + money);
    }

    public void setMousePos(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public Game getBackend() {
        return backend;
    }

    public void drawGrid(Graphics g) {
        Square grid[][] = backend.getGrid();
        for (int i = 0; i < backend.GRID_HEIGHT; i++) {
            for (int j = 0; j < backend.GRID_WIDTH; j++) {
                grid[i][j].draw(g);
            }
        }




        for (int i = 0; i < backend.GRID_HEIGHT; i++) {
            for (int j = 0; j < backend.GRID_WIDTH; j++) {
                grid[i][j].drawRange(g, mouseX, mouseY);
            }
        }
//        for (Enemy enemy : enemies) {
//            enemy.draw(g);
//        }
//        for (Tower tower : towers) {
//            tower.drawRange(g);
//        }
//        g.setColor(Color.WHITE);
//        g.fillRect(10, 40, 150, 30);
//
//        g.setColor(Color.BLACK);
//        g.drawString("Health: " + health, 20, 60);
    }

    public void updateGame() {

    }

    public void spawnWave() {

    }

    public void addTower(int x, int y) {
        Square[][] grid = backend.getGrid();

        int col = x / Square.SQUARE_WIDTH;
        int row = y / Square.SQUARE_HEIGHT;

        if (row >= 0 && row < backend.GRID_HEIGHT &&
                col >= 0 && col < backend.GRID_WIDTH) {
            if (grid[row][col].getImage() == Game.WALL) {
                int cost = (selectedTowerType == Game.SNIPER) ? SNPIER_TOWER_COST : BASIC_TOWER_COST;
                if (money >= cost) {
                    grid[row][col].addImage(selectedTowerType);
                    updateStats(health, money-cost);
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough Money!");
                }
            }
        }
    }



}
