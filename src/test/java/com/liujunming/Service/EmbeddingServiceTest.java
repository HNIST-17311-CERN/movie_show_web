package com.liujunming.Service;

import org.example.AI.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    public void testEmbed() {
        System.out.println("=== 测试 Embedding ===");
        float[] vector = embeddingService.embed("你好，世界");
        if (vector.length > 0) {
            System.out.println("Embedding 成功！向量维度: " + vector.length);
            System.out.println("前 5 个值: ");
            for (int i = 0; i < Math.min(5, vector.length); i++) {
                System.out.printf("  [%d] %.6f%n", i, vector[i]);
            }
        } else {
            System.err.println("Embedding 失败，返回空数组");
        }
    }
}
