package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.UI.ChatPanel;
import de.MCmoderSD.UI.Frame;
import de.MCmoderSD.UI.MenuPanel;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.other.OpenAi;

import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class ChatGPT {

    // Constants
    private final OpenAi openAI;

    // Attributes
    private final int maxConversationCalls;
    private final int maxTokenSpendingLimit;
    private final String botName;
    private final String instruction;
    private final double temperature;
    private final int maxTokens;
    private final double topP;
    private final double frequencyPenalty;
    private final double presencePenalty;


    public ChatGPT() {
        JsonUtility jsonUtility = new JsonUtility();
        JsonNode config = jsonUtility.load("/ChatGPT.json");

        // Create OpenAI instance
        botName = "YEPPBot";
        instruction = config.get("instruction").asText();
        String model = config.get("chatModel").asText();
        maxConversationCalls = config.get("maxConversationCalls").asInt();
        maxTokenSpendingLimit = config.get("maxTokenSpendingLimit").asInt();
        temperature = config.get("temperature").asDouble();
        maxTokens = config.get("maxTokens").asInt();
        topP = config.get("topP").asDouble();
        frequencyPenalty = config.get("frequencyPenalty").asDouble();
        presencePenalty = config.get("presencePenalty").asDouble();
        openAI = new OpenAi(config);

        // Print Welcome Message
        System.out.println(BOLD);
        System.out.println("Welcome to ChatGPT!\n");
        System.out.println("Bot Name: " + botName);
        System.out.println("Instruction: " + instruction);
        System.out.println("Model: " + model);
        System.out.println("Max Conversation Calls: " + maxConversationCalls);
        System.out.println("Max Token Spending Limit: " + maxTokenSpendingLimit);
        System.out.println("Temperature: " + temperature);
        System.out.println("Max Tokens: " + maxTokens);
        System.out.println("Top P: " + topP);
        System.out.println("Frequency Penalty: " + frequencyPenalty);
        System.out.println("Presence Penalty: " + presencePenalty);
        System.out.println(BREAK);
        System.out.println("Token Cost: " + OpenAi.ChatModel.GPT_4O_MINI_2024_07_18.calculateCost(maxTokens));
        System.out.println("Prompt Cost: " + OpenAi.ChatModel.GPT_4O_MINI_2024_07_18.calculateCost(maxTokens));
        System.out.println("Conversation Cost: " + OpenAi.ChatModel.GPT_4O_MINI_2024_07_18.calculateCost(maxTokens));
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
            System.out.printf("Tokens: %s%s%s%s", OpenAi.calculateTokens(input), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.prompt(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s%s%s", OpenAi.calculateTokens(response), UNBOLD, BREAK, BREAK);
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
            System.out.printf("Tokens: %s%s%s%s", OpenAi.calculateTokens(input), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.converse(id, maxConversationCalls, maxTokenSpendingLimit, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("Tokens: %s%s", OpenAi.calculateTokens(response), BREAK);
            System.out.printf("Tokens Used: %s%s", openAI.getConversationTokens(id), BREAK);
            System.out.printf("Cost: %s%s%s%s", OpenAi.ChatModel.GPT_4O_MINI_2024_07_18.calculateCost(openAI.getConversationTokens(id)), UNBOLD, BREAK, BREAK);
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
}
