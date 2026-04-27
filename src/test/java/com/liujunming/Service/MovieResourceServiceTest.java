package com.liujunming.Service;

import org.example.Entity.Movie_Resource;
import org.example.Service.Movie_Resource_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class MovieResourceServiceTest
{

    @Autowired
    private Movie_Resource_Service service;

    @Test
    public void testFindByMovieId()
    {
        List<Movie_Resource> list = service.get_resource_by_id(1L);

        System.out.println("====== Service查询资源 ======");
        for (Movie_Resource r : list)
        {
            System.out.println(r);
        }
    }

    @Test
    public void testInsert()
    {
        Movie_Resource r = new Movie_Resource();
        r.setMovieId(1L);
        r.setName("Service测试资源");
        r.setQuality("4K");
        r.setSize("8GB");
        r.setType("磁力");
        r.setUrl("magnet:?xt=xxxxx");
        r.setCreateTime(LocalDateTime.now());
        r.setSubtitle("中字");

        int rows = service.insert(r);

        System.out.println("====== 插入测试 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testUpdate()
    {
        Movie_Resource r = new Movie_Resource();
        r.setId(1L);
        r.setName("更新后的Service资源");
        r.setQuality("1080P");
        r.setSize("3GB");
        r.setType("网盘");
        r.setUrl("https://xxx.com");
        r.setCreateTime(LocalDateTime.now());
        r.setSubtitle("英文");

        int rows = service.update(r);

        System.out.println("====== 更新测试 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testDelete()
    {
        int rows = service.deleteById(1L);

        System.out.println("====== 删除测试 ======");
        System.out.println("影响行数: " + rows);
    }
}