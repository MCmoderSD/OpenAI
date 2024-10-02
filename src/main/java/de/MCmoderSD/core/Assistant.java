package de.MCmoderSD.core;

import com.fasterxml.jackson.databind.JsonNode;
import de.MCmoderSD.OpenAI.OpenAI;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.OpenAI.modules.Transcription;
import de.MCmoderSD.objects.AudioFile;
import de.MCmoderSD.objects.AudioRecorder;
import de.MCmoderSD.utilities.json.JsonUtility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

import static de.MCmoderSD.utilities.other.Calculate.*;

public class Assistant {

    // Associations
    private final OpenAI openAI;
    private final Chat chat;
    private final Speech speech;
    private final Transcription transcription;

    // Chat
    private final int maxConversationCalls;
    private final int maxTokenSpendingLimit;
    private final double temperature;
    private final int maxTokens;
    private final double topP;
    private final double frequencyPenalty;
    private final double presencePenalty;
    private final String instruction;

    // Speech
    private final String voice;
    private final double speed;
    private final String format;

    // Transcription
    private final String prompt;
    private final String language;
    private final double transcriptionTemperature;

    // Constructor
    public Assistant() {

        JsonUtility jsonUtility = new JsonUtility();
        JsonNode config = jsonUtility.load("/ChatGPT.json");
        JsonNode chatConfig = config.get("chat");
        JsonNode transcriptionConfig = config.get("transcription");
        JsonNode speechConfig = config.get("speech");

        // Set Associations
        this.openAI = new OpenAI(config);
        chat = openAI.getChat();
        speech = openAI.getSpeech();
        transcription = openAI.getTranscription();

        // Chat Configuration
        maxConversationCalls = chatConfig.get("maxConversationCalls").asInt();
        maxTokenSpendingLimit = chatConfig.get("maxTokenSpendingLimit").asInt();
        temperature = chatConfig.get("temperature").asDouble();
        maxTokens = chatConfig.get("maxTokens").asInt();
        topP = chatConfig.get("topP").asDouble();
        frequencyPenalty = chatConfig.get("frequencyPenalty").asDouble();
        presencePenalty = chatConfig.get("presencePenalty").asDouble();
        instruction = chatConfig.get("instruction").asText();

        // Transcription Configuration
        prompt = transcriptionConfig.get("prompt").asText();
        language = transcriptionConfig.get("language").asText();
        transcriptionTemperature = transcriptionConfig.get("temperature").asDouble();

        // Speech Configuration
        voice = speechConfig.get("voice").asText();
        speed = speechConfig.get("speed").asDouble();
        format = speechConfig.get("format").asText();

        // Loop
        new Thread(this::loop).start();
    }

    private void loop() {

        // Attributes
        var id = 1;
        Scanner scanner = new Scanner(System.in);
        AudioRecorder recorder = new AudioRecorder();
        BigDecimal totalCost = new BigDecimal(0);

        // Loop
        while (openAI.isActive()) {

            // Wait for user input
            System.out.println(BOLD + "Press enter to start recording");
            scanner.nextLine();

            // Start recording
            recorder.startRecording();
            System.out.println("Recording... Press enter to stop" + UNBOLD);

            // Wait for user input
            scanner.nextLine();

            // Stop and get audio file
            recorder.stopRecording();
            AudioFile userAudio = recorder.getAudioFile();

            // Transcribe audio
            String userInput = transcription.transcribe(userAudio, prompt, language, transcriptionTemperature);

            // Print user input
            System.out.println("User Input: ");
            System.out.println(userInput);

            // Calculate cost
            BigDecimal transcriptionCost = transcription.calculatePrice(userAudio);
            System.out.println("\nTranscription Cost: $" + transcriptionCost.setScale(4, RoundingMode.HALF_UP));
            totalCost = totalCost.add(transcriptionCost);

            // Chat
            String response = chat.converse(id, maxConversationCalls, maxTokenSpendingLimit, "YEPPBot", instruction, userInput, temperature, maxTokens, topP, frequencyPenalty, presencePenalty);

            // Calculate cost
            BigDecimal chatCost = chat.calculateConverationCost(id);
            System.out.println("Chat Cost: $" + chatCost.setScale(9, RoundingMode.HALF_UP));
            totalCost = totalCost.add(chatCost);

            // Text-to-Speech
            AudioFile audioResponse = speech.tts(response, voice, format, speed);

            // Calculate cost
            BigDecimal speechCost = speech.calculatePrice(response);
            System.out.println("Speech Cost: $" + speechCost.setScale(6, RoundingMode.HALF_UP));
            totalCost = totalCost.add(speechCost);

            // Play audio
            System.out.println("\nBot Response: ");
            System.out.println(response);
            System.out.println("\nTotal Cost: $" + totalCost.setScale(9, RoundingMode.HALF_UP));
            System.out.println("\n");
            audioResponse.play();
        }
    }
}