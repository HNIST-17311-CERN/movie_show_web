package com.liujunming.Service;

import org.example.Entity.Movie_Score;
import org.example.Service.Movie_Score_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MovieScoreServiceTest
{

    @Autowired
    private Movie_Score_Service service;

    @Test
    public void testGetScoreByMovieId()
    {
        List<Movie_Score> list = service.get_score_by_id(1L);

        System.out.println("====== Service查询评分 ======");
        for (Movie_Score s : list)
        {
            System.out.println(s);
        }
    }
}