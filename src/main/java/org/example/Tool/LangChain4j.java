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
        //String apiKey = "sk-2f4d5816aead4dc4a6eaa1b68a5bc116";
        String apiKey = "sk-48a98e4a66c341378f6abbc46628c63e";
//        OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
//                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
//                .apiKey(apiKey)
//                .modelName("qwen-vl-plus")
//                .build();
//
//        UserMessage userMessage = UserMessage.from(
//                TextContent.from("这是什么"),
//                ImageContent.from("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg")
//        );

                OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(apiKey)
                .modelName("deepseek-v4-pro")
                .build();

        UserMessage userMessage = UserMessage.from(
                TextContent.from("h")
                //ImageContent.from("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg")
        );

        return aiChatModel.chat(userMessage);
    }
}
