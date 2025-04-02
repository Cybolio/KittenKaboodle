package util;

import Main.GamePanel;

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

    public PlayerMovement(GamePanel gp) {
        this.gp = gp;
        this.playerWidth = gp.getTileSize();
        this.playerHeight = gp.getTileSize();
        this.worldX = gp.getTileSize() * gp.maxWorldRow / 4 - gp.getScreenWidth() / 2;
        this.worldY = gp.getTileSize() * gp.maxWorldCol / 4 -  gp.getScreenHeight() / 2;
        this.screenX = gp.getScreenWidth() / 2 - gp.getTileSize() / 2;
        this.screenY = gp.getScreenHeight() / 2 - gp.getTileSize() / 2;
        worldX += 3000;
        worldY -= 0;
    }

    public void updatePlayerPosition() {
        int speed = gp.getPlayerSpeed();

        isMoving = false;
        int nextWorldX = worldX;
        int nextWorldY = worldY;

        if (gp.keyHandler.upPressed) {
            nextWorldY -= speed;
            direction = 1;
            isMoving = true;
            // Check collision with tiles AND static sprites
            if (gp.getCollisionHandler().checkCollision(nextWorldX, nextWorldY, playerWidth, playerHeight, gp.getMapTileNum()) ||
                    gp.checkStaticSpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight)) {
                nextWorldY = worldY; // Collision detected, don't move
            } else {
                worldY = nextWorldY; // No collision, update position
            }
        }

        if (gp.keyHandler.downPressed) {
            nextWorldY += speed;
            direction = 0;
            isMoving = true;
            if (gp.getCollisionHandler().checkCollision(nextWorldX, nextWorldY, playerWidth, playerHeight, gp.getMapTileNum()) ||
                    gp.checkStaticSpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight)) {
                nextWorldY = worldY;
            } else {
                worldY = nextWorldY;
            }
        }

        if (gp.keyHandler.leftPressed) {
            nextWorldX -= speed;
            direction = 2;
            isMoving = true;
            if (gp.getCollisionHandler().checkCollision(nextWorldX, nextWorldY, playerWidth, playerHeight, gp.getMapTileNum()) ||
                    gp.checkStaticSpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight)) {
                nextWorldX = worldX;
            } else {
                worldX = nextWorldX;
            }
        }

        if (gp.keyHandler.rightPressed) {
            nextWorldX += speed;
            direction = 3;
            isMoving = true;
            if (gp.getCollisionHandler().checkCollision(nextWorldX, nextWorldY, playerWidth, playerHeight, gp.getMapTileNum()) ||
                    gp.checkStaticSpriteCollision(nextWorldX, nextWorldY, playerWidth, playerHeight)) {
                nextWorldX = worldX;
            } else {
                worldX = nextWorldX;
            }
        }

        // Corrected Camera Lock Logic - Fix for the right and bottom edge bug
        // Calculate world map boundaries
        int worldWidth = gp.getTileSize() * gp.maxWorldCol;
        int worldHeight = gp.getTileSize() * gp.maxWorldRow;

        // Fix left and top boundaries
        if (worldX < 0) {
            worldX = 0;
        }
        // Fix right boundary - allow player to reach the full right side of the map
        else if (worldX > worldWidth - playerWidth) {
            worldX = worldWidth - playerWidth;
        }

        if (worldY < 0) {
            worldY = 0;
        }
        // Fix bottom boundary - allow player to reach the full bottom side of the map
        else if (worldY > worldHeight - playerHeight) {
            worldY = worldHeight - playerHeight;
        }

        // Default centered position
        screenX = gp.getScreenWidth() / 2 - gp.getTileSize() / 2;
        screenY = gp.getScreenHeight() / 2 - gp.getTileSize() / 2;

        // Left edge
        if (worldX < gp.getScreenWidth() / 2 - gp.getTileSize() / 2) {
            screenX = worldX;
        }
        // Top edge
        if (worldY < gp.getScreenHeight() / 2 - gp.getTileSize() / 2) {
            screenY = worldY;
        }

        // Right edge - Fixed calculation
        if (worldX > worldWidth - gp.getScreenWidth() / 2 - gp.getTileSize() / 2) {
            screenX = gp.getScreenWidth() - (worldWidth - worldX);
        }
        // Bottom edge - Fixed calculation
        if (worldY > worldHeight - gp.getScreenHeight() / 2 - gp.getTileSize() / 2) {
            screenY = gp.getScreenHeight() - (worldHeight - worldY);
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getDirection() {
        return direction;
    }
}