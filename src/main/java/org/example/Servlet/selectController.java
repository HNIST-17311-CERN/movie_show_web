package org.example.Servlet;


import org.example.DAO.UserDAO;
import org.example.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/JDBC")
@CrossOrigin(origins = "*")  // 允许所有来源访问
public class selectController
{

    @Autowired
    public UserDAO userDAO;

    @GetMapping("/find/all")
    public List<User> get()
    {
        return userDAO.Find_All();
    }

}
