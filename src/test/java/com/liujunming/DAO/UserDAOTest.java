package com.liujunming.DAO;

import org.example.DAO.UserDAO;
import org.example.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserDAOTest
{

    @Autowired
    private UserDAO userDAO;

    @Test
    public void testFindAll()
    {
        List<User> list = userDAO.Find_All();

        System.out.println("====== 查询所有用户 ======");
        for (User u : list)
        {
            System.out.println("ID: " + u.getId());
            System.out.println("用户名: " + u.getUsername());
            System.out.println("密码: " + u.getPassword());
            System.out.println("邮箱: " + u.getEmail());
            System.out.println("角色: " + u.getRole());
            System.out.println("------------------");
        }
    }

    @Test
    public void testFindByName()
    {
        User user = userDAO.Find_By_Name("admin");

        System.out.println("====== 根据用户名查询 ======");
        System.out.println("ID: " + user.getId());
        System.out.println("用户名: " + user.getUsername());
        System.out.println("密码: " + user.getPassword());
        System.out.println("邮箱: " + user.getEmail());
        System.out.println("角色: " + user.getRole());
    }
}