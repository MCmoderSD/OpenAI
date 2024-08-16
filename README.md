# OpenAI Utility

This is a java Utility for the OpenAI API Services. <br>
It uses the [OpenAi Library](https://github.com/TheoKanning/openai-java)
from [TheoKanning](https://github.com/TheoKanning/) which is sadly not maintained anymore.

## Usecase

This Utility is used to interact with the OpenAI API Services. <br>
It is beeing developed to be used in my [YEPPBot - Twitch Chatbot](https://github.com/MCmoderSD/YEPPBot/). <br>
But should work in any other Java Project as well. <br>

Currently, it supports simple prompts and chat conversations. <br>
But I am planning to add more features in the future. <br>

## Usage

After you have [configured](#Configuration) the Utility it is very easy to use. <br>

You create a new instance of the OpenAi Utility and give it the config.json as JsonNode. <br>
Then you can use the methods prompt and converse Methods to interact with the OpenAI API. <br>

## Configuration

To use this Utility you need an OpenAi API Key and a config.json file in the resources folder. <br>
The config.json file should look like this:

```json
{
  "apiKey": "YOUR_API_KEY",
  "chatModel": "gpt-4o-mini-2024-07-18",
  "maxConversationCalls": 10,
  "maxTokensPerConversation": 16383,
  "temperature": 1,
  "maxTokens": 120,
  "topP": 1,
  "frequencyPenalty": 0,
  "presencePenalty": 0,
  "instruction": "You are the best TwitchBot that ever existed!"
}
```

You can get the API key from [OpenAI](https://platform.openai.com/signup). <br>

- The **model** is the model that the bot will use to generate the text. <br>
  The available models are: <br>

| **Model**              | **Pricing**                                               | 
|:-----------------------|:----------------------------------------------------------|
| gpt-4o                 | $5.00 / 1M input tokens <br/> \$15.00 / 1M output tokens  |
| gpt-4o-2024-08-06      | $2.50 / 1M input tokens <br/> \$10.00 / 1M output tokens  |
| gpt-4o-2024-05-13      | $5.00 / 1M input tokens <br/> \$15.00 / 1M output tokens  |
| gpt-4o-mini            | $0.150 / 1M input tokens <br/> \$0.600 / 1M output tokens |
| gpt-4o-mini-2024-07-18 | $0.150 / 1M input tokens <br/> \$0.600 / 1M output tokens |

- The **maxTokensPerConversation** is the limit for the conversation. <br>
  After the limit is reached, the conversation will end. <br>
  You can set the limit to overall max limit of 16383 tokens. <br>


- The **temperature** is the randomness of the text. <br>
  Lowering results in less random completions. As the temperature approaches zero, the model will become deterministic
  and repetitive. <br>
  Higher temperature results in more random completions. <br>
  The min value is 0 and the max value is 2. <br>


- The **maxTokens** is the maximum length of the response text. <br>
  One token is roughly 4 characters for standard English text. <br>
  The limit is 16383 tokens, but it's recommended to use a value that is suitable for the use, on Twitch the message
  limit is 500 characters.
  If you divide the limit by 4, you an estimate the number of characters. <br>


- The **topP** is the nucleus sampling. <br>
  The lower the value, the more plain the text will be. <br>
  The higher the value, the more creative the text will be. <br>
  The min value is 0 and the max value is 1. <br>


- The **frequencyPenalty** reduces the likelihood of repeating the same words in a response.
  The higher the value, the less the bot will repeat itself. <br>
  The min value is 0 and the max value is 1. <br>


- The **presencePenalty** reduces the likelihood of mentioning words that have already appeared in the
  conversation. <br>
  The higher the value, the less the bot will repeat itself. <br>
  The min value is 0 and the max value is 1. <br>


- The **instruction** is the way the bot should behave and how he should reply to the prompt.