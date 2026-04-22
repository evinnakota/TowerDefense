import java.awt.*;

public class Square {
    private int x_cord;
    private int y_cord;
    private int image;
    public static int SQUARE_WIDTH = 25;
    public static int SQUARE_HEIGHT = 25;
    public static int bar = 30;
    private static int count = 0;

    public Square() {
        int col = count % Game.GRID_WIDTH;
        int row = count / Game.GRID_WIDTH;
        x_cord = col * SQUARE_WIDTH;
        y_cord = row * SQUARE_HEIGHT + bar;
        count++;

    }

    public void draw(Graphics g) {
        g.drawRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
    }

    public void addImage(int image) {
        this.image = image;
    }

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

