package util;

import Main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class PlayerMovement {

    private GamePanel gp;
    private boolean isMoving;
    private int direction;
    private int playerWidth;
    private int playerHeight;
    public int worldX;
    public int worldY;
    public int screenX;
    public int screenY;
    public int collisionOffsetX = 14;
    public int collisionOffsetY = 16;
    public int collisionWidth = 40;
    public int collisionHeight = 40;
    public BufferedImage[] sprites;
    public boolean canMove = true;

    private float scaleFactor;

    private boolean collision;
    private String name;


    private int imageIndex = 0;
    private int imageTimer = 0;
    private int imageSwitchInterval = 40; // Default animation speed

    private int velocityX = 0;
    private int velocityY = 0;
    private String movementType; // "horizontal" or "vertical"
    private int travelDistanceX; // Travel distance in X direction
    private int travelDistanceY; // Travel distance in Y direction
    private int travelCounterX = 0; // Current travel in X direction
    private int travelCounterY = 0; // Current travel in Y direction

    private String[] dialogues;
    private String dialogue;

    private final String LOG_FILE = "GameLogs/logs.txt";

    private boolean playerCanMove = true; // Added this line
    public int worldXOriginal;
    public int worldYOriginal;
    public PlayerMovement(GamePanel gp) {
        this.gp = gp;
        this.playerWidth = gp.getTileSize();
        this.playerHeight = gp.getTileSize();
        this.worldX = gp.getTileSize() * gp.maxWorldRow / 4 - gp.getScreenWidth() / 2;
        this.worldY = gp.getTileSize() * gp.maxWorldCol / 4 - gp.getScreenHeight() / 2;

        this.screenX = gp.getScreenWidth() / 2 - gp.getTileSize() / 2;
        this.screenY = gp.getScreenHeight() / 2 - gp.getTileSize() / 2;
        worldX += 2000;
        worldY -= 0;
        worldXOriginal=worldX+30;
        worldYOriginal=worldY;
        clearLogFile();
    }


    private int screenXOriginal;
    private int screenYOriginal;

    public Rectangle getPlayerCollisionBounds() {
        // Create a collision box around the player for combat detection
        int collisionBoxSize = gp.getTileSize() - 20; // Slightly smaller than player tile
        return new Rectangle(worldX + 10, worldY + 10, collisionBoxSize, collisionBoxSize);
    }

    public PlayerMovement() {
        worldX += 2000;
        worldY -= 0;
        clearLogFile();
    }

    private void clearLogFile() {
        String projectRoot = System.getProperty("user.dir");
        String logFilePath = projectRoot + File.separator + LOG_FILE;
        File logFile = new File(logFilePath);

        try {
            if (logFile.exists()) {
                new FileWriter(logFile, false).close();
            }
        } catch (IOException e) {
            System.err.println("Error clearing log file: " + e.getMessage());
        }
    }

    public BufferedImage getImage1() {
        if (sprites != null && sprites.length > 0) {
            return sprites[0];
        }
        return null;
    }

    public BufferedImage getCurrentImage() {
        if (sprites != null && sprites.length > imageIndex) {
            return sprites[imageIndex];
        }
        return null;
    }

    public boolean isPlayerBound() {
        if (!playerCanMove) {
            return false;
        }
        int speed = gp.getPlayerSpeed();
        int nextWorldX = worldX;
        int nextWorldY = worldY;

        boolean moved = false;

        if (gp.keyHandler.upPressed) {
            nextWorldY -= speed;
            direction = 1;
            moved = true;
        }

        if (gp.keyHandler.downPressed) {
            nextWorldY += speed;
            direction = 0;
            moved = true;
        }

        if (gp.keyHandler.leftPressed) {
            nextWorldX -= speed;
            direction = 2;
            moved = true;
        }

        if (gp.keyHandler.rightPressed) {
            nextWorldX += speed;
            direction = 3;
            moved = true;
        }

        isMoving = moved;

        Rectangle playerBounds = getCollisionBounds(nextWorldX, nextWorldY);

        boolean collisionDetected = false;
        EntitySprite collidingNPC = null;
        boolean isDetected=false;
        for (EntitySprite npc : gp.spriteManager.entity) {
            if (npc.hasCollision()) {
                Rectangle npcBounds = npc.getCollisionBounds();
                if (playerBounds.intersects(npcBounds)) {
                    logCollision(npc.getName());



                    //System.out.println("Collision Detected with " + npc.getName());
                    isDetected=true;
                    collisionDetected = true;
                    collidingNPC = npc;
                    break;
                }
            }
        }

        if (collisionDetected) {
            if (collidingNPC != null) {
                Rectangle npcBounds = collidingNPC.getCollisionBounds();

                int overlapX = Math.min(playerBounds.x + playerBounds.width, npcBounds.x + npcBounds.width) - Math.max(playerBounds.x, npcBounds.x);
                int overlapY = Math.min(playerBounds.y + playerBounds.height, npcBounds.y + npcBounds.height) - Math.max(playerBounds.y, npcBounds.y);

                int pushFactor = 2;

                overlapX *= pushFactor;
                overlapY *= pushFactor;

                if (collidingNPC.getVelocityX() != 0) {
                    if (playerBounds.y < npcBounds.y) {
                        worldY -= overlapY;
                    } else {
                        worldY += overlapY;
                    }
                } else if (collidingNPC.getVelocityY() != 0) {
                    if (playerBounds.x < npcBounds.x) {
                        worldX -= overlapX;
                    } else {
                        worldX += overlapX;
                    }
                }

                if (gp.keyHandler.interactPressed) {
                    gp.keyHandler.interactPressed = false;
                    showDialogue(collidingNPC.getName());
                }
            }
        } else if (!gp.getCollisionHandler().checkCollision(nextWorldX, nextWorldY, playerWidth, playerHeight, gp.getMapTileNum()) &&
                !gp.checkEntitySpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight) &&
                !gp.checkStaticSpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight)) {

            worldX = nextWorldX;
            worldY = nextWorldY;
        }

        int worldWidth = gp.getTileSize() * gp.maxWorldCol;
        int worldHeight = gp.getTileSize() * gp.maxWorldRow;

        if (worldX < 0) {
            worldX = 0;
        } else if (worldX > worldWidth - playerWidth) {
            worldX = worldWidth - playerWidth;
        }

        if (worldY < 0) {
            worldY = 0;
        } else if (worldY > worldHeight - playerHeight) {
            worldY = worldHeight - playerHeight;
        }

        screenX = gp.getScreenWidth() / 2 - gp.getTileSize() / 2;
        screenY = gp.getScreenHeight() / 2 - gp.getTileSize() / 2;

        if (worldX < gp.getScreenWidth() / 2 - gp.getTileSize() / 2) {
            screenX = worldX;
        }
        if (worldY < gp.getScreenHeight() / 2 - gp.getTileSize() / 2) {
            screenY = worldY;
        }

        if (worldX > worldWidth - gp.getScreenWidth() / 2 - gp.getTileSize() / 2) {
            screenX = gp.getScreenWidth() - (worldWidth - worldX);
        }
        if (worldY > worldHeight - gp.getScreenHeight() / 2 - gp.getTileSize() / 2) {
            screenY = gp.getScreenHeight() - (worldHeight - worldY);
        }
        return isDetected;
    }

    private void showDialogue(String npcName) {
        String dialogue = "You are talking to " + npcName + ". Hello!";
        //JOptionPane.showMessageDialog(gp, dialogue, "Dialogue", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getDirection() {
        return direction;
    }

    public Rectangle getCollisionBounds(int worldX, int worldY) {
        return new Rectangle(
                worldX + collisionOffsetX,
                worldY + collisionOffsetY,
                collisionWidth,
                collisionHeight
        );
    }

    private void logCollision(String npcName) {
        String projectRoot = System.getProperty("user.dir");
        String logFilePath = projectRoot + File.separator + LOG_FILE;
        File logFile = new File(logFilePath);

        try {
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            if (!isDuplicateLog(logFile, npcName)) {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = now.format(formatter);
                    out.println(formattedDateTime + " - Collision Detected with: " + npcName);

                } catch (IOException e) {
                    System.out.println("Error writing to log file: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error creating log file or directories: " + e.getMessage());
        }
    }

    private boolean isDuplicateLog(File logFile, String npcName) throws IOException {
        if (!logFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String lastLine = null;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                lastLine = currentLine;
            }

            if (lastLine != null && lastLine.contains(" - Collision Detected with: " + npcName)) {
                return true;
            }
        }
        return false;
    }

    public void resetPlayerPosition() {
        worldX = worldXOriginal;
        worldY = worldYOriginal;
    }

    public void setPlayerCanMove(boolean canMove) {
        this.playerCanMove = canMove;
    }
}

