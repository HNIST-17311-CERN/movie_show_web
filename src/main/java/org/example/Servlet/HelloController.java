package org.example.Servlet;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController
{
    @GetMapping("/hello")
    @CrossOrigin // 允许跨域
    //@PreAuthorize("hasAuthority('test') or hasAuthority('admin')")
    public String hello()
    {
        System.out.println("HELLO CONTROLLER ENTER");
        return "hello world";
    }


}
