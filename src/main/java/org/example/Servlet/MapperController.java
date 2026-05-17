package org.example.Servlet;

import org.example.Entity.MovieCascadeDetails;
import org.example.Entity.Movie_details;
import org.example.Mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/Mapper")
@CrossOrigin(origins = "*")  // 允许所有来源访问
public class MapperController
{

    @Autowired
    MovieMapper movieMapper;

    @GetMapping("/findlast12")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<Movie_details> findlast12()
    {

          return  movieMapper.findlast12();

    }

    @GetMapping("/findMovieWithScoreById")
    @PreAuthorize("hasAuthority('movie:view')")
    public MovieCascadeDetails findMovieWithScoreById(@RequestParam("id") Long id)
    {
        return movieMapper.findMovieWithScoreById(id);
    }

    @GetMapping("/findMovieWithResourcesById")
    @PreAuthorize("hasAuthority('movie:view')")
    public MovieCascadeDetails findMovieWithResourcesById(@RequestParam("id") Long id)
    {
        return movieMapper.findMovieWithResourcesById(id);
    }

}
