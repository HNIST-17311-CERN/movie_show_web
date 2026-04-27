package com.liujunming.Enity;

import org.example.Entity.ResonseResult;
import org.junit.jupiter.api.Test;

public class ResponseResultTest
{

    @Test
    public void testResponseResult()
    {
        // 1. 模拟成功返回数据
        ResonseResult<String> success = new ResonseResult<>(
                200,
                "success",
                "登录成功"
        );

        // 2. 模拟失败返回
        ResonseResult<String> error = new ResonseResult<>(
                500,
                "error",
                "系统异常"
        );

        // 3. 输出
        System.out.println("====== 成功响应 ======");
        System.out.println("code: " + success.getCode());
        System.out.println("msg: " + success.getMsg());
        System.out.println("data: " + success.getData());

        System.out.println("\n====== 失败响应 ======");
        System.out.println("code: " + error.getCode());
        System.out.println("msg: " + error.getMsg());
        System.out.println("data: " + error.getData());

        // 4. 模拟接口返回JSON结构
        System.out.println("\n====== JSON模拟 ======");
        System.out.println(success);
    }
}