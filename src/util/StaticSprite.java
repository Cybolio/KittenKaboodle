package util;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StaticSprite {
    private BufferedImage[] sprites = new BufferedImage[2]; // Array to hold two idle images
    private int worldX;
    private int worldY;
    private float scaleFactor;
    private GamePanel gp;
    private boolean collision;
    private int layer;

    private double collisionWidth = 10.9;
    private double collisionOffsetX = 0.0;

    private double collisionHeight = 5.0;
    private double collisionOffsetY = 40.0;

    private int imageIndex = 0;
    private int imageTimer = 0;
    private final int imageSwitchInterval = 50; // Adjust for idle animation speed

    // Constructor for single image
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision, int layer) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.layer = layer;
        loadSprite(imagePath);
    }
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision, int layer, double sizeWidth, double sizeHeight, double offsetX, double offsetY) {
        collisionWidth = sizeWidth;
        collisionHeight = sizeHeight;
        collisionOffsetX = offsetX;
        collisionOffsetY = offsetY;
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.layer = layer;
        loadSprite(imagePath);
    }

    // Overloaded constructor for animated sprites (idle animation)
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath1, String imagePath2, float scaleFactor, boolean collision, int layer) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.layer = layer;
        loadSprites(imagePath1, imagePath2);
    }

    public StaticSprite(GamePanel gamePanel, int i, int i1, String s, String s1, float v, boolean b, int i2, double v1, double v2, double v3, double v4) {
        collisionWidth = v1;
        collisionHeight = v2;
        collisionOffsetX = v3;
        collisionOffsetY = v4;
        this.gp = gamePanel;
        this.worldX = i;
        this.worldY = i1;
        this.scaleFactor = v;
        this.collision = b;
        this.layer = i2;
        loadSprites(s, s1);
    }



    public boolean hasCollision() {
        return collision;
    }



    public Rectangle getCollisionBounds() {
        int scaledWidth = (int) (gp.getTileSize() * scaleFactor);
        int scaledHeight = (int) (gp.getTileSize() * scaleFactor);

        double collisionPaddingWidth = (gp.getTileSize() / collisionWidth * scaleFactor);
        double collisionPaddingHeight = (gp.getTileSize() / collisionHeight * scaleFactor);

        return new Rectangle(
                worldX + (int) collisionPaddingWidth + (int) collisionOffsetX,
                worldY + (int) collisionPaddingHeight + (int) collisionOffsetY,
                scaledWidth - (int) (collisionPaddingWidth * 2),
                scaledHeight - (int) (collisionPaddingHeight * 2)
        );
    }

    public void loadSprite(String imagePath) {
        try {
            sprites[0] = ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSprites(String imagePath1, String imagePath2) {
        try {
            sprites[0] = ImageIO.read(getClass().getResource(imagePath1));
            sprites[1] = ImageIO.read(getClass().getResource(imagePath2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.playerMovement.worldX + gp.playerMovement.screenX;
        int screenY = worldY - gp.playerMovement.worldY + gp.playerMovement.screenY;

        int scaledWidth = (int) (gp.getTileSize() * scaleFactor);
        int scaledHeight = (int) (gp.getTileSize() * scaleFactor);

        if (screenX > -scaledWidth && screenX < gp.getScreenWidth() &&
                screenY > -scaledHeight && screenY < gp.getScreenHeight()) {

            if (sprites[1] != null) { // If there are two images, animate
                imageTimer++;
                if (imageTimer >= imageSwitchInterval) {
                    imageTimer = 0;
                    imageIndex = (imageIndex + 1) % 2;
                }
                g2.drawImage(sprites[imageIndex], screenX, screenY, scaledWidth, scaledHeight, null);
            } else { // If only one image, just draw it
                g2.drawImage(sprites[0], screenX, screenY, scaledWidth, scaledHeight, null);
            }
            Boolean isHitBoxVisible = true;
            if (collision&&isHitBoxVisible) {
                Rectangle collisionBox = getCollisionBounds();
                int collisionScreenX = collisionBox.x - gp.playerMovement.worldX + gp.playerMovement.screenX;
                int collisionScreenY = collisionBox.y - gp.playerMovement.worldY + gp.playerMovement.screenY;
                g2.setColor(Color.RED);
                g2.drawRect(collisionScreenX, collisionScreenY, collisionBox.width, collisionBox.height);
            }
        }
    }
}