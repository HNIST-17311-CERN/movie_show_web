package org.example.Servlet;

import org.example.DAO.MovieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private MovieDAO movieDAO;

    @GetMapping("/movies-monthly")
    public Map<String, Integer> moviesMonthly() {
        return movieDAO.countByMonth();
    }
}
