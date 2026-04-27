package com.liujunming.Mapper;

import org.example.Mapper.MovieMapper;
import org.example.Entity.Movie_details;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MovieMapperTest {

    @Autowired
    private MovieMapper movieMapper;

    // 测试查询全部
    @Test
    public void findlast12() {
        List<Movie_details> list = movieMapper.findlast12();
        System.out.println("电影数量：" + list.size());
        list.forEach(System.out::println);
    }
}