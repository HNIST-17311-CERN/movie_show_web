package com.liujunming.DAO;

import org.example.DAO.MovieDAO;
import org.example.Entity.Movie_details;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MovieFilterDAOTest {

    @Autowired
    private MovieDAO movieDAO;

    @Test
    public void testFilterByTypeYearRegionLanguage() {
        List<Movie_details> list = movieDAO.Find_by_filter(
                "剧情",
                "2026",
                "美国",
                "英语",
                "releaseDate",
                1,
                12
        );

        System.out.println("===== testFilterByTypeYearRegionLanguage =====");
        list.forEach(System.out::println);

        assertNotNull(list);
        assertTrue(list.size() >= 0);
        for (Movie_details movie : list) {
            assertTrue(contains(movie.getType(), "剧情"));
            assertTrue(contains(movie.getRegion(), "美国"));
            assertTrue(contains(movie.getLanguage(), "英语"));
            assertTrue(movie.getReleaseDate().toString().startsWith("2026"));
        }
    }

    @Test
    public void testFilterByTypeOnly() {
        List<Movie_details> list = movieDAO.Find_by_filter(
                "喜剧",
                null,
                null,
                null,
                "create_time",
                1,
                12
        );

        System.out.println("===== testFilterByTypeOnly =====");
        list.forEach(System.out::println);

        assertNotNull(list);
        for (Movie_details movie : list) {
            assertTrue(contains(movie.getType(), "喜剧"));
        }
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }
}
