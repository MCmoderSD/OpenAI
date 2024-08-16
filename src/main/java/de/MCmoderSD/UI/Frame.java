package de.MCmoderSD.UI;

import de.MCmoderSD.core.ChatGPT;

import javax.swing.*;
import java.awt.*;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class Frame extends JFrame {

    // Associations
    private final ChatGPT chatGPT;
    private final MenuPanel menuPanel;
    private final ChatPanel chatPanel;

    // Constructor
    public Frame(ChatGPT chatGPT) {

        // Init Frame
        super("OpenAI Chatbot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set ChatGPT
        this.chatGPT = chatGPT;

        // Set Colors
        setBackground(DARK);
        setForeground(PURPLE);

        // Set Layout
        setLayout(new BorderLayout());

        // Variables
        var multiplyer = 0.75;
        var rawHeight = getToolkit().getScreenSize().getHeight() * multiplyer;
        var rawWidth = rawHeight * ((double) 4 / 3);
        var height = Math.toIntExact(Math.round(rawHeight));
        var width = Math.toIntExact(Math.round(rawWidth));
        Dimension size = new Dimension(width, height);

        // Add Panel
        chatPanel = new ChatPanel(this, size);
        menuPanel = new MenuPanel(this, size);

        // Set ChatPanel
        chatPanel.append("You: " + BREAK);

        // Set Visible
        pack();
        setLocation(centerJFrame(this));
        setVisible(true);
    }

    // Getter

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }

    public ChatGPT getChatGPT() {
        return chatGPT;
    }
}