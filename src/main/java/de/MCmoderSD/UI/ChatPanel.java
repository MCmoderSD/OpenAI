package de.MCmoderSD.UI;

import de.MCmoderSD.utilities.frontend.RoundedTextArea;

import javax.swing.*;
import java.awt.*;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class ChatPanel extends JPanel {

    // Associations
    private final Frame frame;

    // Attributes
    private final RoundedTextArea chatArea;

    // Constructor
    public ChatPanel(Frame frame, Dimension size) {

        // Init Panel
        super();
        setLayout(null);
        setBackground(DARK);
        setForeground(PURPLE);

        // Set Frame
        this.frame = frame;

        // Set Size
        var height = Math.toIntExact(Math.round(size.height * 0.9));
        Dimension panelSize = new Dimension(size.width, height);
        setPreferredSize(panelSize);

        // Variables
        var fontSize = panelSize.width / 50;
        var padding = panelSize.width / 100;

        // Font
        Font font = new Font("Roboto", Font.PLAIN, fontSize);

        // Log Area
        chatArea = new RoundedTextArea();
        chatArea.setBounds(padding, padding, panelSize.width - 2 * padding, panelSize.height - 2 * padding);
        chatArea.setBackground(LIGHT);
        chatArea.setForeground(WHITE);
        chatArea.setFont(font);
        add(chatArea);

        // Add to Frame
        frame.add(this, BorderLayout.NORTH);
        frame.pack();
    }

    public void append(String response) {
        chatArea.appendText(response);
    }

    public void clear() {
        chatArea.removeAll();
    }
}