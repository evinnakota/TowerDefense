import java.util.ArrayList;

public class Tower {
    private int x, y, range, damage, cooldown = 0;

    public Tower(int x, int y, int type) {
        this.x = x;
        this.y = y;

        // Assign stats based on the tower type constant
        if (type == Game.SNIPER) {
            this.range = Square.SNIPER_TOWER_RANGE;
            this.damage = 40;
        } else {
            this.range = Square.BASIC_TOWER_RANGE;
            this.damage = 10;
        }
    }

    public void attack(ArrayList<Enemy> enemies) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        for (Enemy e : enemies) {
            if (inRange(e)) {
                e.takeDamage(damage);
                cooldown = 15; // Fire rate delay
                break;
            }
        }
    }

    public boolean inRange(Enemy e) {
        double dist = Math.sqrt(Math.pow(x - e.getX(), 2) + Math.pow(y - e.getY(), 2));
        return dist <= range;
    }
}