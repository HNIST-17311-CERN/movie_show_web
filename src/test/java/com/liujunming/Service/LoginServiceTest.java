package org.example;

import org.example.Entity.User;
import org.example.Service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class LoginServiceTest
{

    @Autowired
    private LoginService loginService;

    @Test
    public void testLogin() throws Exception
    {
        User user = new User();
        user.setUsername("cern");
        user.setPassword("8373558");

        System.out.println("====== 登录测试 ======");

        var result = loginService.login(user);

        System.out.println("code: " + result.getCode());
        System.out.println("msg: " + result.getMsg());

        Map<String, String> data = (Map<String, String>) result.getData();

        System.out.println("token: " + data.get("token"));
    }
}