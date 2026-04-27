package com.liujunming.DAO;

import org.example.DAO.Movie_ScoreDAO;
import org.example.Entity.Movie_Score;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class MovieScoreDAOTest
{

    @Autowired
    private Movie_ScoreDAO dao;

    @Test
    public void testFindAll()
    {
        List<Movie_Score> list = dao.findAll();

        System.out.println("====== 查询所有评分 ======");
        for (Movie_Score s : list)
        {
            System.out.println("ID: " + s.getId());
            System.out.println("电影ID: " + s.getMovieId());
            System.out.println("来源: " + s.getSource());
            System.out.println("评分: " + s.getScore());
            System.out.println("人数: " + s.getCount());
            System.out.println("-------------------");
        }
    }

    @Test
    public void testFindByMovieId()
    {
        List<Movie_Score> list = dao.findByMovieId(1L);

        System.out.println("====== 电影评分查询 ======");
        for (Movie_Score s : list)
        {
            System.out.println(s);
        }
    }

    @Test
    public void testInsert()
    {
        Movie_Score score = new Movie_Score();
        score.setMovieId(1L);
        score.setSource("douban");
        score.setScore(new BigDecimal("8.2"));
        score.setCount(120000);

        int rows = dao.insert(score);

        System.out.println("====== 插入评分 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testUpdate()
    {
        Movie_Score score = new Movie_Score();
        score.setId(1L);
        score.setSource("imdb");
        score.setScore(new BigDecimal("7.9"));
        score.setCount(90000);

        int rows = dao.update(score);

        System.out.println("====== 更新评分 ======");
        System.out.println("影响行数: " + rows);
    }

    @Test
    public void testDelete()
    {
        int rows = dao.deleteById(1L);

        System.out.println("====== 删除评分 ======");
        System.out.println("影响行数: " + rows);
    }

    // 🔥 进阶：加权平均评分计算（加分点）
    @Test
    public void testWeightedScore()
    {
        List<Movie_Score> list = dao.findByMovieId(1L);

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        for (Movie_Score s : list)
        {
            total = total.add(s.getScore().multiply(new BigDecimal(s.getCount())));
            count += s.getCount();
        }

        BigDecimal avg = count == 0
                ? BigDecimal.ZERO
                : total.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);

        System.out.println("====== 加权评分 ======");
        System.out.println("总人数: " + count);
        System.out.println("综合评分: " + avg);
    }
}