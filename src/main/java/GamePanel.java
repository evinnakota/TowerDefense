import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private GameViewer viewer;
    private ArrayList<Enemy> enemies;

    public GamePanel(GameViewer viewer) {
        this.viewer = viewer;
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                viewer.addTower(x, y);
                repaint();
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                viewer.setMousePos(e.getX(), e.getY()); {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        viewer.drawGrid(g);
        for (Enemy enemy : viewer.getBackend().getEnemies()) {
            enemy.draw(g);
        }
    }
}
