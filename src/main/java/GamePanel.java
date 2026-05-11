import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private GameViewer viewer;
    private ArrayList<Enemy> enemies;

    public GamePanel(GameViewer viewer) {
        this.viewer = viewer;
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (SwingUtilities.isRightMouseButton(e)) {
                    // Right click — open upgrade menu
                    Tower clickedTower = viewer.getBackend().getTowerAt(x, y);
                    if (clickedTower != null) {
                        viewer.showUpgradeOverlay(clickedTower, x, y);
                    } else {
                        viewer.hideUpgradeOverlay();
                    }

                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    viewer.hideUpgradeOverlay();
                    if (viewer.getSelectedTowerType() != null) {
                        viewer.addTower(x, y);
                    }
                }
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                viewer.setMousePos(e.getX(), e.getY());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0x2CB433));
        g.fillRect(0,0, viewer.getWindowWidth(), viewer.getWindowHeight());
        viewer.drawGrid(g);
        for (Enemy enemy : viewer.getBackend().getEnemies()) {
            enemy.draw(g);
            // Draw projectiles
            for (Projectile p : viewer.getBackend().getProjectiles()) {
                p.draw(g);
            }
        }

    }
}