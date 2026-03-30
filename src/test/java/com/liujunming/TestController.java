package com.liujunming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class TestController {

    @Autowired
    DataSource dataSource;

    @GetMapping("/testdb")
    public String test() throws Exception {
        System.out.println(dataSource.getConnection());
        return "ok";
    }
}