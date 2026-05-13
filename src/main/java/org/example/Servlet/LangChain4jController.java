package org.example.Servlet;


import dev.langchain4j.model.chat.response.ChatResponse;
import org.example.Tool.LangChain4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/AI")
@CrossOrigin(origins = "*")  // 允许所有来源访问
public class LangChain4jController
{
    @Autowired
    LangChain4j langChain4j;

    @GetMapping("/hello")
    @CrossOrigin // 允许跨域
    public String LangChainHello()
    {
        return  langChain4j.Langcain().aiMessage().text();
    }

}
