import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game implements ActionListener {
    private int spawnCounter = 0;
    private int enemiesSpawned = 0;
    private int currentWave = 1;
    private int enemiesPerWave = 5;
    private int enemySpeed = 5;
    private static final int SLEEP_TIME = 150;
    private GameViewer frontend;
    private boolean running;
    private Square[][] Grid;
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Projectile> projectiles;
    public static int GRID_HEIGHT = 33;
    public static int GRID_WIDTH = 59;
    public static final int WALL = 1;
    public static final int PATH = 2;
    public static final int START_POS = 3;
    public static final int END_POS = 4;
    public static final int BASIC_TOWER = 5;
    public static final int SNIPER = 6;


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

                    if (c == '#') {
                        Grid[row][col].addImage(WALL);
                    } else if (c == ' ') {
                        Grid[row][col].addImage(PATH);
                    } else if (c == 'S') {
                        Grid[row][col].addImage(START_POS);
                    } else if (c == 'E') {
                        Grid[row][col].addImage(END_POS);
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        frontend = new GameViewer(this);
        enemies = new ArrayList<>();
        towers = new ArrayList<>();

        // Find starting square and spawn enemy
        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {
                if (Grid[r][c].getImage() == Game.START_POS) {
                    enemies.add(new Enemy(
                            Grid[r][c].getX_cord(),
                            Grid[r][c].getY_cord()
                    ));
                }
            }
        }
        Timer clock = new Timer(SLEEP_TIME, this);
        clock.start();
        startGame();
    }

    public void spawnEnemy() {
        int enemyHealth = 50 + (currentWave * 25);

        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {

                if (Grid[r][c].getImage() == START_POS) {

                    enemies.add(new Enemy(
                            Grid[r][c].getX_cord(),
                            Grid[r][c].getY_cord()
                    ));

                    return;
                }
            }
        }
    }
    public void addTower(Tower tower) {
        towers.add(tower);
    }
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void startGame() {
        frontend.repaint();
    }

    public Square[][] getGrid() {
        return Grid;
    }

    public void gameLoop() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        spawnCounter++;

        // Spawn enemies for current wave
        if (enemiesSpawned < enemiesPerWave) {

            if (spawnCounter >= 25) {
                spawnEnemy();
                enemiesSpawned++;
                spawnCounter = 0;
            }

        }

        // Start next wave when all enemies are defeated
        else if (enemies.isEmpty()) {

            currentWave++;

            // Increase difficulty each wave
            enemiesPerWave += 3;

            enemiesSpawned = 0;
            spawnCounter = 0;

            System.out.println("Wave " + currentWave + " started!");
        }

        // Towers attack enemies
        for (Tower tower : towers) {
            tower.attack(enemies);
        }

        // Enemy updates
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.move(Grid);

            // Enemy defeated
            if (!enemy.isAlive()) {

                frontend.updateStats(
                        frontend.getHealth(),
                        frontend.getMoney() + 25
                );

                enemies.remove(i);
            }

            // Enemy reaches end
            else if (enemy.reachedEnd(Grid)) {

                frontend.updateStats(
                        frontend.getHealth() - 10,
                        frontend.getMoney()
                );

                enemies.remove(i);
            }
        }

        // Game over
        if (frontend.getHealth() <= 0) {
            JOptionPane.showMessageDialog(frontend, "Game Over!");
            System.exit(0);
        }

        // Redraw screen
        frontend.getGamePanel().repaint();
    }    public static void main(String[] args) {
        Game g = new Game();
    }


}
