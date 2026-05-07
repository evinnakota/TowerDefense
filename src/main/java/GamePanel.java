import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private GameViewer viewer;
    private Game backend;

    public GamePanel(GameViewer viewer) {
        // 1. Establish the connection to the main window
        this.viewer = viewer;
        backend = viewer.getBackend();

        // 2. Mouse Click Listener: Handles tower placement
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Calls the method in GameViewer to try and place a tower
                viewer.addTower(x, y);

                // Refresh the screen to show the new tower immediately
                repaint();
            }
        });

        // 3. Mouse Motion Listener: Tracks movement for range previews
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                // Tells the viewer where the mouse is so it can highlight squares
                viewer.setMousePos(e.getX(), e.getY());

                // Refresh to update the hover effect or range circle
                repaint();
            }
        });

        // 4. Visual properties (Optional but recommended)
        this.setFocusable(true);
        this.setBackground(Color.WHITE); // Ensures the background isn't transparent or grey
    }    // Add this inside GameViewer.java
    public void addTower(int x, int y) {
        Square[][] grid = backend.getGrid();

        int col = x / Square.SQUARE_WIDTH;
        int row = y / Square.SQUARE_HEIGHT;

        // Check boundary limits
        if (row >= 0 && row < Game.GRID_HEIGHT && col >= 0 && col < Game.GRID_WIDTH) {
            if (grid[row][col].getImage() == Game.WALL) {
                // Pick a cost based on the selected type
                int cost = (viewer.getSelectedTowerType() == Game.SNIPER) ? SNIPER_TOWER_COST : BASIC_TOWER_COST;

                if (money >= cost) {
                    // Change the visual tile to a tower
                    grid[row][col].addImage(selectedTowerType);

                    // Add the physical tower to the backend
                    backend.addTower(new Tower(
                            grid[row][col].getX_cord(),
                            grid[row][col].getY_cord(),
                            selectedTowerType
                    ));

                    // Deduct cost and update the labels
                    updateStats(health, money - cost, wave);
                }
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        viewer.drawGrid(g);

        ArrayList<Enemy> enemies = viewer.getBackend().getEnemies();
        // Standard loop prevents ConcurrentModificationException
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
    }
}