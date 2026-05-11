import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JPanel {

    public GameOverScreen(JFrame frame, int wave, int score, Runnable onRestart) {
        setBackground(new Color(20, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 72));
        gameOverLabel.setForeground(new Color(200, 0, 0));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel waveLabel = new JLabel("You survived " + (wave - 1) + " wave(s)");
        waveLabel.setFont(new Font("Arial", Font.BOLD, 28));
        waveLabel.setForeground(Color.LIGHT_GRAY);
        waveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Final Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(255, 215, 0));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton restartBtn = new JButton("Play Again");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 22));
        restartBtn.setBackground(new Color(0, 120, 200));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartBtn.setMaximumSize(new Dimension(200, 55));
        restartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartBtn.setFocusPainted(false);
        restartBtn.addActionListener(e -> onRestart.run());

        add(gameOverLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(waveLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(scoreLabel);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(restartBtn);
        add(Box.createVerticalGlue());
    }
}