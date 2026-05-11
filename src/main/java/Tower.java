import java.awt.*;
import java.util.ArrayList;

public class Tower {
    private int x, y, range, damage, fireRate;
    private int fireCooldown = 0;
    private int towerType;
    private int upgradeLevel  = 0;
    private static final int MAX_UPGRADES = 5;

    public Tower(int x, int y, int towerType) {
        this.x = x + Square.SQUARE_WIDTH  / 2;
        this.y = y + Square.SQUARE_HEIGHT / 2;
        this.towerType = towerType;

        if (towerType == Game.SNIPER) {
            this.range    = Square.SNIPER_TOWER_RANGE;
            this.damage   = 40;
            this.fireRate = 60;
        } else if (towerType == Game.CROSSBOW) {
            this.range    = Square.CROSSBOW_TOWER_RANGE;
            this.damage   = 25;   // between basic (10) and sniper (40)
            this.fireRate = 35;   // slower than basic (20), faster than sniper (60)
        } else {                  // BASIC_TOWER
            this.range    = Square.BASIC_TOWER_RANGE;
            this.damage   = 10;
            this.fireRate = 20;
        }
    }

    public boolean upgrade() {
        if (upgradeLevel >= MAX_UPGRADES) return false;
        upgradeLevel++;
        if (towerType == Game.SNIPER) {
            damage   += 15;
            range    += 15;
            fireRate  = Math.max(5, fireRate - 3);
        } else if (towerType == Game.CROSSBOW) {
            damage   += 10;   // solid damage jumps
            range    += 12;
            fireRate  = Math.max(10, fireRate - 4); // gets meaningfully faster
        } else {
            damage   += 5;
            range    += 15;
            fireRate  = Math.max(5, fireRate - 3);
        }
        return true;
    }

    public int getUpgradeCost() {
        int base = (towerType == Game.SNIPER)   ? 75
                : (towerType == Game.CROSSBOW) ? 60
                :                                50;
        return base + (upgradeLevel * 25);
    }

    public Projectile attack(ArrayList<Enemy> enemies) {
        if (fireCooldown > 0) { fireCooldown--; return null; }
        for (Enemy enemy : enemies) {
            if (inRange(enemy)) {
                fireCooldown = fireRate;
                int projSpeed = (towerType == Game.SNIPER)   ? 14
                        : (towerType == Game.CROSSBOW) ? 9   // heavier bolt, mid speed
                        :                                6;
                int projType  = (towerType == Game.CROSSBOW) ? Projectile.TYPE_BOLT
                        :                                 Projectile.TYPE_BULLET;
                return new Projectile(x, y, enemy, damage, projSpeed, projType);
            }
        }
        return null;
    }

    public boolean inRange(Enemy e) {
        int ex = e.getX() + Square.SQUARE_WIDTH  / 2;
        int ey = e.getY() + Square.SQUARE_HEIGHT / 2;
        return Math.sqrt(Math.pow(x - ex, 2) + Math.pow(y - ey, 2)) <= range;
    }

    public void drawRange(Graphics g) {
        g.setColor(new Color(0, 0, 255, 80));
        g.drawOval(x - range, y - range, range * 2, range * 2);
    }

    public int getUpgradeLevel() { return upgradeLevel; }
    public int getMaxUpgrades()  { return MAX_UPGRADES; }
    public int getDamage()       { return damage; }
    public int getRange()        { return range; }
    public int getFireRate()     { return fireRate; }
    public int getTowerType()    { return towerType; }
    public int getX()            { return x; }
    public int getY()            { return y; }
}