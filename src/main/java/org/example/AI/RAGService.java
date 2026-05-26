package org.example.AI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RAGService {

    @Autowired
    private RetrievalService retrievalService;

    @Autowired
    private LangChain4j langChain4j;

    private static final String SYSTEM_PROMPT =
            "你是一个基于知识库的问答助手。请根据以下参考资料回答用户问题。\n" +
            "如果资料中有答案，请基于资料回答，并在末尾标注引用来源。\n" +
            "如果资料中没有相关信息，请如实告知'资料中未找到相关信息'，不要编造。\n\n" +
            "参考资料：\n{context}";

    public String ask(String question) {
        List<String> chunks = retrievalService.search(question);
        if (chunks.isEmpty()) return "未找到相关文档，请先上传资料。";

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            context.append("[").append(i + 1).append("] ").append(chunks.get(i)).append("\n\n");
        }

        String systemPrompt = SYSTEM_PROMPT.replace("{context}", context.toString());
        return langChain4j.chatWithSystem(systemPrompt, question);
    }
}
