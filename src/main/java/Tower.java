import java.awt.*;
import java.util.ArrayList;

public class Tower {
    private int x;
    private int y;
    private int range;
    private int damage;
    private int fireRate;

    public Tower(int x, int y) {
        this.x = x;
        this.y = y;

        // Default tower stats
        this.range = 100;
        this.damage = 10;
        this.fireRate = 30;
    }

    public void drawRange(Graphics g) {
        g.setColor(new Color(0, 0, 255, 80));

        g.drawOval(
                x - range + Square.SQUARE_WIDTH / 2,
                y - range + Square.SQUARE_HEIGHT / 2,
                range * 2,
                range * 2
        );
    }

    public void attack(ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (inRange(enemy)) {
                enemy.takeDamage(damage);
                break;
            }
        }
    }
    public boolean inRange(Enemy e) {
        double distance = Math.sqrt(Math.pow(x - e.getX(), 2) + Math.pow(y - e.getY(), 2));
        return distance <= range;
    }

    public void upgrade() {
        range += 20;
        damage += 5;
    }



}
