package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class SpringbootApplication
{
    public static void main(String[] args)
    {
        ApplicationContext context = SpringApplication.run(SpringbootApplication.class,args);
    }
}
