package de.MCmoderSD.utilities.other;

import okhttp3.ResponseBody;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class WavPlayer {

    // Attributes
    private final HashMap<String, byte[]> byteCache;
    private final HashMap<byte[], Clip> clipCache;

    // Constructor
    public WavPlayer() {
        byteCache = new HashMap<>();
        clipCache = new HashMap<>();
    }

    // Play audio
    private void play(byte[] audio) throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        // Check audio
        if (audio == null || audio.length == 0) throw new IllegalArgumentException("Audio is empty");

        // Check cache
        if (clipCache.containsKey(audio)) {
            clipCache.get(audio).stop();
            clipCache.get(audio).setFramePosition(0);
            clipCache.get(audio).start();
        } else {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audio));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clipCache.put(audio, clip);
        }
    }

    // Format audio
    public void play(ResponseBody responseBody) {
        new Thread(() -> {
            try {
                byte[] audio = responseBody.bytes();
                play(audio);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException |
                     IllegalArgumentException e) {
                System.out.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    // Format TTS audio
    public void play(String text, ResponseBody responseBody) {
        new Thread(() -> {
            try {
                byte[] audio = responseBody.bytes();
                byteCache.put(text, audio);
                play(audio);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException |
                     IllegalArgumentException e) {
                System.out.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    // Stop audio
    public void stop(String text) {
        if (byteCache.containsKey(text)) clipCache.get(byteCache.get(text)).stop();
    }

    public void stop(byte[] audio) {
        if (clipCache.containsKey(audio)) clipCache.get(audio).stop();
    }

    public void stopAll() {
        clipCache.values().forEach(Clip::stop);
    }

    // Getter
    public byte[] getAudio(String text) {
        return byteCache.get(text);
    }

    public Clip getClip(String text) {
        return clipCache.get(byteCache.get(text));
    }

    public Clip getClip(byte[] audio) {
        return clipCache.get(audio);
    }

    // Setter
    public void setAudio(String text, byte[] audio) {
        byteCache.put(text, audio);
    }

    public void setClip(String text, Clip clip) {
        clipCache.put(byteCache.get(text), clip);
    }

    public void setClip(byte[] audio, Clip clip) {
        clipCache.put(audio, clip);
    }

    // Clear cache
    public void clear() {
        byteCache.clear();
        clipCache.clear();
    }
}