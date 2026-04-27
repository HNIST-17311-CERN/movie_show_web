package com.liujunming.tool;

import org.example.Tool.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisCacheTest
{

    @Autowired
    private RedisCache redisCache;

    @Test
    public void testRedisSetAndGet()
    {
        // 1. 存数据
        redisCache.setCacheObject("user:1", "admin");

        // 2. 取数据
        String value = redisCache.getCacheObject("user:1");

        System.out.println("====== Redis测试 ======");
        System.out.println("获取值: " + value);
    }

    @Test
    public void testRedisExpire()
    {
        // 带过期时间
        redisCache.setCacheObject(
                "token:123",
                "abcdefg",
                10,
                TimeUnit.SECONDS
        );

        String value = redisCache.getCacheObject("token:123");

        System.out.println("====== 带过期时间测试 ======");
        System.out.println("token: " + value);
    }

}