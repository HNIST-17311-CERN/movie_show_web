package com.liujunming.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class HelloControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    // ===================== 1. 登录 + 2. 访问 =====================
    @Test
    public void testFullChain() throws Exception
    {
        System.out.println("====== 1. 登录 ======");

        User user = new User();
        user.setUsername("cern");
        user.setPassword("8373558");

        String loginJson = objectMapper.writeValueAsString(user);

        MvcResult loginResult = mockMvc.perform(
                post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        ).andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        System.out.println("登录返回: " + loginResponse);

        // 解析token
        Map map = objectMapper.readValue(loginResponse, Map.class);
        Map data = (Map) map.get("data");
        token = (String) data.get("token");

        System.out.println("Token: " + token);

        System.out.println("====== 2. 访问 hello ======");

        MvcResult helloResult = mockMvc.perform(
                get("/api/hello")
                        .header("token", token)   // ❗注意：不是 Authorization
        ).andReturn();

        String helloResponse = helloResult.getResponse().getContentAsString();

        System.out.println("返回: " + helloResponse);
    }
}