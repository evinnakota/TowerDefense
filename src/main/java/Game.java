import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Game {
    private GameViewer frontend;
    private boolean running;
    private Square[][] Grid;
    public static int GRID_HEIGHT = 33;
    public static int GRID_WIDTH = 59;
    public static final int WALL = 1;
    public static final int PATH = 2;
    public static final int START_POS = 3;
    public static final int END_POS = 4;
    public static final int TOWER = 5;


    public Game() {
        try {
            Grid = new Square[GRID_HEIGHT][GRID_WIDTH];
            File myObj = new File("Resources/maze1.txt");
            Scanner myReader = new Scanner(myObj);

            for (int row = 0; row < GRID_HEIGHT && myReader.hasNextLine(); row++) {
                String line = myReader.nextLine();

                for (int col = 0; col < GRID_WIDTH && col < line.length(); col++) {
                    Grid[row][col] = new Square();
                    char c = line.charAt(col);

                    if (c == '#') {
                        Grid[row][col].addImage(WALL);
                    } else if (c == ' ') {
                        Grid[row][col].addImage(PATH);
                    } else if (c == 'S') {
                        Grid[row][col].addImage(START_POS);
                    } else if (c == 'E') {
                        Grid[row][col].addImage(END_POS);
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        frontend = new GameViewer(this);
        startGame();
    }

    public void startGame() {
        frontend.repaint();
    }

    public Square[][] getGrid() {
        return Grid;
    }

    public void gameLoop() {

    }



    public static void main(String[] args) {
        Game g = new Game();
    }
}
