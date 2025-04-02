package util;

import Main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EntitySprite {
    private BufferedImage[] sprites;
    private int worldX;
    private int worldY;
    private float scaleFactor;
    private GamePanel gp;
    private boolean collision;

    private double collisionWidth = 8;
    private double collisionOffsetX = 0.0;
    private double collisionHeight = 5.0;
    private double collisionOffsetY = 20.0;

    private int imageIndex = 0;
    private int imageTimer = 0;
    private final int imageSwitchInterval = 50;

    private int velocityX = 0;
    private int velocityY = 0;
    private String direction;

    // Constructor for Idle animation (2 images)
    public EntitySprite(GamePanel gp, int worldX, int worldY, String imagePath1, String imagePath2, float scaleFactor, boolean collision, int layer) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.sprites = new BufferedImage[2];
        loadSprites(imagePath1, imagePath2);
        this.direction = "idle";
    }

    // Constructor for movement animation (4 images)
    public EntitySprite(GamePanel gp, int worldX, int worldY, String imagePathUp, String imagePathDown, String imagePathLeft, String imagePathRight, float scaleFactor, boolean collision, int layer) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.sprites = new BufferedImage[4];
        loadSprites(imagePathUp, imagePathDown, imagePathLeft, imagePathRight);
        this.direction = "down";
    }

    // Constructor for movement animation and custom collision
    public EntitySprite(GamePanel gamePanel, int i, int i1, String s, String s1, String s2, String s3, float v, boolean b, int i2, double v1, double v2, double v3, double v4) {
        collisionWidth = v1;
        collisionHeight = v2;
        collisionOffsetX = v3;
        collisionOffsetY = v4;
        this.gp = gamePanel;
        this.worldX = i;
        this.worldY = i1;
        this.scaleFactor = v;
        this.collision = b;
        this.sprites = new BufferedImage[4];
        loadSprites(s, s1, s2, s3);
        this.direction = "down";
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

    private void loadSprites(String... imagePaths) {
        for (int i = 0; i < imagePaths.length; i++) {
            try {
                sprites[i] = ImageIO.read(getClass().getResource(imagePaths[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void updatePosition() {
        worldX += velocityX;
        worldY += velocityY;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public void setDirection(String direction) {
        this.direction = direction;
        updateImageIndex();
    }

    private void updateImageIndex() {
        if (sprites.length == 4) {
            switch (direction) {
                case "up":
                    imageIndex = 0;
                    break;
                case "down":
                    imageIndex = 1;
                    break;
                case "left":
                    imageIndex = 2;
                    break;
                case "right":
                    imageIndex = 3;
                    break;
                default:
                    imageIndex = 1; // Default to down if direction is unknown
                    break;
            }
        }
    }
}