public class Game {
    private GameViewer frontend;
    private boolean running;


    public Game() {
        frontend = new GameViewer(this);
    }
    public void startGame() {

    }

    public void gameLoop() {

    }



    public static void main(String[] args) {
        Game g = new Game();
    }
}
