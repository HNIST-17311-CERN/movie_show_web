package com.liujunming.DAO;

import org.example.DAO.Movie_ResourceDAO;
import org.example.Entity.Movie_Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class MovieResourceDAOTest
{

    @Autowired
    private Movie_ResourceDAO dao;

    @Test
    public void testFindByMovieId()
    {
        List<Movie_Resource> list = dao.findByMovieId(1L);

        System.out.println("====== 查询电影资源 ======");
        for (Movie_Resource r : list)
        {
            System.out.println("ID: " + r.getId());
            System.out.println("电影ID: " + r.getMovieId());
            System.out.println("名称: " + r.getName());
            System.out.println("清晰度: " + r.getQuality());
            System.out.println("大小: " + r.getSize());
            System.out.println("类型: " + r.getType());
            System.out.println("地址: " + r.getUrl());
            System.out.println("字幕: " + r.getSubtitle());
            System.out.println("时间: " + r.getCreateTime());
            System.out.println("---------------------");
        }
    }

    @Test
    public void testInsert()
    {
        Movie_Resource r = new Movie_Resource();
        r.setMovieId(1L);
        r.setName("测试资源 4K");
        r.setQuality("4K");
        r.setSize("10GB");
        r.setType("磁力");
        r.setUrl("magnet:?xt=xxxx");
        r.setCreateTime(LocalDateTime.now());
        r.setSubtitle("中字");

        int rows = dao.insert(r);

        System.out.println("====== 插入测试 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testUpdate()
    {
        Movie_Resource r = new Movie_Resource();
        r.setId(1L);
        r.setName("更新后的资源");
        r.setQuality("1080P");
        r.setSize("3GB");
        r.setType("网盘");
        r.setUrl("https://xxx.com");
        r.setCreateTime(LocalDateTime.now());
        r.setSubtitle("英文");

        int rows = dao.update(r);

        System.out.println("====== 更新测试 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testDelete()
    {
        int rows = dao.deleteById(1L);

        System.out.println("====== 删除测试 ======");
        System.out.println("影响行数: " + rows);
    }
}