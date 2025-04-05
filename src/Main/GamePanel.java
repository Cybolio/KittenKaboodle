package Main;

import java.awt.*;
import javax.swing.JPanel;

import tiles.TileManager;
import util.*;

public class GamePanel extends JPanel implements Runnable {
    private final int tileSize = 72;
    private final int screenWidth = 960;
    private final int screenHeight = 600;
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    private int playerSpeed = 9;
    private Thread gameThread;
    public KeyHandler keyHandler;
    private PlayerSprite pSpriteManager;
    public PlayerMovement playerMovement;
    private CollisionHandler collisionHandler;

    private final SoundManager soundmgr = new SoundManager();
    private final TileManager tiles;

    // Game state
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;

    public DialogueManager dialogueManager;
    public ShopManager shopManager;
    public GameTimer gameTimer;
    public SpriteManager spriteManager;

    private MouseHandler mouseHandler;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.lightGray);
        setDoubleBuffered(true);
        setFocusable(true);
        keyHandler = new KeyHandler();
        keyHandler.setupKeyBindings(this);

        playerMovement = new PlayerMovement(this);
        tiles = new TileManager(this);

        collisionHandler = new CollisionHandler(this, tiles);
        pSpriteManager = new PlayerSprite(this);

        dialogueManager = new DialogueManager(this);
        shopManager = new ShopManager(this);
        gameTimer = new GameTimer(this);
        spriteManager = new SpriteManager(this);

        gameState = playState;
        startGameThread();
        gameTimer.startTimerThread();
        soundmgr.loopCycle();

        mouseHandler = new MouseHandler();
        mouseHandler.setupMouseBindings(this);
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

        for (StaticSprite sprite : spriteManager.staticSprites) {
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

        for (EntitySprite entitySprite : spriteManager.entity) {
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
        if (gameState == playState || gameState == dialogueState) {
            playerMovement.isPlayerBound();
            pSpriteManager.updateSprite(playerMovement.isMoving(), playerMovement.getDirection());

            for (EntitySprite entitySprite : spriteManager.entity) {
                entitySprite.updatePosition();
            }

            checkDialogueTrigger();
        } else if (gameState == pauseState) {
            gameTimer.pauseTimer();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tiles.draw(g2);

        for (EntitySprite entitySprite : spriteManager.entity) {
            entitySprite.draw(g2);
        }

        pSpriteManager.drawSprite(g2, playerMovement.screenX, playerMovement.screenY, tileSize);

        for (StaticSprite sprite : spriteManager.staticSprites) {
            sprite.draw(g2);
        }

        if (dialogueManager.showDialogueBox) {
            dialogueManager.drawDialogueBox(g2);
        }

        if (shopManager.showShopMenu) {
            shopManager.drawShopMenu(g2);
        }

        gameTimer.drawGameTimer(g2);

        g2.dispose();
    }

    public void checkDialogueTrigger() {
        int playerX = playerMovement.worldX;
        int playerY = playerMovement.worldY;

        if (shopManager.showShopMenu && mouseHandler.isLeftMousePressed()) {
            for (int i = 0; i < shopManager.shopButtons.size(); i++) {
                if (shopManager.shopButtons.get(i).contains(mouseHandler.getMouseX(), mouseHandler.getMouseY())) {
                    System.out.println("Button " + i + " Clicked!");
                    if (shopManager.shopItems.get(i).equals("Exit")) {
                        System.out.println("Exit Button Logic Executed!");
                        shopManager.showShopMenu = false;
                        dialogueManager.dialogueTriggered = false;
                        dialogueManager.currentInteractable = null;
                        playerMovement.setPlayerCanMove(true);
                        gameState = playState;
                        gameTimer.resumeTimer();
                    } else {
                        System.out.println("Item " + shopManager.shopItems.get(i) + " purchased!");
                    }
                    mouseHandler.leftMousePressed = false;
                    return;
                }
            }
            mouseHandler.leftMousePressed = false;
        }

        for (EntitySprite npc : spriteManager.entity) {
            if (npc != null && npc.hasDialogue()) {
                Rectangle npcBounds = npc.getCollisionBounds();
                int npcX = npcBounds.x;
                int npcY = npcBounds.y;
                int npcWidth = npcBounds.width;
                int npcHeight = npcBounds.height;

                int closestX = Math.max(npcX, Math.min(playerX, npcX + npcWidth));
                int closestY = Math.max(npcY, Math.min(playerY, npcY + npcHeight));

                double distance = Math.sqrt(Math.pow(playerX - closestX, 2) + Math.pow(playerY - closestY, 2));

                if (distance < dialogueManager.dialogueDistanceThreshold) {
                    if (keyHandler.isEPressed()) {
                        if (!dialogueManager.dialogueTriggered || dialogueManager.currentInteractable != npc) {
                            dialogueManager.dialogueTriggered = true;
                            dialogueManager.currentInteractable = npc;
                            dialogueManager.currentDialogue = npc.getDialogue();
                            dialogueManager.showDialogueBox = true;
                            playerMovement.setPlayerCanMove(false);
                            gameState = dialogueState;
                            gameTimer.pauseTimer();
                        } else if (dialogueManager.currentInteractable == npc && dialogueManager.dialogueTriggered) {
                            dialogueManager.dialogueTriggered = false;
                            dialogueManager.currentInteractable = null;
                            dialogueManager.showDialogueBox = false;
                            playerMovement.setPlayerCanMove(true);
                            gameState = playState;
                            gameTimer.resumeTimer();
                        }
                        keyHandler.setEPressed(false);
                    }
                }
            }
        }

        for (StaticSprite staticSprite : spriteManager.staticSprites) {
            if (staticSprite != null && staticSprite.hasDialogue()) {
                Rectangle spriteBounds = staticSprite.getCollisionBounds();
                int spriteX = spriteBounds.x;
                int spriteY = spriteBounds.y;
                int spriteWidth = spriteBounds.width;
                int spriteHeight = spriteBounds.height;

                int closestX = Math.max(spriteX, Math.min(playerX, spriteX + spriteWidth));
                int closestY = Math.max(spriteY, Math.min(playerY, spriteY + spriteHeight));

                double distance = Math.sqrt(Math.pow(playerX - closestX, 2) + Math.pow(playerY - closestY, 2));

                if (distance < dialogueManager.dialogueDistanceThreshold) {
                    boolean interactionTriggered = keyHandler.isEPressed() ||
                            (mouseHandler.isLeftMousePressed() && spriteBounds.contains(
                                    mouseHandler.getMouseX() + playerMovement.worldX - playerMovement.screenX,
                                    mouseHandler.getMouseY() + playerMovement.worldY - playerMovement.screenY));

                    if (interactionTriggered) {
                        if (!dialogueManager.dialogueTriggered || dialogueManager.currentInteractable != staticSprite) {
                            dialogueManager.dialogueTriggered = true;
                            dialogueManager.currentInteractable = staticSprite;
                            if (staticSprite.getDialogue().equals("Welcome to my shop!")) {
                                shopManager.showShopMenu = true;
                                playerMovement.setPlayerCanMove(false);
                                gameState = dialogueState;
                                gameTimer.pauseTimer();
                            } else {
                                dialogueManager.currentDialogue = staticSprite.getDialogue();
                                dialogueManager.showDialogueBox = true;
                                playerMovement.setPlayerCanMove(false);
                                gameState = dialogueState;
                                gameTimer.pauseTimer();
                            }
                        } else if (dialogueManager.currentInteractable == staticSprite && dialogueManager.dialogueTriggered) {
                            dialogueManager.dialogueTriggered = false;
                            dialogueManager.currentInteractable = null;
                            dialogueManager.showDialogueBox = false;
                            shopManager.showShopMenu = false;
                            playerMovement.setPlayerCanMove(true);
                            gameState = playState;
                            gameTimer.resumeTimer();
                        }
                        keyHandler.setEPressed(false);
                        if (mouseHandler.isLeftMousePressed()) {
                            mouseHandler.leftMousePressed = false;
                        }
                    }
                }
            }
        }

        if (dialogueManager.dialogueTriggered && dialogueManager.currentInteractable != null) {
            Rectangle bounds;
            if (dialogueManager.currentInteractable instanceof EntitySprite) {
                bounds = ((EntitySprite) dialogueManager.currentInteractable).getCollisionBounds();
            } else {
                bounds = ((StaticSprite) dialogueManager.currentInteractable).getCollisionBounds();
            }
            int interactableX = bounds.x;
            int interactableY = bounds.y;
            int interactableWidth = bounds.width;
            int interactableHeight = bounds.height;

            int closestX = Math.max(interactableX, Math.min(playerX, interactableX + interactableWidth));
            int closestY = Math.max(interactableY, Math.min(playerY, interactableY + interactableHeight));

            double distance = Math.sqrt(Math.pow(playerX - closestX, 2) + Math.pow(playerY - closestY, 2));

            if (distance > dialogueManager.dialogueDistanceThreshold) {
                dialogueManager.dialogueTriggered = false;
                dialogueManager.currentInteractable = null;
                dialogueManager.showDialogueBox = false;
                shopManager.showShopMenu = false;
                playerMovement.setPlayerCanMove(true);
                gameState = playState;
                gameTimer.resumeTimer();
            }
        }
    }
}