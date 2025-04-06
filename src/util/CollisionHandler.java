package util;

import Main.GamePanel;
import tiles.Tile;
import tiles.TileManager;

import java.awt.*;
import java.util.ArrayList;

public class CollisionHandler {

    private GamePanel gp;
    private TileManager tileManager;

    public CollisionHandler(GamePanel gp, TileManager tileManager) {
        this.gp = gp;
        this.tileManager = tileManager;
    }

    public boolean checkCollision(double x, double y, int entityWidth, int entityHeight, int[][] map) {
        int entityLeftWorldX = (int) x;
        int entityRightWorldX = (int) (x + entityWidth);
        int entityTopWorldY = (int) y;
        int entityBottomWorldY = (int) (y + entityHeight);

        int entityLeftCol = entityLeftWorldX / gp.getTileSize();
        int entityRightCol = entityRightWorldX / gp.getTileSize();
        int entityTopRow = entityTopWorldY / gp.getTileSize();
        int entityBottomRow = entityBottomWorldY / gp.getTileSize();

        for (int row = entityTopRow; row <= entityBottomRow; row++) {
            for (int col = entityLeftCol; col <= entityRightCol; col++) {
                try {
                    int tileNum = map[col][row];
                    Tile[] tiles = tileManager.getTile();

                    if (tiles != null && tiles.length > tileNum && tiles[tileNum] != null && tiles[tileNum].collision) {
                        int tileLeft = col * gp.getTileSize();
                        int tileRight = tileLeft + gp.getTileSize();
                        int tileTop = row * gp.getTileSize();
                        int tileBottom = tileTop + gp.getTileSize();

                        int collisionPadding = gp.getTileSize() / 4;
                        tileLeft += collisionPadding;
                        tileRight -= collisionPadding;
                        tileTop += collisionPadding;
                        tileBottom -= collisionPadding;

                        if (entityRightWorldX > tileLeft && entityLeftWorldX < tileRight &&
                                entityBottomWorldY > tileTop && entityTopWorldY < tileBottom) {
                            return true;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkEntityCollision(Rectangle playerBounds) {
        for (EntitySprite entitySprite : gp.spriteManager.entity) {
            if (entitySprite.hasCollision()) {
                Rectangle entityBounds = entitySprite.getCollisionBounds();
                if (playerBounds.intersects(entityBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkStaticSpriteCollision(Rectangle playerBounds) {
        for (StaticSprite sprite : gp.spriteManager.staticSprites) {
            if (sprite.hasCollision()) {
                Rectangle spriteBounds = sprite.getCollisionBounds();
                if (playerBounds.intersects(spriteBounds)) {
                    return true;
                }
            }
        }
        return false;
    }


}