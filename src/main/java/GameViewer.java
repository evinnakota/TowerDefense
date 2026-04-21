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
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 500;

    public GameViewer(Game backend) {
        this.backend = backend;
        this.setTitle("State Tracker!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setVisible(true);
    }

    public void paint(Graphics g) {

    }

    public void updateGame() {

    }

    public void spawnWave() {

    }

    public void addTower(int x, int y) {

    }



}
