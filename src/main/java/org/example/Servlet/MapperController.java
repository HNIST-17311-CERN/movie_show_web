package org.example.Servlet;

import org.example.Entity.Movie_details;
import org.example.Mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/Mapper")
public class MapperController
{

    @Autowired
    MovieMapper movieMapper;

    @GetMapping("/findlast12")
    public List<Movie_details> findlast12()
    {

          return  movieMapper.findlast12();

    }

}
