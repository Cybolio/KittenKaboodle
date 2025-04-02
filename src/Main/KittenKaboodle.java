package Main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class KittenKaboodle {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kitten Kaboodle");
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.setResizable(true);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}