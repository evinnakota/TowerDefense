import javax.swing.*;
import java.awt.*;

public class Enemy {
    private int x;
    private int y;
    private int health;
    private int speed;
    private int pathIndex;
    private int lastX;
    private int lastY;
    private int lastRow;
    private int lastCol;
    private Image monster1 = new ImageIcon(getClass().getResource("/monster1.png")).getImage();

    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.lastX = startX;
        this.lastY = startY;
        this.health = 100;
        this.speed = Square.SQUARE_WIDTH;
        this.lastRow = -1;
        this.lastCol = -1;
    }



    public void move(Square[][] grid) {
        int row = (y - Square.bar) / Square.SQUARE_HEIGHT;
        int col = x / Square.SQUARE_WIDTH;

        if (row < 0 || row >= Game.GRID_HEIGHT ||
                col < 0 || col >= Game.GRID_WIDTH) {
            return;
        }

        // RIGHT
        if (col + 1 < Game.GRID_WIDTH &&
                (grid[row][col + 1].getImage() == Game.PATH ||
                        grid[row][col + 1].getImage() == Game.END_POS) &&
                !(row == lastRow && col + 1 == lastCol)) {

            lastRow = row;
            lastCol = col;
            x += speed;
        }

        // DOWN
        else if (row + 1 < Game.GRID_HEIGHT &&
                (grid[row + 1][col].getImage() == Game.PATH ||
                        grid[row + 1][col].getImage() == Game.END_POS) &&
                !(row + 1 == lastRow && col == lastCol)) {

            lastRow = row;
            lastCol = col;
            y += speed;
        }

        // LEFT
        else if (col - 1 >= 0 &&
                (grid[row][col - 1].getImage() == Game.PATH ||
                        grid[row][col - 1].getImage() == Game.END_POS) &&
                !(row == lastRow && col - 1 == lastCol)) {

            lastRow = row;
            lastCol = col;
            x -= speed;
        }

        // UP
        else if (row - 1 >= 0 &&
                (grid[row - 1][col].getImage() == Game.PATH ||
                        grid[row - 1][col].getImage() == Game.END_POS) &&
                !(row - 1 == lastRow && col == lastCol)) {

            lastRow = row;
            lastCol = col;
            y -= speed;
        }
    }


    public void draw(Graphics g) {
        // Enemy body
        g.drawImage(monster1, getX(), getY(), Square.SQUARE_WIDTH, Square.SQUARE_HEIGHT, null);

        // Health bar background
        g.setColor(Color.RED);
        g.fillRect(x, y - 8, Square.SQUARE_WIDTH, 5);

        // Current health
        g.setColor(Color.GREEN);
        int healthWidth = (int)((health / 100.0) * Square.SQUARE_WIDTH);
        g.fillRect(x, y - 8, healthWidth, 5);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getHealth() {
        return health;
    }
}