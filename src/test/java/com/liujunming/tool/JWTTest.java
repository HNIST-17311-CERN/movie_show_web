package com.liujunming.tool;

import org.example.Tool.JWT_Utils;
import org.junit.jupiter.api.Test;

public class JWTTest
{

    @Test
    public void testJWT() throws Exception
    {
        // 1. 生成token
        String token = JWT_Utils.JWT_Creat(1001L);

        System.out.println("====== JWT生成 ======");
        System.out.println("Token: " + token);

        // 2. 解析token
        String id = JWT_Utils.JWT_Parse(token);

        System.out.println("\n====== JWT解析 ======");
        System.out.println("用户ID: " + id);
    }
}