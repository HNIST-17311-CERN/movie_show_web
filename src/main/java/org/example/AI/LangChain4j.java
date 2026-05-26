package org.example.AI;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class LangChain4j {

    private OpenAiChatModel chatModel;

    @PostConstruct
    public void init() {
        String apiKey = System.getenv("DEEPSEEK_API_KEY");
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(apiKey)
                .modelName("deepseek-v4-pro")
                .build();
    }

    public String chat(String userMessage) {
        return chatModel.chat(UserMessage.from(userMessage)).aiMessage().text();
    }

    public String chatWithSystem(String systemPrompt, String userMessage) {
        return chatModel.chat(
                SystemMessage.from(systemPrompt),
                UserMessage.from(userMessage)
        ).aiMessage().text();
    }
}
