package org.example.Service;

import org.example.DAO.BigMovieDAO;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BigMovieService {

    @Autowired
    private BigMovieDAO bigMovieDAO;

    public List<Movie_details> getAll() {
        return bigMovieDAO.findAll();
    }

    public List<Movie_details> getTop3() {
        return bigMovieDAO.findTop3();
    }
}
