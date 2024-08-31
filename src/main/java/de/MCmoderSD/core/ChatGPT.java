package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.UI.ChatPanel;
import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.MenuPanel;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.other.AudioFile;
import de.MCmoderSD.utilities.other.OpenAi;

import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

@SuppressWarnings("unused")
public class ChatGPT {

    // Associations
    private final OpenAi openAI;

    // Configuration
    private final String botName;
    private final String voice;
    private final double speed;
    private final String format;
    private final int maxConversationCalls;
    private final int maxTokenSpendingLimit;
    private final double temperature;
    private final int maxTokens;
    private final double topP;
    private final double frequencyPenalty;
    private final double presencePenalty;
    private final String instruction;


    public ChatGPT() {

        // Load Configuration
        JsonUtility jsonUtility = new JsonUtility();
        JsonNode config = jsonUtility.load("/ChatGPT.json");
        openAI = new OpenAi(config);

        // Constants
        botName = "YEPPBot";
        String chatModel = config.get("chatModel").asText();
        String ttsModel = config.get("ttsModel").asText();
        voice = config.get("voice").asText();
        speed = config.get("speed").asDouble();
        format = config.get("format").asText();
        maxConversationCalls = config.get("maxConversationCalls").asInt();
        maxTokenSpendingLimit = config.get("maxTokenSpendingLimit").asInt();
        temperature = config.get("temperature").asDouble();
        maxTokens = config.get("maxTokens").asInt();
        topP = config.get("topP").asDouble();
        frequencyPenalty = config.get("frequencyPenalty").asDouble();
        presencePenalty = config.get("presencePenalty").asDouble();
        instruction = config.get("instruction").asText();

        // Print Welcome Message
        System.out.println(BOLD);
        System.out.println("Welcome to ChatGPT!\n");
        System.out.println("Bot Name: " + botName);
        System.out.println("Chat Model: " + chatModel);
        System.out.println("TTS Model: " + ttsModel);
        System.out.println("Voice: " + voice);
        System.out.println("Speed: " + speed);
        System.out.println("Format: " + format);
        System.out.println("Max Conversation Calls: " + maxConversationCalls);
        System.out.println("Max Token Spending Limit: " + maxTokenSpendingLimit);
        System.out.println("Temperature: " + temperature);
        System.out.println("Max Tokens: " + maxTokens);
        System.out.println("Top P: " + topP);
        System.out.println("Frequency Penalty: " + frequencyPenalty);
        System.out.println("Presence Penalty: " + presencePenalty);
        System.out.println("Instruction: " + instruction);
        System.out.println(UNBOLD);

        Scanner scanner = new Scanner(System.in);
    }

    private void promptLoop(Scanner scanner) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s", BOLD, input.length(), BREAK);
            System.out.printf("Tokens: %s%s", OpenAi.calculateTokens(input), BREAK);
            System.out.printf("Cost: %s%s%s%s", openAI.calculatePromptCost(input), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.prompt(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s", OpenAi.calculateTokens(response), BREAK);
            System.out.printf("Cost: %s%s", openAI.calculatePromptCost(response), BREAK);
            System.out.printf("Total Cost: %s%s%s%s", openAI.calculatePromptCost(input, response), UNBOLD, BREAK, BREAK);
        }
    }

    private void conversationLoop(Scanner scanner) {

        var id = 1;

        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s", BOLD, input.length(), BREAK);
            System.out.printf("Tokens: %s%s", OpenAi.calculateTokens(input), BREAK);
            System.out.printf("Cost: %s%s%s%s", openAI.calculatePromptCost(input), UNBOLD, BREAK, BREAK);


            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.converse(id, maxConversationCalls, maxTokenSpendingLimit, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);
            AudioFile audioFile = openAI.tts(response, voice, format, speed);
            audioFile.play();

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s", OpenAi.calculateTokens(response), BREAK);
            System.out.printf("Tokens Used: %s%s", openAI.getConversationTokens(id), BREAK);
            System.out.printf("Cost: %s%s", openAI.calculateConverationCost(id), BREAK);
        }
    }

    public void promptStream(Frame frame, String input) {

        // Associations
        ChatPanel chatPanel = frame.getChatPanel();
        MenuPanel menuPanel = frame.getMenuPanel();

        // Set Standby
        menuPanel.setStandby(true);

        // Format
        chatPanel.append(BREAK + BREAK + "You: " + BREAK);
        chatPanel.append(input + BREAK);

        // Print response
        chatPanel.append(BREAK + "ChatGPT: " + BREAK);
        openAI.promptStream(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty).forEach(chunk -> chatPanel.append(OpenAi.getContent(chunk)));

        // Set Standby
        menuPanel.setStandby(false);
    }

    public void conversationStream(Frame frame, String input) {

        // Variables
        var id = 1;

        // Associations
        ChatPanel chatPanel = frame.getChatPanel();
        MenuPanel menuPanel = frame.getMenuPanel();

        // Set Standby
        menuPanel.setStandby(true);

        // Format
        chatPanel.append(BREAK + BREAK + "You: " + BREAK);
        chatPanel.append(input + BREAK);

        // Print response
        chatPanel.append(BREAK + "ChatGPT: " + BREAK);
        openAI.converseStream(id, maxConversationCalls, maxTokenSpendingLimit, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty).forEach(chunk -> chatPanel.append(OpenAi.getContent(chunk)));

        // Set Standby
        menuPanel.setStandby(false);
    }

    public void ttsLoop(Scanner scanner) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s", BOLD, input.length(), BREAK);
            System.out.println("Cost: " + openAI.calculateTtsCost(input));

            // Get TTS
            AudioFile audioFile = openAI.tts(input, voice, format, speed);
            audioFile.play();
        }
    }
}