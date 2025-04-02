package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface CharacterSpriteManager {
    void loadSprites();
    void updateSprite(boolean isMoving, int direction);
    void drawSprite(Graphics2D g2,int x, int y, int tileSize);
}