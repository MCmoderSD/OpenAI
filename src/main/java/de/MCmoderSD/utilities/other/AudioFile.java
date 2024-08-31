package de.MCmoderSD.utilities.other;

import okhttp3.ResponseBody;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unused")
public class AudioFile {

    // Attributes
    private final byte[] audioData;
    private final ByteArrayInputStream byteArrayInputStream;
    private final AudioInputStream audioInputStream;
    private final AudioFormat audioFormat;
    private final DataLine.Info info;
    private final SourceDataLine audioLine;

    // Byte array constructor
    public AudioFile(byte[] audioData) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.audioData = audioData;
        byteArrayInputStream = new ByteArrayInputStream(audioData);
        audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
        audioFormat = audioInputStream.getFormat();
        info = new DataLine.Info(SourceDataLine.class, audioFormat);
        audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(audioFormat);
    }

    // ResponseBody constructor
    public AudioFile(ResponseBody responseBody) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.writeBytes(responseBody.bytes());
        audioData = byteArrayOutputStream.toByteArray();
        byteArrayInputStream = new ByteArrayInputStream(audioData);
        audioInputStream = AudioSystem.getAudioInputStream(byteArrayInputStream);
        audioFormat = audioInputStream.getFormat();
        info = new DataLine.Info(SourceDataLine.class, audioFormat);
        audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(audioFormat);
    }

    // Play audio
    public void play() {
        new Thread(() -> {
            try {
                // Reset audio stream
                audioInputStream.reset();
                audioLine.start();

                // Play audio
                byte[] allData = audioInputStream.readAllBytes();
                audioLine.write(allData, 0, allData.length);

                // Stop audio
                audioLine.drain();
                audioLine.stop();
            } catch (IOException e) {
                System.err.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    public void pause() {
        audioLine.stop();
    }

    public void resume() {
        audioLine.start();
    }

    public void close() {
        audioLine.close();
    }

    public void reset() {
        try {
            audioInputStream.reset();
        } catch (IOException e) {
            System.err.println("Error resetting audio: " + e.getMessage());
        }
    }

    // Getter
    public byte[] getAudioData() {
        return audioData;
    }

    public ByteArrayInputStream getByteArrayInputStream() {
        return byteArrayInputStream;
    }

    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public DataLine.Info getInfo() {
        return info;
    }

    public SourceDataLine getAudioLine() {
        return audioLine;
    }
}