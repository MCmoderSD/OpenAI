package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.other.OpenAi;

import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class ChatGPT {

    // Constants
    private final OpenAi openAI;

    public ChatGPT() {
        JsonUtility jsonUtility = new JsonUtility();
        JsonNode config = jsonUtility.load("/ChatGPT.json");


        // Custom Instructions
        String instruction = "";

        // Create OpenAI instance
        String botName = "YEPPBot";
        instruction = config.get("instruction").asText();
        String model = config.get("chatModel").asText();
        int maxConversationCalls = config.get("maxConversationCalls").asInt();
        int maxTokensPerConversation = config.get("maxTokensPerConversation").asInt();
        double temperature = config.get("temperature").asDouble();
        int maxTokens = config.get("maxTokens").asInt();
        double topP = config.get("topP").asDouble();
        double frequencyPenalty = config.get("frequencyPenalty").asDouble();
        double presencePenalty = config.get("presencePenalty").asDouble();
        openAI = new OpenAi(config);

        // Print Welcome Message
        System.out.println(BOLD);
        System.out.println("Welcome to ChatGPT!\n");
        System.out.println("Bot Name: " + botName);
        System.out.println("Instruction: " + instruction);
        System.out.println("Model: " + model);
        System.out.println("Max Conversation Calls: " + maxConversationCalls);
        System.out.println("Max Tokens Per Conversation: " + maxTokensPerConversation);
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
        conversationLoop(scanner, maxTokensPerConversation, botName, instruction, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);
    }

    private void promptLoop(Scanner scanner, String botName, String instruction, double temperature, int maxTokens, double topP, double frequencyPenalty, double presencePenalty) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s%s%s", BOLD, input.length(), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.prompt(botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s%s%s", BOLD, response.length(), UNBOLD, BREAK, BREAK);
        }
    }

    private void conversationLoop(Scanner scanner, int maxTokensPerConversation, String botName, String instruction, double temperature, int maxTokens, double topP, double frequencyPenalty, double presencePenalty) {

        var id = 1;

        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s%s%s", BOLD, input.length(), UNBOLD, BREAK, BREAK);


            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            String response = openAI.converse(id, maxTokensPerConversation, botName, instruction, input, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Print response
            System.out.println(formatOpenAiResponse(response, "YEPP"));
            System.out.printf("%sChars: %s%s", BOLD, response.length(), BREAK);
            System.out.printf("%sTokens: %s%s", BOLD, openAI.getConversationTokens(id), BREAK);
            System.out.printf("Cost: %s%s%s%s", OpenAi.ChatModel.GPT_4O_MINI_2024_07_18.calculateCost(openAI.getConversationTokens(id)), UNBOLD, BREAK, BREAK);
        }
    }
}
