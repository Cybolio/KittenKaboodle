package Main;

import java.awt.*;
import javax.swing.JPanel;
import java.util.ArrayList;

import tiles.TileManager;
import util.*;

public class GamePanel extends JPanel implements Runnable {
    private final int tileSize = 72;
    private final int screenWidth = 1366;
    private final int screenHeight = 768;
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    private int playerSpeed = 20;
    private Thread gameThread;
    public KeyHandler keyHandler = new KeyHandler();
    private PlayerSprite pSpriteManager;
    public PlayerMovement playerMovement;
    private CollisionHandler collisionHandler;

    private final SoundManager soundmgr = new SoundManager();
    private final TileManager tiles;

    public ArrayList<StaticSprite> staticSprites = new ArrayList<>();
    public ArrayList<EntitySprite> entity = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.lightGray);
        setDoubleBuffered(true);
        setFocusable(true);
        keyHandler.setupKeyBindings(this);

        playerMovement = new PlayerMovement(this);
        tiles = new TileManager(this);

        collisionHandler = new CollisionHandler(this, tiles);
        pSpriteManager = new PlayerSprite(screenWidth, screenHeight);

        setupStaticSprites();
        startGameThread();
        soundmgr.loopCycle();
    }

    private void setupStaticSprites() {
        // Buildings
        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 10) + 150, (tileSize * 10) - 30,
                "/tiles/Buildings/House5.png",
                4.0f, true, 1
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 10) - 420, (tileSize * 10) - 30,
                "/tiles/Buildings/House2.png",
                4.0f, true, 1
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 10) + 150, (tileSize * 10) + 500,
                "/tiles/Buildings/House1.png",
                4.0f, true, 1
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 10) - 420, (tileSize * 10) + 500,
                "/tiles/Buildings/House4.png",
                4.0f, true, 1
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 10) - 360, (tileSize * 10) - 500,
                "/tiles/Buildings/Shop1.png",
                2f, true, 1, 10.0, 3.0, 0.0, 50.0
        ));

        // Trees
        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 40) - 100, (tileSize * 1) + 300,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 1, 3, 3.5, 0.0, 55.5
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 40) + 200, (tileSize * 10) + 110,
                "/tiles/Buildings/Tree.png",
                2.5f, true, 1, 3, 3.5, 0.0, 55.5
        ));

        // PALM TREES
        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 40) - 10, (tileSize * 40) + 395,
                "/tiles/Buildings/PalmTree.png",
                2.5f, true, 1, 2.4, 2.5, 0.0, 55.5
        ));

        staticSprites.add(new StaticSprite(
                this,
                (tileSize * 30) + 200, (tileSize * 40) + 395,
                "/tiles/Buildings/PalmTree.png",
                2.5f, true, 1, 2.4, 2.5, 0.0, 55.5
        ));

        // NPCs
        entity.add(new EntitySprite(
                this,
                (tileSize * 20) - 300, (tileSize * 20) - 430,
                "/entity/npc/Rizzly1.png", "/entity/npc/Rizzly2.png",
                1f, true, 2
        ));

        entity.add(new EntitySprite(
                this,
                (tileSize * 10) - 450, (tileSize * 10) - 400,
                "/entity/npc/Panda1.png", "/entity/npc/Panda2.png",
                1f, true, 2
        ));
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    public TileManager getTileManager() {
        return tiles;
    }

    public int[][] getMapTileNum() {
        return tiles.mapTileNum;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        long drawInterval = 1000000000 / 60;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                long remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep(remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkStaticSpriteCollision(int nextWorldX, int nextWorldY, int width, int height) {
        Rectangle playerRect = new Rectangle(nextWorldX, nextWorldY, width, height);

        for (StaticSprite sprite : staticSprites) {
            if (sprite.hasCollision()) {
                Rectangle spriteRect = sprite.getCollisionBounds();
                if (playerRect.intersects(spriteRect)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkEntitySpriteCollision(int nextWorldX, int nextWorldY, int width, int height) {
        Rectangle playerRect = new Rectangle(nextWorldX, nextWorldY, width, height);

        for (EntitySprite entitySprite : entity) {
            if (entitySprite.hasCollision()) {
                Rectangle entityRect = entitySprite.getCollisionBounds();
                if (playerRect.intersects(entityRect)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void update() {
        playerMovement.updatePlayerPosition();
        pSpriteManager.updateSprite(playerMovement.isMoving(), playerMovement.getDirection());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tiles.draw(g2);

        int screenX = playerMovement.screenX;
        int screenY = playerMovement.screenY;
        pSpriteManager.drawSprite(g2, screenX, screenY, tileSize);

        for (StaticSprite sprite : staticSprites) {
            sprite.draw(g2);
        }
        for (EntitySprite entitySprite : entity) {
            entitySprite.draw(g2);
        }

        g2.dispose();
    }
}