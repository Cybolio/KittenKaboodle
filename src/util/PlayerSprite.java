package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PlayerSprite implements CharacterSpriteManager {

    private BufferedImage[][] playerSprites = new BufferedImage[4][4]; // [direction][imageIndex]
    private int direction = 0; // 0: down, 1: up, 2: left, 3: right (Default to front)
    private int imageIndex = 0;
    private int imageTimer = 0;
    private final int imageSwitchInterval = 15;
    private final int idleSwitchInterval = 50; // Increased idle switch interval
    private int idleTimer = 0; // Separate timer for idle animation.

    private boolean isMoving;
    private int lastDirection = 0; // Store the last movement direction

    public PlayerSprite(int screenWidth, int screenHeight) {
        loadSprites();
    }

    @Override
    public void loadSprites() {
        try {
            // Front
            playerSprites[0][0] = ImageIO.read(getClass().getResource("/entity/player/front_stand1.png"));
            playerSprites[0][1] = ImageIO.read(getClass().getResource("/entity/player/front_stand2.png"));
            playerSprites[0][2] = ImageIO.read(getClass().getResource("/entity/player/front_walk1.png"));
            playerSprites[0][3] = ImageIO.read(getClass().getResource("/entity/player/front_walk2.png"));

            // Back
            playerSprites[1][0] = ImageIO.read(getClass().getResource("/entity/player/back_stand1.png"));
            playerSprites[1][1] = ImageIO.read(getClass().getResource("/entity/player/back_stand2.png"));
            playerSprites[1][2] = ImageIO.read(getClass().getResource("/entity/player/back_walk1.png"));
            playerSprites[1][3] = ImageIO.read(getClass().getResource("/entity/player/back_walk2.png"));

            // Left
            playerSprites[2][0] = ImageIO.read(getClass().getResource("/entity/player/left_stand1.png"));
            playerSprites[2][1] = ImageIO.read(getClass().getResource("/entity/player/left_stand2.png"));
            playerSprites[2][2] = ImageIO.read(getClass().getResource("/entity/player/left_walk1.png"));
            playerSprites[2][3] = ImageIO.read(getClass().getResource("/entity/player/left_walk2.png"));

            // Right
            playerSprites[3][0] = ImageIO.read(getClass().getResource("/entity/player/right_stand1.png"));
            playerSprites[3][1] = ImageIO.read(getClass().getResource("/entity/player/right_stand2.png"));
            playerSprites[3][2] = ImageIO.read(getClass().getResource("/entity/player/right_walk1.png"));
            playerSprites[3][3] = ImageIO.read(getClass().getResource("/entity/player/right_walk2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSprite(boolean isMoving, int direction) {
        this.isMoving = isMoving;

        if (isMoving) {
            this.direction = direction;
            this.lastDirection = direction; // Update last direction when moving
            imageTimer++;
            if (imageTimer >= imageSwitchInterval) {
                imageTimer = 0;
                imageIndex = (imageIndex + 1) % 2 + 2; // Walking
            }
        } else {
            this.direction = lastDirection; // Use last direction for idle
            idleTimer++;
            if (idleTimer >= idleSwitchInterval) {
                idleTimer = 0;
                imageIndex = (imageIndex + 1) % 2; // Idle
            }
        }
    }

    @Override
    public void drawSprite(Graphics2D g2, int x, int y, int tileSize) {
        // Increase the sprite size by a factor (e.g., 1.5)
        float scaleFactor = 0.8999f; // Adjust this value to change the size
        int scaledWidth = (int) (tileSize * scaleFactor);
        int scaledHeight = (int) (tileSize * scaleFactor);

        g2.drawImage(playerSprites[direction][imageIndex], x, y, scaledWidth, scaledHeight, null);
    }

    public int getDirection() {
        return direction;
    }

    public boolean getIsMoving() {
        return isMoving;
    }
}