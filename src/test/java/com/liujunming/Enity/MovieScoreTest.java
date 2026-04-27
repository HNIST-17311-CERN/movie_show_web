package com.liujunming.Enity;

import org.example.Entity.Movie_Score;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MovieScoreTest
{

    @Test
    public void testMovieScore()
    {
        // 1. 创建评分对象（模拟多个平台）
        Movie_Score douban = new Movie_Score(
                1L, 1L, "douban",
                new BigDecimal("7.8"), 100000
        );

        Movie_Score imdb = new Movie_Score(
                2L, 1L, "imdb",
                new BigDecimal("7.5"), 80000
        );

        // 2. 放入集合
        List<Movie_Score> scores = Arrays.asList(douban, imdb);

        // 3. 输出评分信息
        System.out.println("====== 电影评分测试 ======");
        for (Movie_Score score : scores)
        {
            System.out.println("评分ID: " + score.getId());
            System.out.println("电影ID: " + score.getMovieId());
            System.out.println("来源: " + score.getSource());
            System.out.println("评分: " + score.getScore());
            System.out.println("人数: " + score.getCount());
            System.out.println("----------------------");
        }

        // 4. 计算加权平均评分（加分点🔥）
        BigDecimal totalScore = BigDecimal.ZERO;
        int totalCount = 0;

        for (Movie_Score s : scores)
        {
            totalScore = totalScore.add(s.getScore().multiply(new BigDecimal(s.getCount())));
            totalCount += s.getCount();
        }

        BigDecimal avgScore = totalScore.divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);

        System.out.println("====== 综合评分 ======");
        System.out.println("总评分人数: " + totalCount);
        System.out.println("加权平均评分: " + avgScore);

        // 5. toString测试
        System.out.println("\n====== toString测试 ======");
        scores.forEach(System.out::println);

        System.out.println("====== 测试结束 ======");
    }
}