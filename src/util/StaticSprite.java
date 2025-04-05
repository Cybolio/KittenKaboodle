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

    private double collisionWidth = 10.9;
    private double collisionOffsetX = 0.0;

    private double collisionHeight = 5.0;
    private double collisionOffsetY = 40.0;

    private int imageIndex = 0;
    private int imageTimer = 0;
    private final int imageSwitchInterval = 50; // Adjust for idle animation speed
    private String dialogue;

    private String name;

    // Constructor for single image
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        loadSprite(imagePath);
    }

    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision, double sizeWidth, double sizeHeight, double offsetX, double offsetY) {
        collisionWidth = sizeWidth;
        collisionHeight = sizeHeight;
        collisionOffsetX = offsetX;
        collisionOffsetY = offsetY;
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        loadSprite(imagePath);
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
    }

    // Constructor with dialogue
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision, String dialogue) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        loadSprite(imagePath);
        this.dialogue = dialogue;
    }

    // Constructor with dialogue and custom collision
    public StaticSprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor, boolean collision, double sizeWidth, double sizeHeight, double offsetX, double offsetY, String dialogue) {
        collisionWidth = sizeWidth;
        collisionHeight = sizeHeight;
        collisionOffsetX = offsetX;
        collisionOffsetY = offsetY;
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        loadSprite(imagePath);
        this.dialogue = dialogue;
    }

    public StaticSprite(GamePanel gp,int worldX, int worldY, String img, float scaleFactor,
            Boolean collision, double sizeWidth, double sizeHeight, double offsetX, double offsetY, String name, String dialogue){

        collisionWidth = sizeWidth;
        collisionHeight = sizeHeight;
        collisionOffsetX = offsetX;
        collisionOffsetY = offsetY;
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        loadSprite(img);
        this.dialogue = dialogue;

    }

    public String getDialogue() {
        return dialogue;
    }

    public boolean hasDialogue() {
        return dialogue != null && !dialogue.isEmpty();
    }

    public boolean hasCollision() {
        return collision;
    }

    public String getName() {
        return name;
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
            if (collision && isHitBoxVisible) {
                Rectangle collisionBox = getCollisionBounds();
                int collisionScreenX = collisionBox.x - gp.playerMovement.worldX + gp.playerMovement.screenX;
                int collisionScreenY = collisionBox.y - gp.playerMovement.worldY + gp.playerMovement.screenY;
                g2.setColor(Color.RED);
                g2.drawRect(collisionScreenX, collisionScreenY, collisionBox.width, collisionBox.height);
            }
        }
    }
}