package util;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Iterator;

public class GameLoopManager {
    private GamePanel gp;
    public boolean combatActive = false;
    public EntitySprite currentEnemy = null;
    private PlayerSprite playerSprite;

    public final int COMBAT_INTRO = 0;
    public final int PLAYER_TURN = 1;
    public final int ENEMY_TURN = 2;
    public final int COMBAT_RESULT = 3;
    public int combatState = COMBAT_INTRO;
    private boolean isHealing = false;
    public int playerHealth = 100;
    private int playerMaxHealth = 100;
    private int playerAttack = 25;
    private int playerDefense = 5;

    private int enemyHealth = 80;
    private int enemyMaxHealth = 80;
    private int enemyAttack = 10;
    private int enemyDefense = 3;
    private Rectangle attackButton;
    private Rectangle defendButton;
    private Rectangle itemButton;
    private Rectangle fleeButton;

    private String combatMessage = "";
    private int messageDuration = 0;

    private int animationCounter = 0;
    private boolean showDamage = false;
    private int damageValue = 0;
    private int damageX, damageY;
    private boolean isPlayerDamaged = false;
    private boolean isGameOver = false;
    private BufferedImage combatBackground;

    private Random random = new Random();
    private final String LOG_FILE = "GameLogs/collision_log.txt";
    private int frameCounter = 0;
    private final int COLLISION_CHECK_INTERVAL = 15;

