package util;

import Main.GamePanel;

import java.awt.*;

public class GameTimer {
    private GamePanel gp;
    private Thread timerThread;
    private long startTime;
    private long elapsedTime;
    private boolean timerRunning;
    private String formattedTime = "00:00:00";

    public GameTimer(GamePanel gp) {
        this.gp = gp;
    }

    public void startTimerThread() {
        startTime = System.currentTimeMillis();
        timerRunning = true;

        timerThread = new Thread(() -> {
            while (timerRunning) {
                if (gp.gameState == gp.playState) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    formattedTime = formatTime(elapsedTime);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        timerThread.start();
    }

    private String formatTime(long elapsedTime) {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long tenths = (elapsedTime / 100) % 10;
        return String.format("%02d:%02d:%02d", minutes, seconds % 60, tenths);
    }

    public void pauseTimer() {
        if (timerRunning && gp.gameState == gp.pauseState) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }

    public void resumeTimer() {
        if (timerRunning && gp.gameState == gp.playState) {
            startTime = System.currentTimeMillis() - elapsedTime;
        }
    }

    public void stopTimer() {
        timerRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    public void drawGameTimer(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        g2.setColor(Color.BLACK);
        g2.drawString("Time: " + formattedTime, 11, 31);
        g2.setColor(Color.WHITE);
        g2.drawString("Time: " + formattedTime, 10, 30);
    }
}