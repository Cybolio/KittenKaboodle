package util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundManager {
    private Clip clip;
    private URL[] musicURL = new URL[10]; // Array for background music
    private URL[] effectURL = new URL[10]; // Array for sound effects
    private int currentMusicIndex = 0;

    // Volume control properties
    private float musicVolume = 0.188f; // Default full volume (range: 0.0f to 1.0f)
    private float effectVolume = 1.0f; // Default full volume

    // Debug flag
    private final boolean DEBUG = true;

    public SoundManager() {
        initSoundResources();
    }

    private void initSoundResources() {
        if (DEBUG) {
            System.out.println("Working Directory: " + System.getProperty("user.dir"));
            URL rootURL = getClass().getResource("/");
            System.out.println("Classpath root: " + (rootURL != null ? rootURL.getPath() : "null"));
        }

        // Initialize multiple music resources
        musicURL[0] = getClass().getResource("/sounds/music/shop.wav");
        musicURL[1] = getClass().getResource("/sounds/music/shop.wav");
        musicURL[2] = getClass().getResource("/sounds/music/shop.wav");

        // Log success/failure for each music file
        for (int i = 0; i < 3; i++) {
            if (musicURL[i] == null) {
                System.err.println("ERROR: Could not find music file at index " + i);
            } else {
                System.out.println("Successfully loaded music " + i + ": " + musicURL[i]);
            }
        }

        // Initialize sound effect resources (using WAV instead of OGG)
        effectURL[0] = getClass().getResource("/sounds/effects/whoosh.wav");
        if (effectURL[0] == null) {
            System.err.println("ERROR: Could not find sound effect file: /sounds/effects/whoosh.wav");
            // Try alternative path without leading slash
            effectURL[0] = getClass().getResource("sounds/effects/whoosh.wav");
            if (effectURL[0] == null) {
                System.err.println("ERROR: Also failed with alternative path: sounds/effects/whoosh.wav");
            }
        } else {
            System.out.println("Successfully loaded sound effect: " + effectURL[0]);
        }
    }

    public void playMusic(int index) {
        if (index >= musicURL.length || musicURL[index] == null) {
            System.err.println("Cannot play music: invalid index or null resource at index " + index);
            return;
        }

        System.out.println("Playing music: " + musicURL[index]);

        try {
            // Stop any currently playing music
            stop();

            // Start new music
            AudioInputStream ais = AudioSystem.getAudioInputStream(musicURL[index]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            updateMusicVolume(); // Apply volume setting
            clip.start();

            // Add listener for auto cycling if needed
            // clip.addLineListener(event -> {
            //     if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
            //         // The track finished playing naturally (not stopped by user)
            //         loopCycle(); // Move to next track
            //     }
            // });
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

            // Apply effect volume
            try {
                FloatControl gainControl = (FloatControl) effectClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (effectVolume > 0.0f) ?
                        (float) (20.0 * Math.log10(effectVolume)) : -80.0f;
                gainControl.setValue(dB);
            } catch (IllegalArgumentException e) {
                System.err.println("Cannot control effect volume: " + e.getMessage());
            }

            effectClip.start();

            // Auto-close the clip when done playing
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
            // Start the first music if nothing is playing
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
            clip = null; // Clear the clip
        }
    }

    // Cycles through background music
    public void loopCycle() {
        stop(); // Stop any currently playing music

        // Check if we have only one music file
        if (musicURL[0] != null && allOthersNull()) {
            playMusic(0);
            loop();
            return;
        }

        // Check if we have any music at all
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

        // Find the next valid music URL
        int originalIndex = currentMusicIndex;
        do {
            currentMusicIndex = (currentMusicIndex + 1) % musicURL.length;
            // Prevent infinite loop if no other tracks are found
            if (currentMusicIndex == originalIndex) {
                break;
            }
        } while (musicURL[currentMusicIndex] == null);

        playMusic(currentMusicIndex);
        loop(); // Start looping the new music
    }

    // Volume control methods
    public void setMusicVolume(float volume) {
        // Ensure volume is between 0.0 and 1.0
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
                // Convert linear scale (0.0 to 1.0) to dB scale (using logarithm)
                float dB = (musicVolume > 0.0f) ?
                        (float) (20.0 * Math.log10(musicVolume)) : -80.0f;
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

    // Helper method to check if all music slots except index 0 are null
    private boolean allOthersNull() {
        for (int i = 1; i < musicURL.length; i++) {
            if (musicURL[i] != null) {
                return false;
            }
        }
        return true;
    }

    // Helper method to check if a file exists
    private boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}