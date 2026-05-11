import javax.swing.*;
import java.awt.*;

public class Square {
    private int x_cord;
    private int y_cord;
    private int image;
    public static int SQUARE_WIDTH = 25;
    public static int SQUARE_HEIGHT = 25;
    public static int bar = 0;
    private static int count = 0;
    private boolean hasTower;
    private boolean isWall;
    private boolean isStart;
    private boolean isEnd;
    private static Image basicTower;
    private static Image sniperTower;
    public static final int BASIC_TOWER_RANGE = 150;
    public static final int SNIPER_TOWER_RANGE = 300;
    public static final int CROSSBOW_TOWER_RANGE = 200;
    private static Image crossbowTower;

    static {
        try {
            basicTower = new ImageIcon(Square.class.getResource("/tower-defense.png")).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load basic tower image: " + e.getMessage());
        }
        try {
            sniperTower = new ImageIcon(Square.class.getResource("/sniper_Tower.png")).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load sniper tower image: " + e.getMessage());
        }
        try {
            crossbowTower = new ImageIcon(Square.class.getResource("/crossbowTower.png")).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load crossbow tower image: " + e.getMessage());
        }
    }

    public Square() {
        int col = count % Game.GRID_WIDTH;
        int row = count / Game.GRID_WIDTH;
        x_cord = col * SQUARE_WIDTH;
        y_cord = row * SQUARE_HEIGHT + bar;
        count++;
        image = 0;
        hasTower = false;
    }
    public boolean isWall() {
        return isWall;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isEnd() {
        return isEnd;
    }
    public void draw(Graphics g) {
        if (image == Game.PATH) {
            g.setColor(new Color(0x7f807C));
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.START_POS) {
            g.setColor(Color.GREEN);
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.END_POS) {
            g.setColor(Color.RED);
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.BASIC_TOWER) {
            if (basicTower != null) {
                g.drawImage(basicTower, x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT, null);
            } else {
                g.setColor(new Color(100, 100, 255));
                g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        } else if (image == Game.SNIPER) {
            if (sniperTower != null) {
                g.drawImage(sniperTower, x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT, null);
            } else if (image == Game.CROSSBOW) {
                if (crossbowTower != null) {
                    g.drawImage(crossbowTower, x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT, null);
                } else {
                    g.setColor(new Color(180, 100, 20));
                    g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
                }
            } else {
                g.setColor(new Color(255, 100, 100));
                g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
    }

    public void drawRange(Graphics g, int mouseX, int mouseY) {
        if (image != Game.BASIC_TOWER && image != Game.SNIPER && image != Game.CROSSBOW) return;

        if (mouseX < x_cord || mouseX > x_cord + SQUARE_WIDTH ||
                mouseY < y_cord || mouseY > y_cord + SQUARE_HEIGHT) return;

        int range = (image == Game.SNIPER)   ? SNIPER_TOWER_RANGE
                : (image == Game.CROSSBOW) ? CROSSBOW_TOWER_RANGE
                :                            BASIC_TOWER_RANGE;

        int cx = x_cord + SQUARE_WIDTH / 2;
        int cy = y_cord + SQUARE_HEIGHT / 2;

        Graphics2D g2d = (Graphics2D) g;
        Color ringColor = (image == Game.CROSSBOW) ? new Color(180, 100, 20, 50)
                : new Color(255, 255, 0, 50);
        Color ringBorder = (image == Game.CROSSBOW) ? new Color(180, 100, 20, 180)
                : new Color(255, 255, 0, 180);
        g2d.setColor(ringColor);
        g2d.fillOval(cx - range, cy - range, range * 2, range * 2);
        g2d.setColor(ringBorder);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(cx - range, cy - range, range * 2, range * 2);
    }

    public void addImage(int image) {
        this.image = image;

        isWall = false;
        isStart = false;
        isEnd = false;

        if (image == Game.WALL) {
            isWall = true;
        }
        else if (image == Game.START_POS) {
            isStart = true;
        }
        else if (image == Game.END_POS) {
            isEnd = true;
        }
    }

    // Add this static method to Square.java
    public static void resetCount() {
        count = 0;
    }

    public int getImage() { return image; }

    public int getY_cord() {
        return y_cord;
    }

    public int getX_cord() {
        return x_cord;
    }

    public void setX_cord(int x_cord) {
        this.x_cord = x_cord;
    }

    public void setY_cord(int y_cord) {
        this.y_cord = y_cord;
    }
}

