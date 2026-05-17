package org.example.Tool;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class LangChain4j
{
    public ChatResponse Langcain()
    {


        return aiChatModel.chat(userMessage);
    }
}
