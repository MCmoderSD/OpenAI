package de.MCmoderSD.objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.DataLine;
import java.io.ByteArrayOutputStream;

@SuppressWarnings("unused")
public class AudioRecorder {

    // Attributes
    private final AudioFormat format;

    // Variables
    private boolean isRecording;
    private TargetDataLine line;
    private ByteArrayOutputStream buffer;

    // Constructor
    public AudioRecorder() {

        // Audio format
        float sampleRate = 48000;
        int sampleSizeInBits = 16;
        int channels = 1; // Mono
        boolean signed = true;
        boolean bigEndian = false;

        // Set audio format
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        // Initialize variables
        isRecording = false;
    }

    public void startRecording() {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) throw new RuntimeException("Audio format not supported!");

        new Thread(() -> {
            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                isRecording = true;
                buffer = new ByteArrayOutputStream();
                byte[] data = new byte[line.getBufferSize()];

                while (isRecording) {
                    int bytesRead = line.read(data, 0, data.length);
                    if (bytesRead == -1) break;
                    buffer.write(data, 0, bytesRead);
                }
            } catch (LineUnavailableException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }).start();
    }

    // Stop recording
    public void stopRecording() {
        if (isRecording && line != null) {
            isRecording = false;
            line.stop();
            line.close();
        }
    }

    // Get the recorded audio as an AudioFile
    public AudioFile getAudioFile() {

        // Stop recording
        stopRecording();

        // Return audio file
        if (buffer != null) {
            byte[] audioData = buffer.toByteArray();
            return new AudioFile(audioData, format);
        }
        return null;
    }

    // Getters
    public AudioFormat getFormat() {
        return format;
    }

    public boolean isRecording() {
        return isRecording;
    }
}