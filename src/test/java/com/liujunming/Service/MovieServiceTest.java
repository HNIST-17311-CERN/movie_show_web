package org.example;

import org.example.Entity.Movie_details;
import org.example.Service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MovieServiceTest
{

    @Autowired
    private MovieService movieService;

    @Test
    public void testGetAll()
    {
        List<Movie_details> list = movieService.get_all();

        System.out.println("====== 所有电影 ======");
        for (Movie_details m : list)
        {
            System.out.println(m);
        }
    }

    @Test
    public void testGetOne()
    {
        Movie_details movie = movieService.get_one_details(1);

        System.out.println("====== 电影详情 ======");
        System.out.println(movie);
    }

    @Test
    public void testSearch()
    {
        List<Movie_details> list = movieService.search_by_name("流浪");

        System.out.println("====== 搜索结果 ======");
        for (Movie_details m : list)
        {
            System.out.println(m);
        }
    }

    @Test
    public void testAdd()
    {
        Movie_details m = new Movie_details();
        m.setName("测试电影");
        m.setDirector("张三");

        boolean res = movieService.add_movie(m);

        System.out.println("====== 添加电影 ======");
        System.out.println("结果: " + res);
    }

    @Test
    public void testDelete()
    {
        boolean res = movieService.delete_movie(1);

        System.out.println("====== 删除电影 ======");
        System.out.println("结果: " + res);
    }
}