package Main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import tiles.TileManager;
import util.*;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    private final int tileSize = 72;
    private final int screenWidth = 1280;
    private final int screenHeight = 800;
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    private int playerSpeed = 8;
    private Thread gameThread;
    public KeyHandler keyHandler;
    private PlayerSprite pSpriteManager;
    public PlayerMovement playerMovement;
    private CollisionHandler collisionHandler;

    // LEADERBOARD VARIABLES
    public int score = 100; // Starting score
    public int enemiesBeat = 0;
    public int catsCollected = 0;
    public int itemsBought = 0;
    public boolean endGameTriggered = false;
    public long startTime;

    private final SoundManager soundmgr = new SoundManager();
    private final TileManager tiles;

    // Game state
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int combatState = 4;
    public final int gameOverState = 5; // New game over state
    public final int mapChangeDialogueState = 6;

    public DialogueManager dialogueManager;
    public ShopManager shopManager;
    public GameTimer gameTimer;
    public SpriteManager spriteManager;
    public GameLoopManager combatManager;

    private MouseHandler mouseHandler;
    private final String LOG_FILE = "GameLogs/logs.txt";
    public int healingJuice = 1;
    // Main menu variables
    private BufferedImage mainMenuBackground;
    private Rectangle startButton;
    private Rectangle exitButton;
    private Rectangle leaderboardButton; // Add leaderboard button rectangle

    //map change variables
    private String nextMapPath;
    private boolean mapChangeConfirmed = false;
    private StaticSprite mapChangeTriggerSprite;
    private String currentMapPath = "/maps/world01";

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
        combatManager = new GameLoopManager(this);

        gameState = titleState; // Start in the title state
        startGameThread();
        gameTimer.startTimerThread();
        soundmgr.loopCycle();

        mouseHandler = new MouseHandler();
        mouseHandler.setupMouseBindings(this);
        initMouseListener();
        initMainMenu();
    }

    private void initMainMenu() {
        try {
            mainMenuBackground = ImageIO.read(getClass().getResourceAsStream("/tiles/background/KittenKaboodle.png")); // Replace with your image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        int buttonWidth = 200;
        int buttonHeight = 50;
        int startX = screenWidth / 2 - buttonWidth / 2;
        int startY = screenHeight / 2 - buttonHeight - 75; // Adjust startY to make space for leaderboard
        int leaderboardY = screenHeight / 2; // Position leaderboard button between start and exit
        int exitY = screenHeight / 2 + buttonHeight + 75; // Adjust exitY to make space

        startButton = new Rectangle(startX, startY, buttonWidth, buttonHeight);
        leaderboardButton = new Rectangle(startX, leaderboardY, buttonWidth, buttonHeight); // Initialize leaderboard button rectangle
        exitButton = new Rectangle(startX, exitY, buttonWidth, buttonHeight);
    }

    public int getCombatState() {
        return combatState;
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

    // Getter and setter for the score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        // Log score change to game log
        logToGameLog("Score changed to: " + score);
    }

    // Method to decrease score on enemy collision
    public void decreaseScore(int amount) {
        score -= amount;
        if (score < 0) {
            score = 0; // Prevent negative scores
        }
        logToGameLog("Score decreased by " + amount + ". New score: " + score);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void initMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse Clicked: " + e.getX() + ", " + e.getY());

                if (gameState == combatState) {
                    combatManager.handleMouseClick(e.getX(), e.getY());
                } else if (gameState == titleState) {
                    if (startButton.contains(e.getX(), e.getY())) {
                        gameState = playState;
                        gameTimer.resumeTimer();
                    } else if (leaderboardButton.contains(e.getX(), e.getY())) {
                        EndGameFrame.showLeaderboard(GamePanel.this);
                    } else if (exitButton.contains(e.getX(), e.getY())) {
                        System.exit(0);
                    } else if (gameState == gameOverState) {
                        restartGame();
                    }
                }
            }
        });
    }

    public void restartGame() {
        // Reset game variables
        score = 100;
        enemiesBeat = 0;
        catsCollected = 0;
        itemsBought = 0;
        healingJuice = 1;
        playerSpeed = 8;
        combatManager.setPlayerMaxHealth(100);
        combatManager.playerHealth = 100;

        // Reset combat specific variables
        combatManager.combatActive = false;
        combatManager.combatState = combatManager.COMBAT_INTRO;
        combatManager.currentEnemy = null;

        // Reset player position and map
        playerMovement.worldX = playerMovement.worldXOriginal;
        playerMovement.worldY = playerMovement.worldYOriginal;
        tiles.loadNewMap("/maps/world01");
        currentMapPath = "/maps/world01";

        // Reset game timer
        gameTimer.resetTimer();


        // Change game state to title state (main menu)
        gameState = titleState;
        repaint();

        // Show the EndGameFrame with a game over message
        EndGameFrame.showEndGameFrame(this, startTime);
    }

    public void run() {
        long drawInterval = 1000000000 / 60;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();
            if (keyHandler.escapePressed) {
                if (soundmgr.musicMenuFrame.isVisible()) {
                    soundmgr.hideMusicMenu();
                } else {
                    soundmgr.showMusicMenu();
                }
                keyHandler.escapePressed = false;
            }
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
        boolean collisionDetected = false;

        for (EntitySprite entitySprite : spriteManager.entity) {
            if (entitySprite.hasCollision()) {
                Rectangle entityRect = entitySprite.getCollisionBounds();
                if (playerRect.intersects(entityRect) && entitySprite.getIsEnemy()) {
                    // Always decrease score
                    decreaseScore(5); //Use the decreaseScore method.
                    collisionDetected = true;
                }
            }
        }
        return collisionDetected;
    }

    public void startCombat(EntitySprite enemy) {
        combatManager.startCombat(enemy);
    }

    public void update() {
        if (gameState == playState || gameState == dialogueState) {
            playerMovement.isPlayerBound();
            pSpriteManager.updateSprite(playerMovement.isMoving(), playerMovement.getDirection());

            for (EntitySprite entitySprite : spriteManager.entity) {
                entitySprite.updatePosition();
            }

            checkDialogueTrigger();

            // Check for combat triggers when in play state
            if (gameState == playState) {
                combatManager.checkCombatCollision();
            }
        } else if (gameState == pauseState) {
            gameTimer.pauseTimer();
        } else if (gameState == combatState) {
            // Update combat manager
            combatManager.update();
            if (combatManager.isPlayerDefeated()) {
                gameState = gameOverState; //Switch to game over state.
            }
        }
        gameTimer.updateTimer(); // Add this line to update the timer every frame
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == titleState) {
            drawMainMenu(g2);
            g2.dispose();
            return;
        }

        if (gameState == combatState) {
            combatManager.draw(g2);
            drawScore(g2);
            g2.dispose();
            return;
        }

        if (gameState == gameOverState) {
            drawGameOverScreen(g2);
            g2.dispose();
            return;
        }

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
            if (gameState == mapChangeDialogueState && dialogueManager.currentInteractable != null &&
                    dialogueManager.currentDialogue.equals("Do you want to enter your home?")) {
                System.out.println(dialogueManager.currentDialogue);
            }
        }

        if (shopManager.showShopMenu) {
            shopManager.drawShopMenu(g2);
        }

        gameTimer.drawGameTimer(g2);

        drawScore(g2);

        g2.dispose();
    }

    // Method to draw the score in the upper middle of the screen
    private void drawScore(Graphics2D g2) {
        // Save the original font and color
        Font originalFont = g2.getFont();
        Color originalColor = g2.getColor();

        // Set font and color for score display
        Font scoreFont = new Font("Arial", Font.BOLD, 24);
        g2.setFont(scoreFont);

        // Create a background for better visibility
        String scoreText = "Points: " + score;
        FontMetrics metrics = g2.getFontMetrics(scoreFont);
        int textWidth = metrics.stringWidth(scoreText);
        int textHeight = metrics.getHeight();

        int x = screenWidth / 2 - textWidth / 2;
        int y = 40;

        // Draw background rectangle
        g2.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
        g2.fillRoundRect(x - 10, y - textHeight + 5, textWidth + 20, textHeight + 10, 10, 10);

        // Draw text
        g2.setColor(Color.WHITE);
        g2.drawString(scoreText, x, y);

        // Restore original font and color
        g2.setFont(originalFont);
        g2.setColor(originalColor);
    }

    private void drawMainMenu(Graphics2D g2) {
        if (mainMenuBackground != null) {
            g2.drawImage(mainMenuBackground, 0, 0, screenWidth, screenHeight, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(startButton.x - 5, startButton.y - 5, startButton.width + 10, startButton.height + 10, 10, 10);
        g2.fillRoundRect(leaderboardButton.x - 5, leaderboardButton.y - 5, leaderboardButton.width + 10, leaderboardButton.height + 10, 10, 10); // Draw leaderboard button background
        g2.fillRoundRect(exitButton.x - 5, exitButton.y - 5, exitButton.width + 10, exitButton.height + 10, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        drawCenteredString(g2, "Start Game", startButton);
        drawCenteredString(g2, "Leaderboard", leaderboardButton); // Draw leaderboard button text
        drawCenteredString(g2, "Exit Game", exitButton);
    }

    private void drawCenteredString(Graphics2D g2, String text, Rectangle rect) {
        FontMetrics metrics = g2.getFontMetrics();
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(text, x, y);
    }

    // Update mouse handler to process combat clicks
    public void processMouseClick(int x, int y) {
        if (gameState == combatState) {
            combatManager.handleMouseClick(x, y);
        } else {
            // Handle regular game clicks
            // Existing click processing code...
        }
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
                        logToGameLog("Item " + shopManager.shopItems.get(i) + " purchased");
                        if(i==0){
                            healingJuice += 1;
                            logToGameLog("Player acquired +1 Health Juice.");
                        }

                        if(i==1){ // speedbuff Item
                            playerSpeed += 6;
                            logToGameLog("Player speed increased by 6.");
                        }
                        if(i==2){
                            int currentHP = combatManager.getPlayerMaxHealth();
                            combatManager.setPlayerMaxHealth(currentHP+=50);
                            logToGameLog("Player health increased by 50.");
                        }
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
                Rectangle playerRect = playerMovement.getPlayerCollisionBounds();
                if (distance < dialogueManager.dialogueDistanceThreshold) {
                    if (keyHandler.isEPressed()) {
                        if (!dialogueManager.dialogueTriggered || dialogueManager.currentInteractable != npc && playerRect.intersects(npcBounds)) {
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
                        } else if (npc.getName().startsWith("Your Home")) {
                            // Game has ended, player reached home
                            if (!endGameTriggered) {
                                endGameTriggered = true;
                                playerMovement.setPlayerCanMove(false);
                                gameTimer.pauseTimer();
                                long totalGameTime = System.currentTimeMillis() - gameTimer.getElapsedTime();

                                // Show the end game frame
                                EndGameFrame.showEndGameFrame(this, totalGameTime);
                            }
                            break;
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

    private String message;

    public void logToGameLog(String message) {
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

            if (this.message != message) {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String formattedDateTime = now.format(formatter);
                    out.println(formattedDateTime + " - " + message);
                    this.message = message;
                } catch (IOException e) {
                    System.out.println("Error writing to log file: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error creating log file or directories: " + e.getMessage());
        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "Game Over";
        FontMetrics metrics = g2.getFontMetrics();
        int x = (screenWidth - metrics.stringWidth(gameOverText)) / 2;
        int y = screenHeight / 2;
        g2.drawString(gameOverText, x, y);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String restartText = "Click to Restart";
        metrics = g2.getFontMetrics();
        x = (screenWidth - metrics.stringWidth(restartText)) / 2;
        y = screenHeight / 2 + 50;
        g2.drawString(restartText, x, y);
    }

    private void changeMap(String mapPath) {
        tiles.loadNewMap(mapPath);
        currentMapPath = mapPath;
        playerMovement.worldX = tileSize * maxWorldRow / 4 - screenWidth / 2 + 2000;
        playerMovement.worldY = tileSize * maxWorldCol / 4 - screenHeight / 2;
    }
}