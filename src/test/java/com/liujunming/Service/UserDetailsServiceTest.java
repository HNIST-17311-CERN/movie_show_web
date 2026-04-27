package com.liujunming.Service;

import org.example.Entity.LoginUser;
import org.example.Service.UserDetailsService_NEW;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class UserDetailsServiceTest
{

    @Autowired
    private UserDetailsService_NEW userDetailsService;

    @Test
    public void testLoadUserByUsername()
    {
        System.out.println("====== 用户认证加载测试 ======");

        // 这里填你数据库真实存在的用户名
        String username = "cern";

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        LoginUser loginUser = (LoginUser) userDetails;

        System.out.println("用户名: " + loginUser.getUsername());
        System.out.println("密码: " + loginUser.getPassword());
        System.out.println("用户ID: " + loginUser.getUser().getId());
        System.out.println("权限列表: " + loginUser.getAuthorities());

        System.out.println("====== 测试结束 ======");
    }
}