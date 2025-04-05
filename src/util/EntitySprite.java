package util;

import Main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EntitySprite {
    private BufferedImage[] sprites;
    public int worldX;
    public int worldY;
    private float scaleFactor;
    private GamePanel gp;
    private boolean collision;
    private String name;

    private double collisionWidth = 40;
    private double collisionOffsetX = 0;
    private double collisionHeight = 7.0;
    private double collisionOffsetY = 20.0;

    private int imageIndex = 0;
    private int imageTimer = 0;
    private int imageSwitchInterval = 40; // Default animation speed

    private int velocityX = 0;
    private int velocityY = 0;
    private String direction;
    private boolean isMoving = false;
    private String movementType; // "horizontal" or "vertical"
    private int travelDistanceX; // Travel distance in X direction
    private int travelDistanceY; // Travel distance in Y direction
    private int travelCounterX = 0; // Current travel in X direction
    private int travelCounterY = 0; // Current travel in Y direction

    private String[] dialogues;
    private String dialogue;

    public EntitySprite(GamePanel gp){

    }
    // Constructor for moving entity with direction and travel
    public EntitySprite(GamePanel gp, String name, int worldX, int worldY, String imagePath1, String imagePath2,
                        String imagePath3, String imagePath4, float scaleFactor, boolean collision, String movementType, int travelDistanceX, int travelDistanceY) {
        this.gp = gp;
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.sprites = new BufferedImage[4];
        loadSprites(imagePath1, imagePath2, imagePath3, imagePath4);
        this.direction = "down";
        this.movementType = movementType;
        this.travelDistanceX = travelDistanceX;
        this.travelDistanceY = travelDistanceY;
        startMovement(); // Start the movement immediately
    }

    // Constructor for Idle animation (2 images)
    public EntitySprite(GamePanel gp, String name, int worldX, int worldY, String imagePath1, String imagePath2, float scaleFactor,
                        boolean collision, double v1, double v2, double v3, double v4, String dialogue) {
        this.gp = gp;
        this.name = name;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.sprites = new BufferedImage[2];
        loadSprites(imagePath1, imagePath2);
        this.direction = "idle";
        collisionWidth = v1;
        collisionHeight = v2;
        collisionOffsetX = v3;
        collisionOffsetY = v4;
        this.dialogue = dialogue;
    }
    public EntitySprite(GamePanel gp, int worldX, int worldY, String imagePath, float scaleFactor,
                        boolean collision, String dialogue, String name) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scaleFactor = scaleFactor;
        this.collision = collision;
        this.sprites = new BufferedImage[2];
        this.dialogue = dialogue;
        this.name = name;
        loadSprites(imagePath,imagePath);
    }

    // Constructor for movement animation and custom collision
    public EntitySprite(GamePanel gamePanel, String name, int i, int i1, String s, String s1, String s2, String s3, float v, boolean b, int i2, double v1, double v2, double v3, double v4) {
        collisionWidth = v1;
        collisionHeight = v2;
        collisionOffsetX = v3;
        collisionOffsetY = v4;
        this.name = name;
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

            if (sprites.length == 2 && !isMoving) {
                imageTimer++;
                if (imageTimer >= imageSwitchInterval) {
                    imageTimer = 0;
                    imageIndex = (imageIndex + 1) % 2;
                }
                g2.drawImage(sprites[imageIndex], screenX, screenY, scaledWidth, scaledHeight, null);
            } else {
                g2.drawImage(sprites[imageIndex], screenX, screenY, scaledWidth, scaledHeight, null);
                if (isMoving) {
                    imageTimer++;
                    if (imageTimer > imageSwitchInterval) {
                        imageTimer = 0;
                        imageIndex = (imageIndex + 1) % 4;
                    }
                }
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

    public void updatePosition() {
        if (isMoving) {
            // Increment the timer for animation
            imageTimer++;

            // Switch sprite images based on direction and movement type
            if (imageTimer >= imageSwitchInterval) {
                imageTimer = 0; // Reset timer

                if (movementType.equals("horizontal")) {
                    if (direction.equals("left")) {
                        imageIndex = (imageIndex == 2) ? 3 : 2; // Left walking sprites
                    } else if (direction.equals("right")) {
                        imageIndex = (imageIndex == 0) ? 1 : 0; // Right walking sprites
                    }
                } else if (movementType.equals("vertical")) {
                    if (direction.equals("down")) {
                        imageIndex = (imageIndex == 0) ? 1 : 0; // Front walking sprites
                    } else if (direction.equals("up")) {
                        imageIndex = (imageIndex == 2) ? 3 : 2; // Back walking sprites
                    }
                }
            }

            // Handle movement logic based on type
            if (movementType.equals("horizontal")) {
                worldX += velocityX;
                travelCounterX += Math.abs(velocityX);
                if (travelCounterX >= travelDistanceX) {
                    velocityX *= -1; // Reverse direction
                    travelCounterX = 0;
                    updateDirectionHorizontal(); // Update direction ("left" or "right")
                }
            } else if (movementType.equals("vertical")) {
                worldY += velocityY;
                travelCounterY += Math.abs(velocityY);
                if (travelCounterY >= travelDistanceY) {
                    velocityY *= -1; // Reverse direction
                    travelCounterY = 0;
                    updateDirectionVertical(); // Update direction ("up" or "down")
                }
            }
        }
    }

    private void adjustImageSwitchInterval() {
        // Example dynamic scaling based on velocity
        int effectiveSpeed = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        imageSwitchInterval = Math.max(20, 20 / effectiveSpeed); // Adjust interval dynamically
    }

    public String getName() {
        return name;
    }
    public void startMovement() {
        isMoving = true;
        if (movementType.equals("horizontal")) {
            velocityX = 3;
            updateDirectionHorizontal();
        } else if (movementType.equals("vertical")) {
            velocityY = 3;
            updateDirectionVertical();
        }
        adjustImageSwitchInterval(); // Adjust interval when movement starts
    }

    private void updateDirectionHorizontal() {
        direction = (velocityX > 0) ? "right" : "left";
    }

    private void updateDirectionVertical() {
        direction = (velocityY > 0) ? "down" : "up";
    }

    // getters for velocity
    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public String getDialogue() {
        return dialogue;
    }

    public boolean hasDialogue() {
        return dialogue != null && !dialogue.isEmpty();
    }
}