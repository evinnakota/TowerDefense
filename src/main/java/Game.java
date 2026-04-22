public class Game {
    private GameViewer frontend;
    private boolean running;
    private Square[][] Grid;
    public static int GRID_HEIGHT = 43;
    public static int GRID_WIDTH = 75;
    public static final int START_POS = 1;
    public static final int END_POS = 2;


    public Game() {
        Grid = new Square[GRID_HEIGHT][GRID_WIDTH];

        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                Grid[i][j] = new Square();
                if (i == 13 && j == 0) {
                    Grid[i][j].setImage(START_POS);
                }
                if (i == 26 && j == 45) {
                    Grid[i][j].setImage(END_POS);
                }
            }
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