    public GameLoopManager(GamePanel gp) {
        this.gp = gp;
        this.playerSprite = new PlayerSprite(gp);

        int buttonWidth = 150;
        int buttonHeight = 50;
        int startX = gp.getScreenWidth() / 2 - buttonWidth - 10;
        int startY = gp.getScreenHeight() - 120;

        attackButton = new Rectangle(startX, startY, buttonWidth, buttonHeight);
        defendButton = new Rectangle(startX + buttonWidth + 20, startY, buttonWidth, buttonHeight);
        itemButton = new Rectangle(startX, startY + buttonHeight + 10, buttonWidth, buttonHeight);
        fleeButton = new Rectangle(startX + buttonWidth + 20, startY + buttonHeight + 10, buttonWidth, buttonHeight);

        try {
            combatBackground = ImageIO.read(getClass().getResourceAsStream("/tiles/background/CombatBackground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerMaxHealth(int playerMaxHealth) {
        this.playerMaxHealth = playerMaxHealth;
    }

    public int getPlayerMaxHealth() {
        return playerMaxHealth;
    }
    EntitySprite enemy;
    public void startCombat(EntitySprite enemy) {
        this.enemy = enemy;
        combatActive = true;
        currentEnemy = enemy;
        combatState = COMBAT_INTRO;
        combatMessage = "A wild " + enemy.getName() + " appeared!";
        messageDuration = 120;
        gp.logToGameLog("Turn-based combat started with " + enemy.getName());
        //playerHealth = playerMaxHealth;

        if (enemy.getName().startsWith("cop")) {
            enemyHealth = 50;
            enemyMaxHealth = 50;
            enemyAttack = 12;
            enemyDefense = 8;
        }

        gp.playerMovement.setPlayerCanMove(false);
        gp.gameState = gp.getCombatState();
        gp.gameTimer.pauseTimer();
    }

    public void endCombat(boolean playerWon) {
        if (playerWon) {
            combatMessage = "player won the battle!";
            gp.logToGameLog(combatMessage + " against " + enemy.getName());
            gp.setScore(gp.score += 250);
            gp.enemiesBeat++;
        } else {
            combatMessage = "player lost the battle!";
            gp.logToGameLog(combatMessage + " against " + enemy.getName());
            //playerHealth = playerMaxHealth;
            isGameOver = true;
        }

        messageDuration = 120;
        combatState = COMBAT_RESULT;

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                combatActive = false;
                gp.playerMovement.setPlayerCanMove(true);
                gp.gameState = gp.playState;
                System.out.println("endCombat: Game state set to gp.playState");
                gp.gameTimer.resumeTimer();
                isGameOver = false;
                System.out.println("endCombat: combatActive set to false");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= COLLISION_CHECK_INTERVAL) {
            frameCounter = 0;
            checkCombatCollision();
        }

        if (!combatActive) {
            return;
        }

        if (messageDuration > 0) {
            messageDuration--;
            if (messageDuration == 0 && combatState == COMBAT_INTRO) {
                combatState = PLAYER_TURN;
                combatMessage = "Your turn! Choose your action.";
            }
        }

        if (showDamage) {
            animationCounter++;
            if (animationCounter > 60) {
                showDamage = false;
                animationCounter = 0;

                if (combatState == PLAYER_TURN) {
                    if (enemyHealth <= 0) {
                        endCombat(true);
                    } else {
                        combatState = ENEMY_TURN;
                        enemyAction();
                    }
                } else if (combatState == ENEMY_TURN) {
                    if (playerHealth <= 0) {
                        endCombat(false);
                    } else {
                        combatState = PLAYER_TURN;
                        combatMessage = "Your turn! Choose your action.";
                    }
                }
            }
        }
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (!combatActive || combatState != PLAYER_TURN || showDamage) return;

        if (attackButton.contains(mouseX, mouseY)) {
            playerAttackAction();
        } else if (defendButton.contains(mouseX, mouseY)) {
            playerDefendAction();
        } else if (itemButton.contains(mouseX, mouseY)) {
            playerItemAction();
        } else if (fleeButton.contains(mouseX, mouseY)) {
            playerFleeAction();
        }
    }

    private void playerAttackAction() {
        int damage = playerAttack + random.nextInt(20);
        damage = Math.max(10, damage - enemyDefense);
        isHealing = false;
        enemyHealth -= damage;
        enemyHealth = Math.max(0, enemyHealth);

        showDamage = true;
        damageValue = damage;
        damageX = gp.getScreenWidth() / 4 * 3;
        damageY = gp.getScreenHeight() / 2;
        isPlayerDamaged = false;

        combatMessage = "You dealt " + damage + " damage to " + currentEnemy.getName() + "!";
    }

    private void playerDefendAction() {
        playerDefense += 5;

        combatMessage = "You prepare to defend against the next attack!";
        combatState = ENEMY_TURN;
        enemyAction();
    }

    private void playerItemAction() {
        if (gp.healingJuice > 0 && playerHealth < playerMaxHealth) {
            int healAmount = 10 + random.nextInt(4);
            gp.healingJuice--;
            playerHealth += healAmount;
            playerHealth = Math.min(playerMaxHealth, playerHealth);
            combatMessage = "You used a healing juice and recovered " + healAmount + " health!";

        } else {
            if (gp.healingJuice < 1) {
                combatMessage = "You are out of healing juice";
                return;
            }
            if (playerHealth == playerMaxHealth) {
                combatMessage = "You are at full health";
                return;
            }
        }
        combatState = ENEMY_TURN;
        enemyAction();
    }

    private void playerFleeAction() {
        if (random.nextBoolean()) {
            combatMessage = "You successfully fled from battle!";
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    combatActive = false;
                    gp.playerMovement.setPlayerCanMove(true);
                    gp.gameState = gp.playState;
                    gp.gameTimer.resumeTimer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            combatMessage = "Couldn't escape! Enemy attacks!";
            combatState = ENEMY_TURN;
            enemyAction();
        }
    }

    private void enemyAction() {
        if (playerDefense > 5) {
            playerDefense = 5;
        }

        int action = random.nextInt(3);
        if (action == 0) {
            int damage = enemyAttack + random.nextInt(3);
            damage = Math.max(1, damage - playerDefense);

            playerHealth -= damage;
            playerHealth = Math.max(0, playerHealth);

            showDamage = true;
            damageValue = damage;
            damageX = gp.getScreenWidth() / 4;
            damageY = gp.getScreenHeight() / 2;
            isPlayerDamaged = true;

            combatMessage = currentEnemy.getName() + " dealt " + damage + " damage to you!";
        } else if (action == 1) {
            enemyDefense += 5;
            combatMessage = currentEnemy.getName() + " is guarding!";
            messageDuration = 60;
            combatState = PLAYER_TURN;
        } else {
            int healAmount = 15 + random.nextInt(5);
            enemyHealth += healAmount;
            enemyHealth = Math.min(enemyMaxHealth, enemyHealth);

            showDamage = true;
            isHealing = true;
            damageValue = healAmount;
            damageX = gp.getScreenWidth() / 4 * 3;
            damageY = gp.getScreenHeight() / 2;

            combatMessage = currentEnemy.getName() + " healed " + healAmount + " health!";
        }
    }

    public void draw(Graphics2D g2) {
        if (!combatActive) return;

        if (combatBackground != null) {
            g2.drawImage(combatBackground, 0, 0, gp.getScreenWidth(), gp.getScreenHeight(), null);
        } else {
            g2.setColor(new Color(50, 50, 50, 200));
            g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
        }

        int playerX = gp.getScreenWidth() / 4 - 50;
        int playerY = gp.getScreenHeight() / 2;
        int enemyX = gp.getScreenWidth() / 4 * 3 - 50;
        int enemyY = gp.getScreenHeight() / 2;

        playerSprite.drawSprite(g2, playerX, playerY, gp.getTileSize());

        if (currentEnemy != null && currentEnemy.sprites != null && currentEnemy.sprites.length > 0) {
            BufferedImage enemyImage = currentEnemy.sprites[0];
            if (enemyImage != null) {
                int scaledWidth = gp.getTileSize();
                int scaledHeight = gp.getTileSize();
                g2.drawImage(enemyImage, enemyX, enemyY, scaledWidth, scaledHeight, null);
            }
        }

        drawHealthBar(g2, playerX, playerY - 30, playerHealth, playerMaxHealth, Color.GREEN);
        drawHealthBar(g2, enemyX, enemyY - 30, enemyHealth, enemyMaxHealth, Color.RED);

        drawCombatMessage(g2);

        if (combatState == PLAYER_TURN && !showDamage) {
            drawActionButtons(g2);
        }

        if (showDamage) {
            drawDamageAnimation(g2);
        }
    }

    private void drawHealthBar(Graphics2D g2, int x, int y, int currentHealth, int maxHealth, Color color) {
        int barWidth = 100;
        int barHeight = 10;
        int filledWidth = (int) ((double) currentHealth / maxHealth * barWidth);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        g2.setColor(color);
        g2.fillRect(x, y, filledWidth, barHeight);

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, barWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(currentHealth + "/" + maxHealth, x + barWidth + 5, y + barHeight);
    }

    private void drawCombatMessage(Graphics2D g2) {
        int boxX = 50;
        int boxY = 50;
        int boxWidth = gp.getScreenWidth() - 100;
        int boxHeight = 80;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(combatMessage);
        int textX = boxX + (boxWidth - textWidth) / 2;
        int textY = boxY + boxHeight / 2 + fm.getHeight() / 4;

        g2.drawString(combatMessage, textX, textY);
    }

    private void drawActionButtons(Graphics2D g2) {
        drawButton(g2, attackButton, "Attack", Color.RED);
        drawButton(g2, defendButton, "Defend", Color.BLUE);
        drawButton(g2, itemButton, "Heal x" + gp.healingJuice, Color.GREEN);
        drawButton(g2, fleeButton, "Flee", Color.ORANGE);
    }

    private void drawButton(Graphics2D g2, Rectangle button, String text, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(button.x, button.y, button.width, button.height, 10, 10);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(button.x, button.y, button.width, button.height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = button.x + (button.width - textWidth) / 2;
        int textY = button.y + button.height / 2 + fm.getHeight() / 4;

        g2.drawString(text, textX, textY);
    }

    private void drawDamageAnimation(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        int offsetY = (int) (Math.sin(animationCounter * 0.1) * 10);

        g2.setColor(Color.BLACK);
        String displayText = (isHealing ? "+" : "-") + damageValue;
        g2.drawString(displayText, damageX + 2, damageY + offsetY + 2);

        if (isHealing) {
            g2.setColor(Color.GREEN);
        } else {
            g2.setColor(isPlayerDamaged ? Color.RED : Color.ORANGE);
        }

        g2.drawString(displayText, damageX, damageY + offsetY);
    }

    public void checkCombatCollision() {
        if (combatActive) {
            System.out.println("checkCombatCollision: Combat active. Returning.");
            return;
        }

        Rectangle playerRect = gp.playerMovement.getPlayerCollisionBounds();

        Iterator<EntitySprite> iterator = gp.spriteManager.entity.iterator();
        while (iterator.hasNext()) {
            EntitySprite entity = iterator.next();
            Rectangle entityRect = entity.getCollisionBounds();
            entityRect.grow(10, 10);

            if (playerRect.intersects(entityRect)) {
                logCollision(entity.getName());
                if (entity.getName().startsWith("cop")) {
                    System.out.println("Cop collision detected!");
                    System.out.println("Player Rect: " + playerRect);
                    System.out.println("Cop Rect: " + entityRect);

                    int randomNumber = new Random().nextInt(100);
                    System.out.println("Random number: " + randomNumber);

                    if (randomNumber < 5) {
                        System.out.println("Starting combat!");
                        startCombat(entity);
                        break;
                    } else {
                        System.out.println("Combat Avoided.");
                    }
                } else if (entity.getName().startsWith("Cat")) {
                    gp.setScore(gp.getScore() + 250);
                    gp.catsCollected++;
                    iterator.remove();
                    gp.logToGameLog("Cat entity removed. Score increased by 250.");
                    break;
                } else if (entity.getName().startsWith("Your Home")) {
                    System.out.println("checkCombatCollision: Your Home collision detected (post-combat)!");
                    gp.setMinimumSize(new Dimension(gp.getScreenWidth(), gp.getScreenHeight()));
                    gp.endGameTriggered = true;
                    gp.playerMovement.setPlayerCanMove(false);
                    gp.gameTimer.pauseTimer();
                    long totalGameTime = System.currentTimeMillis() - gp.gameTimer.getElapsedTime();

                    EndGameFrame.showEndGameFrame(gp, totalGameTime);

                    gp.gameState = gp.gameOverState;

                    break;
                }
            }
        }
    }

    public boolean isPlayerDefeated() {
        return isGameOver;
    }

    // Leaderboard Methods
    public void recordLeaderboardEntry(String playerName, long gameTime) {
        System.out.println("recordLeaderboardEntry called with: " + playerName + ", " + gameTime);
        ArrayList<LeaderboardEntry> leaderboard = loadLeaderboard();
        LeaderboardEntry newEntry = new LeaderboardEntry(playerName, gp.score, gameTime);

        leaderboard.add(newEntry);
        Collections.sort(leaderboard, Comparator.comparingInt(LeaderboardEntry::getScore).reversed()
                .thenComparingLong(LeaderboardEntry::getGameTime));

        if (leaderboard.size() > 5) {
            leaderboard = new ArrayList<>(leaderboard.subList(0, 5));
        }

        saveLeaderboard(leaderboard);
    }

    public ArrayList<LeaderboardEntry> loadLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        File file = new File("GameLogs/leaderboard.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    leaderboard.add(new LeaderboardEntry(parts[0], Integer.parseInt(parts[1]), Long.parseLong(parts[2])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
        }
        return leaderboard;
    }

    private void saveLeaderboard(ArrayList<LeaderboardEntry> leaderboard) {
        File file = new File("GameLogs/leaderboard.txt");
        System.out.println("Saving leaderboard to: " + file.getAbsolutePath());
        try (PrintWriter writer = new PrintWriter(file)) {
            for (LeaderboardEntry entry : leaderboard) {
                writer.println(entry.getPlayerName() + "," + entry.getScore() + "," + entry.getGameTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class LeaderboardEntry {
        private String playerName;
        private int score;
        private long gameTime;

        public LeaderboardEntry(String playerName, int score, long gameTime) {
            this.playerName = playerName;
            this.score = score;
            this.gameTime = gameTime;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public long getGameTime() {
            return gameTime;
        }
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
}