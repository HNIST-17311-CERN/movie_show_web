package com.liujunming.servlet;

import org.example.Entity.Movie_Resource;
import org.example.Entity.Movie_Score;
import org.example.Entity.Movie_details;
import org.example.Entity.User;
import org.example.Entity.ResonseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String token;

    // ===================== 1. 登录 =====================
    @Test
    public void login() {

        System.out.println("====== 登录获取token ======");

        User user = new User();
        user.setUsername("cern");
        user.setPassword("8373558");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<ResonseResult> resp =
                restTemplate.postForEntity("/api/user/login", request, ResonseResult.class);

        System.out.println("登录返回: " + resp.getBody());

        Map data = (Map) resp.getBody().getData();
        token = (String) data.get("token");

        System.out.println("token = " + token);
    }

    // ===================== 2. ALL =====================
    @Test
    public void testAllMovies() {

        login();

        System.out.println("====== /FILMES/ALL ======");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/FILMES/ALL",
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(resp.getBody());
    }

    // ===================== 3. ONEID =====================
    @Test
    public void testOneMovie() {

        login();

        System.out.println("====== /FILMES/ONEID ======");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/FILMES/ONEID?id=1",
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(resp.getBody());
    }

    // ===================== 4. SCORE =====================
    @Test
    public void testScore() {

        login();

        System.out.println("====== /FILMES/SCORE/ONE ======");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/FILMES/SCORE/ONE?id=1",
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(resp.getBody());
    }

    // ===================== 5. RESOURCE =====================
    @Test
    public void testResource() {

        login();

        System.out.println("====== /FILMES/RESOURCE/ONE ======");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/FILMES/RESOURCE/ONE?id=1",
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(resp.getBody());
    }

    // ===================== 6. DELETE =====================
    @Test
    public void testDeleteMovie() {

        login();

        System.out.println("====== DELETE MOVIE ======");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/FILMES/DELETE?id=1",
                HttpMethod.POST,
                request,
                String.class
        );

        System.out.println(resp.getBody());
    }
}