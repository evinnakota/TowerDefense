import java.awt.*;

public class Enemy {
    private int x, y, health, speed;
    private int lastRow = -1, lastCol = -1;

    public Enemy(int x, int y, int health, int speed) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
    }

    public void move(Square[][] grid) {
        int col = x / Square.SQUARE_WIDTH;
        int row = (y - Square.bar) / Square.SQUARE_HEIGHT;

        // Turn only at the center of a tile
        if (x % Square.SQUARE_WIDTH == 0 && (y - Square.bar) % Square.SQUARE_HEIGHT == 0) {
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Down, Left, Up
            for (int[] d : directions) {
                int nr = row + d[0], nc = col + d[1];
                if (nr >= 0 && nr < Game.GRID_HEIGHT && nc >= 0 && nc < Game.GRID_WIDTH) {
                    if (grid[nr][nc].getImage() == Game.PATH || grid[nr][nc].getImage() == Game.END_POS) {
                        if (!(nr == lastRow && nc == lastCol)) {
                            lastRow = row; lastCol = col;
                            break;
                        }
                    }
                }
            }
        }

        // Apply movement
        if (lastCol < col) x += speed;
        else if (lastCol > col) x -= speed;
        else if (lastRow < row) y += speed;
        else if (lastRow > row) y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, Square.SQUARE_WIDTH, Square.SQUARE_HEIGHT);
    }

    public boolean reachedEnd(Square[][] grid) {
        int r = (y - Square.bar) / Square.SQUARE_HEIGHT;
        int c = x / Square.SQUARE_WIDTH;
        return grid[r][c].getImage() == Game.END_POS;
    }

    public void takeDamage(int dmg) { health -= dmg; }
    public boolean isAlive() { return health > 0; }
    public int getX() { return x; }
    public int getY() { return y; }
}