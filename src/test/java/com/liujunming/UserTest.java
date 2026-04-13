package com.liujunming;

import org.example.Entity.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testSetterAndGetter() {
        User user = new User();

        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("123456");
        user.setEmail("admin@test.com");
        user.setRole("ADMIN");

        // 👇 输出测试数据
        System.out.println("===== testSetterAndGetter =====");
        System.out.println(user);

        assertEquals(1L, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals("admin@test.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void testAllArgsConstructor() {
        User user = new User(2L, "user1", "pwd", "user@test.com", "USER");

        // 👇 输出测试数据
        System.out.println("===== testAllArgsConstructor =====");
        System.out.println(user);

        assertEquals(2L, user.getId());
        assertEquals("user1", user.getUsername());
        assertEquals("pwd", user.getPassword());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("USER", user.getRole());
    }

    @Test
    public void testToString() {
        User user = new User(3L, "test", "pass", "test@test.com", "USER");

        String result = user.toString();

        // 👇 输出测试数据
        System.out.println("===== testToString =====");
        System.out.println(result);

        assertTrue(result.contains("id=3"));
        assertTrue(result.contains("username='test'"));
        assertTrue(result.contains("email='test@test.com'"));
        assertTrue(result.contains("role='USER'"));
    }
}