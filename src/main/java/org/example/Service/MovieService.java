package org.example.Service;

import org.example.DAO.MovieDAO;
import org.example.DAO.Movie_ResourceDAO;
import org.example.DAO.Movie_ScoreDAO;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService
{

    @Autowired
    MovieDAO movieDAO;

    @Autowired
    Movie_ResourceDAO movieResourceDAO;

    @Autowired
    Movie_ScoreDAO movieScoreDAO;

    public List<Movie_details> get_all() {
        return movieDAO.Find_All();
    }

    public List<Movie_details> get_one_p(int page, int size) {
        return movieDAO.Find_one_movie(page, size);
    }

    public Movie_details get_one_details(int id) {
        Movie_details movie = movieDAO.Find_one_by_id(id);
        if (movie == null) {
            throw new RuntimeException("movie not found, id=" + id);
        }
        return movie;
    }

    public List<Movie_details> search_by_name(String name) {
        return movieDAO.Find_by_name(name);
    }

    public List<Movie_details> filter_movies(String type, String year, String region, String language,
                                             String sort, int page, int pageSize) {
        return movieDAO.Find_by_filter(type, year, region, language, sort, page, pageSize);
    }

    public boolean add_movie(Movie_details movie) {
        int rows = movieDAO.Add_movie(movie);
        return rows > 0;
    }

    @Transactional
    public boolean delete_movie(int id) {
        movieResourceDAO.findByMovieId((long) id)
                .forEach(r -> movieResourceDAO.deleteById(r.getId()));

        movieScoreDAO.findByMovieId((long) id)
                .forEach(s -> movieScoreDAO.deleteById(s.getId()));

        int rows = movieDAO.Delete_movie(id);
        return rows > 0;
    }

    public boolean update_movie(Movie_details movie) {
        int rows = movieDAO.Update_movie(movie);
        return rows > 0;
    }

    public List<Movie_details> Find_animation(int page, int size)
    {
        return movieDAO.Find_animation(page, size);
    }
}
