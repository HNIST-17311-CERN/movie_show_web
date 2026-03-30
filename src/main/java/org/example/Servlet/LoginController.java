package org.example.Servlet;

import org.example.Entity.ResonseResult;
import org.example.Entity.User;
import org.example.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController
{

    @Autowired
    LoginService loginService;

    @PostMapping("/api/user/login")
    @CrossOrigin // 允许跨域
    public ResonseResult login(@RequestBody User user) throws Exception
    {
        ResonseResult rs = new ResonseResult();
        rs=loginService.login(user);
        return rs;
    }


    @RequestMapping("/api/user/logout")
    @CrossOrigin // 允许跨域
    public ResonseResult logout() throws Exception
    {
        return loginService.logout();
    }



}
