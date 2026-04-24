import java.awt.*;
import java.io.File;
import java.util.Scanner;

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
        if (image == Game.WALL) {
            g.setColor(Color.BLACK);
            g.drawRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.PATH) {
            g.setColor(Color.BLACK);
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.START_POS) {
            g.setColor(Color.GREEN);
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        } else if (image == Game.END_POS) {
            g.setColor(Color.RED);
            g.fillRect(x_cord, y_cord, SQUARE_WIDTH, SQUARE_HEIGHT);
        }
    }

    public void setImage(int image) {
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

