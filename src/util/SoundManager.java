package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class SoundManager {
    private Clip clip;
    private final URL[] musicURL = new URL[10];
    private final URL[] effectURL = new URL[10];
    private int currentMusicIndex = 0;
    public JFrame musicMenuFrame;
    private JList<String> musicList;
    private final String[] musicNames = {"Shop", "Condensed Milk", "Snowden", "No Music"};
    private float musicVolume = 0.188f;
    private float effectVolume = 1.0f;
    private final boolean DEBUG = true;

    public SoundManager() {
        initSoundResources();
        createMusicMenu();
        System.out.println("SoundManager constructor finished. Music menu frame: " + (musicMenuFrame != null));
    }

    private void initSoundResources() {
        if (DEBUG) {
            System.out.println("Working Directory: " + System.getProperty("user.dir"));
            URL rootURL = getClass().getResource("/");
            System.out.println("Classpath root: " + (rootURL != null ? rootURL.getPath() : "null"));
        }

        musicURL[0] = getClass().getResource("/sounds/music/shop.wav");
        musicURL[1] = getClass().getResource("/sounds/music/condensedMilk.wav");
        musicURL[2] = getClass().getResource("/sounds/music/snowden.wav");

        for (int i = 0; i < 3; i++) {
            if (musicURL[i] == null) {
                System.err.println("ERROR: Could not find music file at index " + i);
            } else {
                System.out.println("Successfully loaded music " + i + ": " + musicURL[i]);
            }
        }

        effectURL[0] = getClass().getResource("/sounds/effects/whoosh.wav");
        if (effectURL[0] == null) {
            System.err.println("ERROR: Could not find sound effect file: /sounds/effects/whoosh.wav");
            effectURL[0] = getClass().getResource("sounds/effects/whoosh.wav");
            if (effectURL[0] == null) {
                System.err.println("ERROR: Also failed with alternative path: sounds/effects/whoosh.wav");
            }
        } else {
            System.out.println("Successfully loaded sound effect: " + effectURL[0]);
        }
    }

    private void createMusicMenu() {
        musicMenuFrame = new JFrame("Music Menu");
        musicMenuFrame.setSize(300, 200);
        musicMenuFrame.setLocationRelativeTo(null);
        musicMenuFrame.setLayout(new BorderLayout());

        musicList = new JList<>(musicNames);
        musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        musicList.setSelectedIndex(currentMusicIndex);
        musicMenuFrame.add(new JScrollPane(musicList), BorderLayout.CENTER);

        JButton playButton = new JButton("Play Selected");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = musicList.getSelectedIndex();
                if (selectedIndex == 3){
                    stop();
                    return;
                }
                if (selectedIndex >= 0 && selectedIndex < musicURL.length && musicURL[selectedIndex] != null) {
                    currentMusicIndex = selectedIndex;
                    playMusic(currentMusicIndex);
                    loop();
                }
            }
        });
        musicMenuFrame.add(playButton, BorderLayout.SOUTH);

        musicMenuFrame.setVisible(false);
    }

    public void showMusicMenu() {
        musicMenuFrame.setVisible(true);
    }

    public void hideMusicMenu() {
        musicMenuFrame.setVisible(false);
    }

    public void playMusic(int index) {
        if (index >= musicURL.length || musicURL[index] == null) {
            System.err.println("Cannot play music: invalid index or null resource at index " + index);
            return;
        }

        System.out.println("Playing music: " + musicURL[index]);

        try {
            stop();
            AudioInputStream ais = AudioSystem.getAudioInputStream(musicURL[index]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            updateMusicVolume();
            clip.start();
        } catch (IOException e) {
            System.err.println("IO Error playing music: " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("General exception playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playEffect(int index) {
        if (index >= effectURL.length || effectURL[index] == null) {
            System.err.println("Cannot play effect: invalid index or null resource at index " + index);
            return;
        }

        System.out.println("Playing effect: " + effectURL[index]);

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(effectURL[index]);
            Clip effectClip = AudioSystem.getClip();
            effectClip.open(ais);
            try {
                FloatControl gainControl = (FloatControl) effectClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (effectVolume > 0.0f) ? (float) (20.0 * Math.log10(effectVolume)) : -80.0f;
                gainControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                System.err.println("Cannot control effect volume: " + e.getMessage());
            }
            effectClip.start();
            effectClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    effectClip.close();
                }
            });
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loop() {
        if (clip != null && clip.isOpen()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            playMusic(currentMusicIndex);
            if (clip != null && clip.isOpen()) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void stop() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            if (clip.isOpen()) {
                clip.close();
            }
            clip = null;
        }
    }

    public void loopCycle() {
        stop();
        if (musicURL[0] != null && allOthersNull()) {
            playMusic(0);
            loop();
            return;
        }
        boolean foundMusic = false;
        for (URL url : musicURL) {
            if (url != null) {
                foundMusic = true;
                break;
            }
        }
        if (!foundMusic) {
            System.err.println("No valid music resources found!");
            return;
        }
        int originalIndex = currentMusicIndex;
        do {
            currentMusicIndex = (currentMusicIndex + 1) % musicURL.length;
            if (currentMusicIndex == originalIndex) {
                break;
            }
        } while (musicURL[currentMusicIndex] == null);
        playMusic(currentMusicIndex);
        loop();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateMusicVolume();
    }

    public float getMusicVolume() {
        return this.musicVolume;
    }

    private void updateMusicVolume() {
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (musicVolume > 0.0f) ? (float) (20.0 * Math.log10(musicVolume)) : -80.0f;
                gainControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                System.err.println("Cannot control volume: " + e.getMessage());
            }
        }
    }

    public void setEffectVolume(float volume) {
        this.effectVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getEffectVolume() {
        return this.effectVolume;
    }

    private boolean allOthersNull() {
        for (int i = 1; i < musicURL.length; i++) {
            if (musicURL[i] != null) {
                return false;
            }
        }
        return true;
    }
}