package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.UI.ChatPanel;
import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.MenuPanel;

import de.MCmoderSD.objects.AudioFile;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.HTTP.AudioBroadcast;
import de.MCmoderSD.OpenAI.OpenAI;

import java.util.HashSet;
import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ChatGPT {

    // Associations
    private final OpenAI openAI;
    private final Chat chat;
    private final Image image;
    private final Speech speech;
    private final AudioBroadcast audioBroadcast;

    // Configuration
    private final String botName;
    private final JsonNode config;
    private final JsonNode chatConfig;
    private final JsonNode imageConfig;
    private final JsonNode speechConfig;

    // Chat
    private final String chatModel;
    private final int maxConversationCalls;
    private final int maxTokenSpendingLimit;
    private final double temperature;
    private final int maxTokens;
    private final double topP;
    private final double frequencyPenalty;
    private final double presencePenalty;
    private final String instruction;

    // Image
    private final String imageModel;
    private final String quality;
    private final String resolution;
    private final String style;

    // Speech
    private final String ttsModel;
    private final String voice;
    private final double speed;
    private final String format;


    public ChatGPT() {

        // Init Associations
        audioBroadcast = new AudioBroadcast("localhost", 8000);
        audioBroadcast.registerBrodcast("");

        // Load Configuration
        JsonUtility jsonUtility = new JsonUtility();
        botName = "YEPPBot";
        config = jsonUtility.load("/ChatGPT.json");
        chatConfig = config.get("chat");
        imageConfig = config.get("image");
        speechConfig = config.get("speech");
        openAI = new OpenAI(config);

        // Set Associations
        chat = openAI.getChat();
        image = openAI.getImage();
        speech = openAI.getSpeech( );

        // Chat Configuration
        chatModel = chatConfig.get("chatModel").asText();
        maxConversationCalls = chatConfig.get("maxConversationCalls").asInt();
        maxTokenSpendingLimit = chatConfig.get("maxTokenSpendingLimit").asInt();
        temperature = chatConfig.get("temperature").asDouble();
        maxTokens = chatConfig.get("maxTokens").asInt();
        topP = chatConfig.get("topP").asDouble();
        frequencyPenalty = chatConfig.get("frequencyPenalty").asDouble();
        presencePenalty = chatConfig.get("presencePenalty").asDouble();
        instruction = chatConfig.get("instruction").asText();

        // Image Configuration
        imageModel = imageConfig.get("imageModel").asText();
        quality = imageConfig.get("quality").asText();
        resolution = imageConfig.get("resolution").asText();
        style = imageConfig.get("style").asText();

        // Speech Configuration
        ttsModel = speechConfig.get("ttsModel").asText();
        voice = speechConfig.get("voice").asText();
        speed = speechConfig.get("speed").asDouble();
        format = speechConfig.get("format").asText();

        // Print Welcome Message
        System.out.println(BOLD);
        System.out.println("Welcome to ChatGPT!\n");
        System.out.println("Bot Name: " + botName);
        System.out.println("Chat Model: " + chatModel);
        System.out.println("Image Model: " + imageModel);
        System.out.println("TTS Model: " + ttsModel);
        System.out.println(UNBOLD);

        Scanner scanner = new Scanner(System.in);
        ttsLoop(scanner);
    }

    private void promptLoop(Scanner scanner) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s", BOLD, input.length(), BREAK);
            System.out.printf("Tokens: %s%s", Chat.calculateTokens(input), BREAK);
            System.out.printf("Cost: %s%s%s%s", chat.calculatePromptCost(input), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = chat.prompt(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s", Chat.calculateTokens(response), BREAK);
            System.out.printf("Cost: %s%s", chat.calculatePromptCost(response), BREAK);
            System.out.printf("Total Cost: %s%s%s%s", chat.calculatePromptCost(input, response), UNBOLD, BREAK, BREAK);
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
            System.out.printf("Tokens: %s%s", Chat.calculateTokens(input), BREAK);
            System.out.printf("Cost: %s%s%s%s", chat.calculatePromptCost(input), UNBOLD, BREAK, BREAK);


            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = chat.converse(id, maxConversationCalls, maxTokenSpendingLimit, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);
            AudioFile audioFile = speech.tts(response, voice, format, speed);
            audioFile.play();

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s", Chat.calculateTokens(response), BREAK);
            System.out.printf("Tokens Used: %s%s", chat.getConversationTokens(id), BREAK);
            System.out.printf("Cost: %s%s", chat.calculateConverationCost(id), BREAK);
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
        chat.promptStream(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty).forEach(chunk -> chatPanel.append(Chat.getContent(chunk)));

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
        chat.converseStream(id, maxConversationCalls, maxTokenSpendingLimit, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty).forEach(chunk -> chatPanel.append(Chat.getContent(chunk)));

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
            System.out.println("Cost: " + speech.calculatePrice(input));

            // Get TTS
            AudioFile audioFile = speech.tts(input, voice, format, speed);
            audioBroadcast.play("mcmodersd", audioFile);
        }
    }

    public void imageLoop(Scanner scanner) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s", BOLD, input.length(), BREAK);

            // Get Image
            HashSet<String> result = image.generate(botName, input, 1, resolution);
            result.forEach(System.out::println);
        }
    }
}