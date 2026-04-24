import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameViewer extends JFrame {
    private Game backend;
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Projectile> projectiles;
    private int health;
    private int money;
    private int wave;
    private static int WINDOW_WIDTH = 1920;
    private static int WINDOW_HEIGHT = 1080;

    public GameViewer(Game backend) {
        this.backend = backend;
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                addTower(x, y);
                repaint();
            }
        });
        this.setTitle("State Tracker!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setVisible(true);
    }

    public void drawGrid(Graphics g) {
        Square grid[][] = backend.getGrid();
        for (int i = 0; i < backend.GRID_HEIGHT; i++) {
            for (int j = 0; j < backend.GRID_WIDTH; j++) {
                grid[i][j].draw(g);
            }
        }
    }

    public void paint(Graphics g) {
        drawGrid(g);
    }

    public void updateGame() {

    }

    public void spawnWave() {

    }

    public void addTower(int x, int y) {
        Square[][] grid = backend.getGrid();

        int col = x / Square.SQUARE_WIDTH;
        int row = (y - Square.bar) / Square.SQUARE_HEIGHT;

        if (row >= 0 && row < backend.GRID_HEIGHT &&
                col >= 0 && col < backend.GRID_WIDTH) {

            grid[row][col].addImage(Game.TOWER);
        }
    }



}
