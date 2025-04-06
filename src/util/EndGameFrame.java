package util;

import Main.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EndGameFrame extends JFrame {

    private GamePanel gp;
    private JTextField nameField;
    private JLabel scoreLabel, timeLabel, enemiesLabel, catsLabel, itemsLabel, titleLabel;
    private JButton recordButton, restartButton, mainMenuButton, quitButton;
    private long gameTime;

    public EndGameFrame(GamePanel gp, long gameTime) {
        this.gp = gp;
        this.gameTime = gameTime;
        setTitle("Game Over");
        setSize(gp.getScreenWidth(), gp.getScreenHeight());
        setLocationRelativeTo(gp);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contentPane = new JPanel(null);
        contentPane.setBackground(Color.BLACK);
        setContentPane(contentPane);

        titleLabel = new JLabel("Game Over!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(gp.getScreenWidth() / 2 - 100, 50, 200, 40);
        contentPane.add(titleLabel);

        JLabel nameLabel = new JLabel("Enter Your Name:");
        nameLabel.setBounds(gp.getScreenWidth() / 2 - 175, 120, 150, 25);
        nameLabel.setForeground(Color.WHITE);
        contentPane.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(gp.getScreenWidth() / 2 - 25, 120, 150, 25);
        contentPane.add(nameField);

        recordButton = new JButton("Record");
        recordButton.setFont(new Font("Arial", Font.BOLD, 14));
        recordButton.setBackground(Color.DARK_GRAY);
        recordButton.setForeground(Color.WHITE);
        recordButton.setBorder(null);
        recordButton.setBounds(gp.getScreenWidth() / 2 + 130, 120, 80, 25);
        contentPane.add(recordButton);

        scoreLabel = new JLabel("Score: " + gp.score);
        scoreLabel.setBounds(gp.getScreenWidth() / 2 - 175, 170, 300, 25);
        scoreLabel.setForeground(Color.WHITE);
        contentPane.add(scoreLabel);

        enemiesLabel = new JLabel("Enemies Beat: " + gp.enemiesBeat);
        enemiesLabel.setBounds(gp.getScreenWidth() / 2 - 175, 200, 300, 25);
        enemiesLabel.setForeground(Color.WHITE);
        contentPane.add(enemiesLabel);

        catsLabel = new JLabel("Cats Collected: " + gp.catsCollected);
        catsLabel.setBounds(gp.getScreenWidth() / 2 - 175, 230, 300, 25);
        catsLabel.setForeground(Color.WHITE);
        contentPane.add(catsLabel);

        itemsLabel = new JLabel("Items Bought: " + gp.itemsBought);
        itemsLabel.setBounds(gp.getScreenWidth() / 2 - 175, 260, 300, 25);
        itemsLabel.setForeground(Color.WHITE);
        contentPane.add(itemsLabel);

        timeLabel = new JLabel("Game Time: " + formatTime(gameTime));
        timeLabel.setBounds(gp.getScreenWidth() / 2 - 175, 290, 300, 25);
        timeLabel.setForeground(Color.WHITE);
        contentPane.add(timeLabel);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setBackground(Color.DARK_GRAY);
        restartButton.setForeground(Color.WHITE);
        restartButton.setBorder(null);
        restartButton.setBounds(gp.getScreenWidth() / 2 - 250, 350, 100, 30);
        contentPane.add(restartButton);

        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Arial", Font.BOLD, 16));
        mainMenuButton.setBackground(Color.DARK_GRAY);
        mainMenuButton.setForeground(Color.WHITE);
        mainMenuButton.setBorder(null);
        mainMenuButton.setBounds(gp.getScreenWidth() / 2 - 50, 350, 100, 30);
        contentPane.add(mainMenuButton);

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 16));
        quitButton.setBackground(Color.DARK_GRAY);
        quitButton.setForeground(Color.WHITE);
        quitButton.setBorder(null);
        quitButton.setBounds(gp.getScreenWidth() / 2 + 150, 350, 100, 30);
        contentPane.add(quitButton);

        recordButton.addActionListener(e -> recordName());

        restartButton.addActionListener(e -> {
            gp.playerMovement.setPlayerCanMove(true);
            recordAndReset();
        });

        mainMenuButton.addActionListener(e -> {
            dispose();
            gp.gameState = gp.titleState;
            gp.revalidate();
            gp.repaint();
        });

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void recordName() {
        String playerName = nameField.getText();
        if (playerName != null && !playerName.trim().isEmpty()) {
            gp.combatManager.recordLeaderboardEntry(playerName, gameTime);
            JOptionPane.showMessageDialog(this, "Name and score recorded.");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
        }
    }

    private void recordAndReset() {
        String playerName = nameField.getText();
        if (playerName != null && !playerName.trim().isEmpty()) {
            gp.combatManager.recordLeaderboardEntry(playerName, gameTime);
            gp.endGameTriggered = false;
            gp.gameState = gp.playState;
            gp.playerMovement.resetPlayerPosition();
            gp.gameTimer.resumeTimer();
            dispose();
            gp.playerMovement.worldX = gp.playerMovement.worldXOriginal;
            gp.playerMovement.worldY = gp.playerMovement.worldYOriginal;
            gp.revalidate();
            gp.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
        }
    }

    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void showEndGameFrame(GamePanel gp, long gameTime) {
        EndGameFrame frame = new EndGameFrame(gp, gameTime);
        frame.setVisible(true);
    }

    public static void showLeaderboard(GamePanel gp) {
        JFrame leaderboardFrame = new JFrame("Leaderboard");
        leaderboardFrame.setSize(400, 300);
        leaderboardFrame.setLocationRelativeTo(gp);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardFrame.setResizable(false);

        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));

        ArrayList<GameLoopManager.LeaderboardEntry> leaderboard = gp.combatManager.loadLeaderboard();
        if (leaderboard.isEmpty()) {
            leaderboardPanel.add(new JLabel("No leaderboard entries yet."));
        } else {
            leaderboardPanel.add(new JLabel("<html><b>Leaderboard</b></html>")); // Title
            for (GameLoopManager.LeaderboardEntry entry : leaderboard) {
                leaderboardPanel.add(new JLabel(entry.getPlayerName() + " - Score: " + entry.getScore() + " - Time: " + formatTime(entry.getGameTime())));
            }
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardPanel);
        leaderboardFrame.add(scrollPane);
        leaderboardFrame.setVisible(true);
    }
}