package com.liujunming.AI;

import org.example.AI.RAGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServiceTest {

    @Autowired
    private RAGService ragService;

    @Test
    public void testAsk() {
        System.out.println("=== 测试 RAG 问答 ===");
        String answer = ragService.ask("你好");
        System.out.println("回答: " + answer);
    }
}
