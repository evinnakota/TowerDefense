import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Enemy {
    public static final int TYPE_MONSTER1 = 1;
    public static final int TYPE_MONSTER2 = 2;
    public static final int TYPE_BOSS     = 3;

    private int x;
    private int y;
    private int health;
    private int maxHealth;
    private int speed;
    private int pathIndex = 0;
    private ArrayList<int[]> path;
    private int enemyType;
    private double velocityX = 0;
    private double velocityY = 0;

    private static Image monster1Img;
    private static Image monster2Img;
    private static Image bossImg;

    static {
        try { monster1Img = new ImageIcon(Enemy.class.getResource("/monster1.png")).getImage(); }
        catch (Exception e) { System.err.println("Failed to load monster1: " + e.getMessage()); }
        try { monster2Img = new ImageIcon(Enemy.class.getResource("/monster2.png")).getImage(); }
        catch (Exception e) { System.err.println("Failed to load monster2: " + e.getMessage()); }
        try { bossImg = new ImageIcon(Enemy.class.getResource("/Boss.png")).getImage(); }
        catch (Exception e) { System.err.println("Failed to load Boss: " + e.getMessage()); }
    }

    public Enemy(int startX, int startY, int health, int speed, ArrayList<int[]> path, int enemyType) {
        this.x = startX;
        this.y = startY;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.path = path;
        this.enemyType = enemyType;
    }

    public int getEnemyType() { return enemyType; }

    public boolean reachedEnd(Square[][] grid) {
        return pathIndex >= path.size();
    }

    public void move(Square[][] grid) {
        if (pathIndex >= path.size()) return;

        int[] target = path.get(pathIndex);
        int tx = target[0];
        int ty = target[1];
        int dx = tx - x;
        int dy = ty - y;

        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = tx;
            y = ty;
            pathIndex++;
            if (pathIndex < path.size()) {
                int[] next = path.get(pathIndex);
                double ndx = next[0] - x;
                double ndy = next[1] - y;
                double dist = Math.sqrt(ndx * ndx + ndy * ndy);
                velocityX = dist > 0 ? (ndx / dist) * speed : 0;
                velocityY = dist > 0 ? (ndy / dist) * speed : 0;
            }
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {
                x += (dx > 0) ? speed : -speed;
                velocityX = (dx > 0) ? speed : -speed;
                velocityY = 0;
            } else {
                y += (dy > 0) ? speed : -speed;
                velocityX = 0;
                velocityY = (dy > 0) ? speed : -speed;
            }
        }
    }

    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }

    public void draw(Graphics g) {
        boolean isBoss = (enemyType == TYPE_BOSS);
        int drawW = isBoss ? Square.SQUARE_WIDTH  * 2 : Square.SQUARE_WIDTH;
        int drawH = isBoss ? Square.SQUARE_HEIGHT * 2 : Square.SQUARE_HEIGHT;
        int drawX = isBoss ? x - Square.SQUARE_WIDTH  / 2 : x;
        int drawY = isBoss ? y - Square.SQUARE_HEIGHT / 2 : y;

        Image img = switch (enemyType) {
            case TYPE_MONSTER2 -> monster2Img;
            case TYPE_BOSS     -> bossImg;
            default            -> monster1Img;
        };

        if (img != null) {
            g.drawImage(img, drawX, drawY, drawW, drawH, null);
        } else {
            g.setColor(enemyType == TYPE_MONSTER2 ? new Color(100, 200, 100)
                    : enemyType == TYPE_BOSS     ? new Color(180, 0, 220)
                    : new Color(200, 80, 80));
            g.fillOval(drawX, drawY, drawW, drawH);
        }

        // Health bar — wider for boss
        int barW = isBoss ? Square.SQUARE_WIDTH * 2 : Square.SQUARE_WIDTH;
        int barX = isBoss ? x - Square.SQUARE_WIDTH / 2 : x;
        g.setColor(Color.RED);
        g.fillRect(barX, drawY - 8, barW, 5);
        g.setColor(isBoss ? new Color(180, 0, 220) : Color.GREEN);
        int healthWidth = (int)(((double) health / maxHealth) * barW);
        g.fillRect(barX, drawY - 8, healthWidth, 5);
    }

    public void takeDamage(int dmg) { health -= dmg; }
    public boolean isAlive()        { return health > 0; }
    public int getX()               { return x; }
    public int getY()               { return y; }
    public int getHealth()          { return health; }
}