package de.MCmoderSD.utilities.other;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

public class Calculate {

    // Constants
    public final static String BOLD = "\033[0;1m";
    public final static String UNBOLD = "\u001B[0m";
    public final static String BREAK = "\n";

    // Colors
    public final static Color DARK = new Color(0x0e0e10);
    public final static Color LIGHT = new Color(0x18181b);
    public final static Color PURPLE = new Color(0x771fe2);
    public final static Color WHITE = new Color(0xffffff);

    // Center JFrame
    public static Point centerJFrame(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - frame.getWidth()) / 2;
        int y = (dim.height - frame.getHeight()) / 2;
        return new Point(x, y);
    }

    // Replace Emojis
    public static String replaceEmojis(String input, String replacement) {
        return input.replaceAll("[\\p{So}\\p{Cn}]", replacement);
    }

    // Remove repetitions
    public static String removeRepetitions(String input, String repetition) {
        return input.replaceAll("\\b(" + repetition + ")\\s+\\1\\b", "$1");
    }

    // Format OpenAI Response
    public static String formatOpenAiResponse(String response, String emote) {
        return removeRepetitions(replaceEmojis(response.replaceAll("(?i)" + emote + "[.,!?\\s]*", emote + " "), emote), emote);
    }

    // Calculate Tokens
    public static String getSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found");
            return null;
        }
    }
}