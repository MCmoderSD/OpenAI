package de.MCmoderSD.OpenAI;

import com.fasterxml.jackson.databind.JsonNode;
import com.theokanning.openai.service.OpenAiService;
import de.MCmoderSD.OpenAI.enums.ChatModel;
import de.MCmoderSD.OpenAI.enums.ImageModel;
import de.MCmoderSD.OpenAI.enums.TTSModel;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;

@SuppressWarnings("unused")
public class OpenAI {

    // Constants
    private final JsonNode config;

    // Attributes
    private final OpenAiService service;

    // Associations
    private final Chat chat;
    private final Image image;
    private final Speech speech;

    // Enums
    private final ChatModel chatModel;
    private final ImageModel imageModel;
    private final TTSModel ttsModel;

    // Constructor
    public OpenAI(JsonNode config) {

        // Set Config
        this.config = config;

        // Initialize OpenAI Service
        service = new OpenAiService(config.get("apiKey").asText());

        // Set Chat Model
        JsonNode chatConfig = config.get("chat");
        String chatModelName = chatConfig.get("chatModel").asText();
        if (chatModelName == null) throw new IllegalArgumentException("Chat model is null");
        if (chatModelName.isEmpty() || chatModelName.isBlank()) throw new IllegalArgumentException("Chat model is empty");
        chatModel = switch (chatModelName) {
            case "gpt-4o" -> ChatModel.GPT_4O;
            case "gpt-4o-2024-08-06" -> ChatModel.GPT_4O_2024_08_06;
            case "gpt-4o-2024-05-13" -> ChatModel.GPT_4O_2024_05_13;
            case "gpt-4o-mini" -> ChatModel.GPT_4O_MINI;
            case "gpt-4o-mini-2024-07-18" -> ChatModel.GPT_4O_MINI_2024_07_18;
            default -> throw new IllegalArgumentException("Invalid chat model");
        };

        // Set Image Model
        JsonNode imageConfig = config.get("image");
        String imageModelName = imageConfig.get("imageModel").asText();
        if (imageModelName == null) throw new IllegalArgumentException("Image model is null");
        if (imageModelName.isEmpty() || imageModelName.isBlank()) throw new IllegalArgumentException("Image model is empty");
        imageModel = switch (imageModelName) {
            case "dall-e-2" -> ImageModel.DALL_E_2;
            case "dall-e-3" -> ImageModel.DALL_E_3;
            default -> throw new IllegalArgumentException("Invalid image model");
        };

        // Set TTS Model
        JsonNode ttsConfig = config.get("speech");
        String ttsModelName = ttsConfig.get("ttsModel").asText();
        if (ttsModelName == null) throw new IllegalArgumentException("TTS model is null");
        if (ttsModelName.isEmpty() || ttsModelName.isBlank()) throw new IllegalArgumentException("TTS model is empty");
        ttsModel = switch (ttsModelName) {
            case "tts-1" -> TTSModel.TTS;
            case "tts-1-hd" -> TTSModel.TTS_HD;
            default -> throw new IllegalArgumentException("Invalid TTS model");
        };

        // Initialize Associations
        chat = new Chat(chatModel, service);
        image = new Image(imageModel, service);
        speech = new Speech(ttsModel, service);
    }

    // Getter
    public JsonNode getConfig() {
        return config;
    }

    public boolean isActive() {
        return service != null;
    }

    public OpenAiService getService() {
        return service;
    }

    public Chat getChat() {
        return chat;
    }

    public Image getImage() {
        return image;
    }

    public Speech getSpeech() {
        return speech;
    }

    // Enums
    public ChatModel getChatModel() {
        return chatModel;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }

    public TTSModel getTtsModel() {
        return ttsModel;
    }
}