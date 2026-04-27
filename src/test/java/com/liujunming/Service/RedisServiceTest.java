package com.liujunming.Service;

import org.example.Service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisServiceTest
{

    @Autowired
    private RedisService redisService;

    @Test
    public void testSetAndGet()
    {
        redisService.setValue("test:key", "hello redis");

        Object value = redisService.getValue("test:key");

        System.out.println("====== Redis测试 ======");
        System.out.println("value: " + value);
    }

    @Test
    public void testExpire()
    {
        redisService.setValueWithExpiry(
                "token:test",
                "abcdefg",
                10,
                TimeUnit.SECONDS
        );

        Object value = redisService.getValue("token:test");

        System.out.println("====== 过期测试 ======");
        System.out.println("token: " + value);
    }

    @Test
    public void testDelete()
    {
        redisService.setValue("temp:key", "123");

        redisService.deleteValue("temp:key");

        Object value = redisService.getValue("temp:key");

        System.out.println("====== 删除测试 ======");
        System.out.println("value: " + value);
    }
}