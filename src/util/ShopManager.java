package util;

import Main.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class ShopManager {
    private GamePanel gp;
    public boolean showShopMenu = false;
    public Rectangle shopMenuRect;
    public ArrayList<Rectangle> shopButtons = new ArrayList<>();
    public ArrayList<String> shopItems = new ArrayList<>();

    public ShopManager(GamePanel gp) {
        this.gp = gp;
        setupShopMenu();
    }

    private void setupShopMenu() {
        shopMenuRect = new Rectangle(100, 100, gp.getScreenWidth() - 200, gp.getScreenHeight() - 200);
        shopItems.add("Healing Juice (10 gold)");
        shopItems.add("Catnip (50 gold)");
        shopItems.add("Smoke Bomb (80 gold)");
        shopItems.add("Exit");

        int buttonWidth = 500;
        int buttonHeight = 50;
        int buttonSpacing = 20;
        int startX = shopMenuRect.x + 50;
        int startY = shopMenuRect.y + 50;

        for (int i = 0; i < shopItems.size(); i++) {
            shopButtons.add(new Rectangle(startX, startY + i * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight));
        }
    }

    public void drawShopMenu(Graphics2D g2) {
        gp.logToGameLog("Shop Menu opened");
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fill(shopMenuRect);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        for (int i = 0; i < shopItems.size(); i++) {
            Rectangle button = shopButtons.get(i);
            g2.draw(button);
            g2.drawString(shopItems.get(i), button.x + 20, button.y + 30);
        }
    }
}