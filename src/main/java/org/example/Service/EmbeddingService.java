package org.example.Service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    @Value("${QWEN_API_KEY:#{systemEnvironment['QWEN_API_KEY']}}")
    private String apiKey;

    private EmbeddingModel embeddingModel;

    @PostConstruct
    public void init()
    {
        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("text-embedding-v3")
                .dimensions(1024)
                .build();
    }

    public float[] embed(String text)
    {
        Embedding embedding = embeddingModel.embed(text).content();
        float[] result = embedding.vector();
        System.out.println("Embedding 成功！维度: " + result.length);
        return result;
    }
}
