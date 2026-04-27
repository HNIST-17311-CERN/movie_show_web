package com.liujunming.Enity;

import org.example.Entity.LoginUser;
import org.example.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LoginUserTest
{

    @Test
    public void testLoginUser()
    {
        // 1. 构造用户
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");

        // 2. 构造权限
        List<String> permissions = Arrays.asList("system:user:list", "system:user:add");

        // 3. 创建LoginUser
        LoginUser loginUser = new LoginUser(user, permissions);

        // 4. 输出用户信息
        System.out.println("====== 用户信息测试 ======");
        System.out.println("用户名: " + loginUser.getUsername());
        System.out.println("密码: " + loginUser.getPassword());

        // 5. 获取权限
        Collection<? extends GrantedAuthority> authorities = loginUser.getAuthorities();

        System.out.println("====== 权限信息测试 ======");
        for (GrantedAuthority auth : authorities)
        {
            System.out.println("权限: " + auth.getAuthority());
        }

        // 6. 输出状态信息
        System.out.println("====== 账户状态 ======");
        System.out.println("账户是否过期: " + loginUser.isAccountNonExpired());
        System.out.println("账户是否锁定: " + loginUser.isAccountNonLocked());
        System.out.println("凭证是否过期: " + loginUser.isCredentialsNonExpired());
        System.out.println("是否启用: " + loginUser.isEnabled());

        System.out.println("====== 测试结束 ======");
    }
}