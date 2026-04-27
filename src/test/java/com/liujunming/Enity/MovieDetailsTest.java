package com.liujunming.Enity;

import org.example.Entity.Movie_details;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class MovieDetailsTest
{

    @Test
    public void testMovieDetails()
    {
        // 1. 创建对象
        Movie_details movie = new Movie_details();

        // 2. 设置数据
        movie.setId(1L);
        movie.setName("流浪地球");
        movie.setCover("http://example.com/cover.jpg");
        movie.setDirector("郭帆");
        movie.setActors("吴京,屈楚萧,李光洁");
        movie.setType("科幻");
        movie.setRegion("中国大陆");
        movie.setLanguage("国语");
        movie.setReleaseDate(new Date());
        movie.setDuration(125);
        movie.setDescription("太阳即将毁灭，人类开启流浪地球计划");
        movie.setCreateTime(new Date());

        // 3. 输出测试
        System.out.println("====== 电影信息测试 ======");
        System.out.println("ID: " + movie.getId());
        System.out.println("名称: " + movie.getName());
        System.out.println("封面: " + movie.getCover());
        System.out.println("导演: " + movie.getDirector());
        System.out.println("主演: " + movie.getActors());
        System.out.println("类型: " + movie.getType());
        System.out.println("地区: " + movie.getRegion());
        System.out.println("语言: " + movie.getLanguage());
        System.out.println("上映时间: " + movie.getReleaseDate());
        System.out.println("时长: " + movie.getDuration() + "分钟");
        System.out.println("简介: " + movie.getDescription());
        System.out.println("创建时间: " + movie.getCreateTime());

        // 4. toString测试
        System.out.println("\n====== toString测试 ======");
        System.out.println(movie.toString());

        System.out.println("====== 测试结束 ======");
    }
}