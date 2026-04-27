package com.liujunming.Enity;

import org.example.Entity.Movie_Resource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class MovieResourceTest
{

    @Test
    public void testMovieResource()
    {
        // 1. 创建对象
        Movie_Resource resource = new Movie_Resource();

        // 2. 设置数据
        resource.setId(1001L);
        resource.setMovieId(1L);
        resource.setName("流浪地球 1080P");
        resource.setQuality("1080P");
        resource.setSize("2.9GB");
        resource.setType("磁力");
        resource.setUrl("magnet:?xt=urn:btih:xxxxx");
        resource.setCreateTime(LocalDateTime.now());
        resource.setSubtitle("中字");

        // 3. 输出资源信息
        System.out.println("====== 电影资源测试 ======");
        System.out.println("资源ID: " + resource.getId());
        System.out.println("电影ID: " + resource.getMovieId());
        System.out.println("资源名称: " + resource.getName());
        System.out.println("清晰度: " + resource.getQuality());
        System.out.println("文件大小: " + resource.getSize());
        System.out.println("资源类型: " + resource.getType());
        System.out.println("下载地址: " + resource.getUrl());
        System.out.println("发布时间: " + resource.getCreateTime());
        System.out.println("字幕: " + resource.getSubtitle());

        // 4. toString测试
        System.out.println("\n====== toString测试 ======");
        System.out.println(resource.toString());

        System.out.println("====== 测试结束 ======");
    }
}