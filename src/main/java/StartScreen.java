import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {

    public StartScreen(JFrame frame, Runnable onStart) {
        setBackground(new Color(20, 20, 40));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        JLabel title = new JLabel("Tower Defense");
        title.setFont(new Font("Arial", Font.BOLD, 64));
        title.setForeground(new Color(255, 215, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Defend your base at all costs!");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 22));
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rules panel
        JPanel rulesPanel = new JPanel();
        rulesPanel.setBackground(new Color(30, 30, 60));
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        rulesPanel.setMaximumSize(new Dimension(600, 400));
        rulesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rulesTitle = new JLabel("How to Play");
        rulesTitle.setFont(new Font("Arial", Font.BOLD, 28));
        rulesTitle.setForeground(new Color(255, 215, 0));
        rulesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] rules = {
                "🗺  Enemies follow the path from Start (green) to End (red)",
                "🏰  Place towers on walls to shoot enemies",
                "💰  Basic Tower costs $75  —  Sniper Tower costs $150",
                "❤  You start with 100 health — don't let enemies reach the end!",
                "💵  You start with $500 and earn more money after each wave",
                "📈  Enemies get tougher every wave — plan your defenses!",
                "🎯  Hover over a tower to see its attack range",
                "🏆  Survive as many waves as you can!"
        };

        rulesPanel.add(rulesTitle);
        rulesPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            ruleLabel.setForeground(Color.YELLOW);
            ruleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ruleLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            rulesPanel.add(ruleLabel);
        }

        JButton startBtn = new JButton("Start Game");
        startBtn.setFont(new Font("Arial", Font.BOLD, 24));
        startBtn.setBackground(new Color(0, 180, 0));
        startBtn.setForeground(Color.BLACK);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(220, 60));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> onStart.run());

        add(title);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(subtitle);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(rulesPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(startBtn);
        add(Box.createVerticalGlue());
    }
}