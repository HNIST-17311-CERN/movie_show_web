package org.example.Servlet;

import org.example.Entity.Movie_details;
import org.example.Service.BigMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BIGMOVIE")
@CrossOrigin(origins = "*")
public class BigMovieController {

    @Autowired
    private BigMovieService bigMovieService;

    @GetMapping("/TOP3")
    public List<Movie_details> top3() {
        return bigMovieService.getTop3();
    }

    @GetMapping("/ALL")
    public List<Movie_details> all() {
        return bigMovieService.getAll();
    }
}
