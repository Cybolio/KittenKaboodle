package util;

import Main.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class DialogueManager {
    private GamePanel gp;
    public boolean dialogueTriggered = false;
    public Object currentInteractable = null;
    public String currentDialogue = "";
    public boolean showDialogueBox = false;
    public int dialogueDistanceThreshold = 100;

    public DialogueManager(GamePanel gp) {
        this.gp = gp;
    }



    public void drawDialogueBox(Graphics2D g2) {
        int boxX = 50;
        int boxY = gp.getScreenHeight() - 150;
        int boxWidth = gp.getScreenWidth() - 100;
        int boxHeight = 100;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.drawString(currentDialogue, boxX + 20, boxY + 50);

    }


    private void drawCenteredString(Graphics2D g2, String text, Rectangle rect) {
        FontMetrics metrics = g2.getFontMetrics();
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(text, x, y);
    }


}