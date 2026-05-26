package org.example.AI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RAGController {

    @Autowired
    private RAGService ragService;

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> body) {
        String question = body.getOrDefault("question", "");
        if (question.isEmpty()) return Map.of("answer", "问题不能为空");

        String answer = ragService.ask(question);
        return Map.of("answer", answer);
    }
}
