package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.utilities.json.JsonUtility;
import de.MCmoderSD.utilities.other.OpenAi;

import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class DALLE {

    // Constants
    private final OpenAi openAI;

    public DALLE() {

        // Get Config
        JsonUtility jsonUtility = new JsonUtility();
        JsonNode config = jsonUtility.load("/ChatGPT.json");

        // Create OpenAI instance
        String botName = "YEPPBot";
        String model = config.get("imageModel").asText();
        openAI = new OpenAi(config);

        // Print Welcome Message
        System.out.println(BOLD);
        System.out.println("Welcome to DALL-E!\n");
        System.out.println("Bot Name: " + botName);
        System.out.println("Model: " + model);
        System.out.println(UNBOLD);

        Scanner scanner = new Scanner(System.in);
        loop(scanner, botName);
    }

    private void loop(Scanner scanner, String botName) {
        while (this.openAI.isActive()) {

            // Format
            System.out.printf("%sYou: %s%s", BOLD, UNBOLD, BREAK);

            // Get user input
            String input = scanner.nextLine();
            System.out.printf("%sChars: %s%s%s%s", BOLD, input.length(), UNBOLD, BREAK, BREAK);

            // Get response
            System.out.printf("%sBot: %s%s", BOLD, UNBOLD, BREAK);
            //String response = openAI.createImage(input, STANDART, SD, SD);

            // Print response
            //System.out.println(response);
        }
    }
}
