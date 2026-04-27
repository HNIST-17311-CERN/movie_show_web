package com.liujunming.servlet;

import org.example.Entity.User;
import org.example.Entity.ResonseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerTest
{

    @Autowired
    private TestRestTemplate restTemplate;

    // 保存token
    private static String token;

    @Test
    public void testLogin()
    {
        System.out.println("====== 登录测试 ======");

        User user = new User();
        user.setUsername("cern");   // 改成你数据库存在的用户
        user.setPassword("83735558");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<ResonseResult> response =
                restTemplate.postForEntity("/api/user/login", request, ResonseResult.class);

        System.out.println("状态码: " + response.getStatusCode());
        System.out.println("返回数据: " + response.getBody());

        // ⚠️ 这里你data是map，手动取token
        Map data = (Map) response.getBody().getData();
        token = (String) data.get("token");

        System.out.println("提取token: " + token);
    }

    @Test
    public void testHelloWithToken()
    {
        System.out.println("====== 带Token访问测试 ======");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // 自动加 Authorization: Bearer xxx

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange("/api/hello", HttpMethod.GET, request, String.class);

        System.out.println("状态码: " + response.getStatusCode());
        System.out.println("返回: " + response.getBody());
    }

    @Test
    public void testLogout()
    {
        System.out.println("====== 退出测试 ======");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ResonseResult> response =
                restTemplate.exchange("/api/user/logout", HttpMethod.POST, request, ResonseResult.class);

        System.out.println("状态码: " + response.getStatusCode());
        System.out.println("返回: " + response.getBody());
    }
}