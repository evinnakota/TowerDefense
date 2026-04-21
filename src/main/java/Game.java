public class Game {
    private GameViewer frontend;
    private boolean running;
    private Square[][] Grid;
    public static int GRID_HEIGHT = 15;
    public static int GRID_WIDTH = 25;


    public Game() {
        Grid = new Square[GRID_HEIGHT][GRID_WIDTH];

        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                Grid[i][j] = new Square();
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
