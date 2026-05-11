import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Projectile {
    public static final int TYPE_BULLET = 0;
    public static final int TYPE_BOLT   = 1;

    private double x, y, dx, dy;
    private int    damage;
    private int    speed;
    private boolean active = true;
    private double  angle;
    private int     projectileType;

    private static Image bulletImg;
    private static Image boltImg;

    static {
        try {
            bulletImg = new ImageIcon(Projectile.class.getResource("/tower_Projectile.png")).getImage();
        } catch (Exception e) { System.err.println("Failed to load bullet: " + e.getMessage()); }
        try {
            boltImg = new ImageIcon(Projectile.class.getResource("/bolt.png")).getImage();
        } catch (Exception e) { System.err.println("Failed to load bolt: " + e.getMessage()); }
    }

    public Projectile(int startX, int startY, Enemy target, int damage, int speed, int projectileType) {
        this.x = startX;
        this.y = startY;
        this.damage = damage;
        this.speed  = speed;
        this.projectileType = projectileType;

        int tx = target.getX() + Square.SQUARE_WIDTH  / 2;
        int ty = target.getY() + Square.SQUARE_HEIGHT / 2;

        double predictedX = tx, predictedY = ty;
        for (int i = 0; i < 3; i++) {
            double dist     = Math.sqrt(Math.pow(predictedX - x, 2) + Math.pow(predictedY - y, 2));
            double lookahead = Math.min(dist / speed, dist / (speed * 2));
            predictedX = tx + target.getVelocityX() * lookahead;
            predictedY = ty + target.getVelocityY() * lookahead;
        }

        double predDist = Math.sqrt(Math.pow(predictedX - x, 2) + Math.pow(predictedY - y, 2));
        this.dx = (predictedX - x) / predDist * speed;
        this.dy = (predictedY - y) / predDist * speed;
        this.angle = (projectileType == TYPE_BOLT)
                ? Math.atan2(dy, dx) - Math.PI / 2   // bolt image faces opposite direction
                : Math.atan2(dy, dx) + Math.PI / 2;
    }

    public void move() { x += dx; y += dy; }

    public boolean hits(Enemy enemy) {
        int ex = enemy.getX() + Square.SQUARE_WIDTH  / 2;
        int ey = enemy.getY() + Square.SQUARE_HEIGHT / 2;
        // Bolts have a slightly larger hitbox to match their size
        int threshold = (projectileType == TYPE_BOLT) ? 14 : 10;
        return Math.sqrt(Math.pow(ex - x, 2) + Math.pow(ey - y, 2)) < threshold;
    }

    public void draw(Graphics g) {
        Graphics2D g2d   = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();
        g2d.rotate(angle, x, y);

        if (projectileType == TYPE_BOLT && boltImg != null) {
            // Slightly larger render for the chunky bolt
            g2d.drawImage(boltImg, (int) x - 8, (int) y - 8, 16, 16, null);
        } else if (bulletImg != null) {
            g2d.drawImage(bulletImg, (int) x - 6, (int) y - 6, 12, 12, null);
        } else {
            g2d.setColor(projectileType == TYPE_BOLT ? new Color(180, 100, 20) : Color.YELLOW);
            g2d.fillOval((int) x - 4, (int) y - 4, 8, 8);
        }

        g2d.setTransform(old);
    }

    public boolean isActive()  { return active; }
    public void deactivate()   { active = false; }
    public int  getDamage()    { return damage; }
    public double getX()       { return x; }
    public double getY()       { return y; }
}