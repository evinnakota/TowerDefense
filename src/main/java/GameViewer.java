import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;

public class GameViewer extends JFrame {
    private Game backend;
    private int wave;
    private static int WINDOW_WIDTH  = 1920;
    private static int WINDOW_HEIGHT = 1080;
    private int health = 50;
    private int money  = 250;
    private JLabel healthLabel;
    private JLabel moneyLabel;
    private int mouseX = -1, mouseY = -1;
    private GamePanel gamePanel;
    public static final int BASIC_TOWER_COST  = 50;
    public static final int SNPIER_TOWER_COST = 150;
    private JLabel waveLabel;
    private JPanel waveOverlay;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private static final String SCREEN_START = "start";
    private static final String SCREEN_GAME  = "game";
    private static final String SCREEN_OVER  = "over";
    private int score = 0;
    private JPanel upgradeOverlay;
    private Tower selectedTower;
    private Integer selectedTowerType = null;
    public static final int CROSSBOW_TOWER_COST = 100;

    public GameViewer(Game backend) {
        this.backend = backend;
        this.setTitle("Tower Defense");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        cardLayout     = new CardLayout();
        mainContainer  = new JPanel(cardLayout);

        mainContainer.add(new StartScreen(this, this::showGameScreen), SCREEN_START);
        mainContainer.add(buildGameScreen(), SCREEN_GAME);

        this.add(mainContainer);
        this.setVisible(true);
        cardLayout.show(mainContainer, SCREEN_START);

        // ── R key → instant restart ────────────────────────────────────────────
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    backend.restartGame();
                }
            }
        });
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    public GamePanel getGamePanel()  { return gamePanel; }
    public int       getHealth()     { return health; }
    public int       getMoney()      { return money; }
    public Game      getBackend()    { return backend; }
    public Integer   getSelectedTowerType() { return selectedTowerType; }

    public void setMousePos(int x, int y) { mouseX = x; mouseY = y; }

    // ── Stats ──────────────────────────────────────────────────────────────────

    public void updateStats(int health, int money, int wave) {
        this.health = health;
        this.money  = money;
        this.wave   = wave;
        healthLabel.setText("❤ " + health);
        moneyLabel .setText("$ " + money);
        waveLabel  .setText("Wave: " + wave);
        if (health <= 0) showGameOverScreen();
    }

    // ── Drawing ────────────────────────────────────────────────────────────────

    public void drawGrid(Graphics g) {
        Square[][] grid = backend.getGrid();
        for (int i = 0; i < backend.GRID_HEIGHT; i++)
            for (int j = 0; j < backend.GRID_WIDTH; j++)
                grid[i][j].draw(g);
        for (int i = 0; i < backend.GRID_HEIGHT; i++)
            for (int j = 0; j < backend.GRID_WIDTH; j++)
                grid[i][j].drawRange(g, mouseX, mouseY);
    }

    // ── Tower placement ────────────────────────────────────────────────────────

    public void addTower(int x, int y) {
        if (selectedTowerType == null) return;

        Square[][] grid = backend.getGrid();
        int col = x / Square.SQUARE_WIDTH;
        int row = y / Square.SQUARE_HEIGHT;

        if (row >= 0 && row < backend.GRID_HEIGHT &&
                col >= 0 && col < backend.GRID_WIDTH) {
            if (grid[row][col].getImage() == Game.WALL) {
                int cost = (selectedTowerType == Game.SNIPER)   ? SNPIER_TOWER_COST
                        : (selectedTowerType == Game.CROSSBOW) ? CROSSBOW_TOWER_COST
                        :                                        BASIC_TOWER_COST;
                if (money >= cost) {
                    grid[row][col].addImage(selectedTowerType);
                    backend.addTower(new Tower(
                            grid[row][col].getX_cord(),
                            grid[row][col].getY_cord(),
                            selectedTowerType
                    ));
                    updateStats(health, money - cost, wave);
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough money!");
                }
            }
        }
    }

    // ── Upgrade overlay ────────────────────────────────────────────────────────

    public void showUpgradeOverlay(Tower tower, int clickX, int clickY) {
        hideUpgradeOverlay();
        selectedTower = tower;

        upgradeOverlay = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(20, 20, 40, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.setColor(new Color(255, 215, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            }
        };
        upgradeOverlay.setOpaque(false);
        upgradeOverlay.setLayout(new BoxLayout(upgradeOverlay, BoxLayout.Y_AXIS));
        upgradeOverlay.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        String towerName = (tower.getTowerType() == Game.SNIPER)   ? "Sniper Tower"
                : (tower.getTowerType() == Game.CROSSBOW) ? "Crossbow Tower"
                :                                           "Basic Tower";
        JLabel nameLabel  = styledLabel(towerName, 16, Font.BOLD, new Color(255,215,0));
        JLabel levelLabel = styledLabel("Level " + (tower.getUpgradeLevel()+1) + " / "
                + (tower.getMaxUpgrades()+1), 13, Font.PLAIN, Color.LIGHT_GRAY);
        JLabel dmgLabel   = styledLabel("Damage:    " + tower.getDamage(),   13, Font.PLAIN, Color.WHITE);
        JLabel rangeLabel = styledLabel("Range:     " + tower.getRange(),    13, Font.PLAIN, Color.WHITE);
        JLabel rateLabel  = styledLabel("Fire Rate: " + tower.getFireRate() + " ticks", 13, Font.PLAIN, Color.WHITE);

        JButton upgradeBtn;
        if (tower.getUpgradeLevel() >= tower.getMaxUpgrades()) {
            upgradeBtn = new JButton("Max Level");
            upgradeBtn.setEnabled(false);
            upgradeBtn.setBackground(Color.GRAY);
        } else {
            upgradeBtn = new JButton("Upgrade $" + tower.getUpgradeCost());
            upgradeBtn.setBackground(new Color(0,160,0));
            upgradeBtn.addActionListener(e -> {
                if (money >= tower.getUpgradeCost()) {
                    updateStats(health, money - tower.getUpgradeCost(), wave);
                    tower.upgrade();
                    showUpgradeOverlay(tower, clickX, clickY);
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough money!");
                }
            });
        }
        styleButton(upgradeBtn, 14, new Dimension(180,36));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(150,0,0));
        closeBtn.setForeground(Color.BLACK);
        styleButton(closeBtn, 12, new Dimension(180,30));
        closeBtn.addActionListener(e -> hideUpgradeOverlay());

        upgradeOverlay.add(nameLabel);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,4)));
        upgradeOverlay.add(levelLabel);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,8)));
        upgradeOverlay.add(dmgLabel);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,3)));
        upgradeOverlay.add(rangeLabel);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,3)));
        upgradeOverlay.add(rateLabel);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,10)));
        upgradeOverlay.add(upgradeBtn);
        upgradeOverlay.add(Box.createRigidArea(new Dimension(0,5)));
        upgradeOverlay.add(closeBtn);

        int overlayW = 210, overlayH = 240;
        int ox = Math.min(clickX + 10, gamePanel.getWidth()  - overlayW - 10);
        int oy = Math.min(clickY + 10, gamePanel.getHeight() - overlayH - 10);
        upgradeOverlay.setBounds(ox, oy, overlayW, overlayH);
        gamePanel.add(upgradeOverlay);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void hideUpgradeOverlay() {
        if (upgradeOverlay != null) {
            gamePanel.remove(upgradeOverlay);
            upgradeOverlay = null;
            gamePanel.repaint();
        }
    }

    // ── Wave complete overlay ──────────────────────────────────────────────────

    public void showWaveCompleteOverlay(int completedWave, int reward) {
        if (waveOverlay != null) gamePanel.remove(waveOverlay);

        boolean bossNext    = (completedWave > 0) && backend.isNextWaveBoss();
        boolean infiniteNow = (completedWave >= 6); // past all defined waves

        waveOverlay = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                Color bg = bossNext
                        ? new Color(80, 0, 0, 200)
                        : new Color(0, 0, 0, 150);
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                if (bossNext) {
                    g2d.setColor(new Color(255, 60, 0, 80));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
                }
            }
        };
        waveOverlay.setOpaque(false);
        waveOverlay.setLayout(new BoxLayout(waveOverlay, BoxLayout.Y_AXIS));
        waveOverlay.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title line
        String titleText;
        Color  titleColor;
        if (completedWave == 0) {
            titleText  = "Get Ready!";
            titleColor = Color.YELLOW;
        } else if (bossNext) {
            titleText  = "⚠  BOSS INCOMING  ⚠";
            titleColor = new Color(255, 60, 0);
        } else if (infiniteNow) {
            titleText  = "Wave " + completedWave + " Cleared!";
            titleColor = new Color(180, 0, 220);
        } else {
            titleText  = "Wave " + completedWave + " Complete!";
            titleColor = Color.YELLOW;
        }
        JLabel titleLabel = styledLabel(titleText, 28, Font.BOLD, titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sub-message
        String subText;
        if (completedWave == 0) {
            subText = "Place your towers before the first wave!";
        } else if (bossNext) {
            subText = "A massive Boss is approaching — reinforce NOW!";
        } else if (infiniteNow) {
            subText = "The endless horde continues... how long can you last?";
        } else {
            subText = "Reward: $" + reward;
        }
        Color subColor = bossNext ? new Color(255, 180, 0)
                : infiniteNow ? new Color(200, 130, 255)
                : Color.GREEN;
        JLabel subLabel = styledLabel(subText, 18, Font.BOLD, subColor);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hint
        String hintText = bossNext
                ? "Upgrade towers and save money for the fight!"
                : infiniteNow
                ? "Waves scale forever — good luck!"
                : "Place towers, then start the next wave!";
        JLabel hintLabel = styledLabel(hintText, 13, Font.ITALIC, Color.WHITE);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Next wave button
        int nextWaveNum = completedWave + 1;
        String btnText = (completedWave == 0) ? "Start Wave 1"
                : bossNext             ? "⚔  Face the Boss!"
                :                        "Start Wave " + nextWaveNum;
        Color btnColor = bossNext ? new Color(200, 40, 0) : new Color(0, 180, 0);

        JButton nextWaveBtn = new JButton(btnText);
        nextWaveBtn.setFont(new Font("Arial", Font.BOLD, 18));
        nextWaveBtn.setBackground(btnColor);
        nextWaveBtn.setForeground(Color.BLACK);
        nextWaveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextWaveBtn.setMaximumSize(new Dimension(260, 50));
        nextWaveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextWaveBtn.setFocusPainted(false);
        nextWaveBtn.addActionListener(e -> {
            hideWaveCompleteOverlay();
            if (completedWave == 0) backend.startFirstWave();
            else                    backend.startNextWave();
        });

        waveOverlay.add(Box.createVerticalGlue());
        waveOverlay.add(titleLabel);
        waveOverlay.add(Box.createRigidArea(new Dimension(0, 10)));
        waveOverlay.add(subLabel);
        waveOverlay.add(Box.createRigidArea(new Dimension(0, 8)));
        waveOverlay.add(hintLabel);
        waveOverlay.add(Box.createRigidArea(new Dimension(0, 20)));
        waveOverlay.add(nextWaveBtn);
        waveOverlay.add(Box.createVerticalGlue());

        gamePanel.setLayout(null);
        int overlayW = 380, overlayH = bossNext ? 240 : 210;
        int overlayX = (gamePanel.getWidth()  - overlayW) / 2;
        int overlayY = (gamePanel.getHeight() - overlayH) / 2;
        waveOverlay.setBounds(overlayX, overlayY, overlayW, overlayH);
        gamePanel.add(waveOverlay);
        gamePanel.repaint();
    }

    public void hideWaveCompleteOverlay() {
        if (waveOverlay != null) {
            gamePanel.remove(waveOverlay);
            waveOverlay = null;
            gamePanel.repaint();
        }
    }

    // ── Game over / restart ────────────────────────────────────────────────────

    public void showGameOverScreen() {
        GameOverScreen gameOverScreen = new GameOverScreen(
                this, backend.getCurrentWave(), score, this::restartGame);
        mainContainer.add(gameOverScreen, SCREEN_OVER);
        cardLayout.show(mainContainer, SCREEN_OVER);
    }

    private void restartGame() { backend.restartGame(); }

    public void addScore(int points) { score += points; }

    // ── Side panel / layout ────────────────────────────────────────────────────

    private JPanel buildGameScreen() {
        JPanel gameScreen = new JPanel(new BorderLayout());

        gamePanel = new GamePanel(this);
        gamePanel.setLayout(null);
        gameScreen.add(gamePanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(30, 30, 50));
        sidePanel.setPreferredSize(new Dimension(160, WINDOW_HEIGHT));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        healthLabel = new JLabel("❤ " + health, SwingConstants.CENTER);
        healthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        healthLabel.setForeground(Color.RED);
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        healthLabel.setMaximumSize(new Dimension(160, 60));

        moneyLabel = new JLabel("$ " + money, SwingConstants.CENTER);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        moneyLabel.setForeground(new Color(0, 200, 0));
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        moneyLabel.setMaximumSize(new Dimension(160, 30));

        waveLabel = new JLabel("Wave: 1", SwingConstants.CENTER);
        waveLabel.setFont(new Font("Arial", Font.BOLD, 20));
        waveLabel.setForeground(Color.CYAN);
        waveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waveLabel.setMaximumSize(new Dimension(160, 30));

        // ── Tower buttons ──────────────────────────────────────────────────────
        JPanel basicTowerPanel    = new JPanel(new BorderLayout());
        JPanel crossbowTowerPanel = new JPanel(new BorderLayout());
        JPanel sniperTowerPanel   = new JPanel(new BorderLayout());

        JButton basicTowerBtn    = new JButton("<html><center>Basic Tower<br>$" + BASIC_TOWER_COST    + "</center></html>");
        JButton crossbowTowerBtn = new JButton("<html><center>Crossbow<br>$"    + CROSSBOW_TOWER_COST + "</center></html>");
        JButton sniperTowerBtn   = new JButton("<html><center>Sniper Tower<br>$" + SNPIER_TOWER_COST  + "</center></html>");

        basicTowerPanel   .setMaximumSize(new Dimension(160, 60));
        crossbowTowerPanel.setMaximumSize(new Dimension(160, 60));
        sniperTowerPanel  .setMaximumSize(new Dimension(160, 60));

        basicTowerBtn.addActionListener(e -> {
            if (selectedTowerType != null && selectedTowerType == Game.BASIC_TOWER) {
                selectedTowerType = null;
                basicTowerPanel.setBackground(null);
                basicTowerBtn.setBackground(null);
            } else {
                selectedTowerType = Game.BASIC_TOWER;
                basicTowerPanel   .setBackground(new Color(0, 180, 0));
                basicTowerBtn     .setBackground(new Color(0, 180, 0));
                crossbowTowerPanel.setBackground(null); crossbowTowerBtn.setBackground(null);
                sniperTowerPanel  .setBackground(null); sniperTowerBtn  .setBackground(null);
            }
        });

        crossbowTowerBtn.addActionListener(e -> {
            if (selectedTowerType != null && selectedTowerType == Game.CROSSBOW) {
                selectedTowerType = null;
                crossbowTowerPanel.setBackground(null);
                crossbowTowerBtn.setBackground(null);
            } else {
                selectedTowerType = Game.CROSSBOW;
                crossbowTowerPanel.setBackground(new Color(180, 100, 20));
                crossbowTowerBtn  .setBackground(new Color(180, 100, 20));
                basicTowerPanel   .setBackground(null); basicTowerBtn   .setBackground(null);
                sniperTowerPanel  .setBackground(null); sniperTowerBtn  .setBackground(null);
            }
        });

        sniperTowerBtn.addActionListener(e -> {
            if (selectedTowerType != null && selectedTowerType == Game.SNIPER) {
                selectedTowerType = null;
                sniperTowerPanel.setBackground(null);
                sniperTowerBtn.setBackground(null);
            } else {
                selectedTowerType = Game.SNIPER;
                sniperTowerPanel  .setBackground(new Color(0, 180, 0));
                sniperTowerBtn    .setBackground(new Color(0, 180, 0));
                basicTowerPanel   .setBackground(null); basicTowerBtn   .setBackground(null);
                crossbowTowerPanel.setBackground(null); crossbowTowerBtn.setBackground(null);
            }
        });

        basicTowerPanel   .add(basicTowerBtn,    BorderLayout.CENTER);
        crossbowTowerPanel.add(crossbowTowerBtn, BorderLayout.CENTER);
        sniperTowerPanel  .add(sniperTowerBtn,   BorderLayout.CENTER);

        // ── Restart hint (bottom) ──────────────────────────────────────────────
        JLabel restartHint = new JLabel("[R] Restart", SwingConstants.CENTER);
        restartHint.setFont(new Font("Arial", Font.ITALIC, 12));
        restartHint.setForeground(new Color(180, 180, 180));
        restartHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartHint.setMaximumSize(new Dimension(160, 20));

        // ── Add in order: stats → towers → spacer → restart hint ──────────────
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(healthLabel);
        sidePanel.add(moneyLabel);
        sidePanel.add(waveLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 12)));
        sidePanel.add(basicTowerPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidePanel.add(crossbowTowerPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidePanel.add(sniperTowerPanel);
        sidePanel.add(Box.createVerticalGlue());  // pushes restart hint to bottom
        sidePanel.add(restartHint);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        gameScreen.add(sidePanel, BorderLayout.EAST);
        return gameScreen;
    }

    private void showGameScreen() {
        cardLayout.show(mainContainer, SCREEN_GAME);
        showWaveCompleteOverlay(0, 0);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", style, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(340, size + 10));
        return lbl;
    }

    private void styleButton(JButton btn, int fontSize, Dimension size) {
        btn.setFont(new Font("Arial", Font.BOLD, fontSize));
        btn.setForeground(Color.BLACK);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(size);
        btn.setPreferredSize(size);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }
}