package org.example.Service;

import org.example.DAO.Movie_ScoreDAO;
import org.example.Entity.Movie_Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Movie_Score_Service
{

    @Autowired
    Movie_ScoreDAO movieScoreDAO;
    public List<Movie_Score>  get_score_by_id(Long id)
    {
        return movieScoreDAO.findByMovieId(id);
    }



}
