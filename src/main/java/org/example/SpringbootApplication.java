package org.example;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@MapperScan("org.example.Mapper")
@SpringBootApplication
public class SpringbootApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(SpringbootApplication.class,args);
    }
}
