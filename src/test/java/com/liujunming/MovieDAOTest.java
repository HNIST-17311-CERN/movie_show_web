package com.liujunming;

import org.example.DAO.MovieDAO;
import org.example.Entity.Movie_details;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieDAOTest {

    @Autowired
    private MovieDAO movieDAO;

    // ✅ 测试：查询全部电影
    @Test
    public void testFindAll() {
        List<Movie_details> list = movieDAO.Find_All();

        System.out.println("===== testFindAll =====");
        for (Movie_details movie : list) {
            System.out.println(movie);
        }

        assertNotNull(list);
        assertTrue(list.size() >= 0);
    }

    // ✅ 测试：分页查询
    @Test
    public void testFindOneMovie() {
        List<Movie_details> list = movieDAO.Find_one_movie(1, 5);

        System.out.println("===== testFindOneMovie =====");
        for (Movie_details movie : list) {
            System.out.println(movie);
        }

        assertNotNull(list);
    }

    // ✅ 测试：根据ID查询
    @Test
    public void testFindById() {
        Movie_details movie = movieDAO.Find_one_by_id(1);

        System.out.println("===== testFindById =====");
        System.out.println(movie);

        assertNotNull(movie);
        assertEquals(1, movie.getId());
    }

    // ✅ 测试：根据名称模糊查询
    @Test
    public void testFindByName() {
        List<Movie_details> list = movieDAO.Find_by_name("战");

        System.out.println("===== testFindByName =====");
        for (Movie_details movie : list) {
            System.out.println(movie);
        }

        assertNotNull(list);
    }
}