package org.example.Mapper;

import org.example.Entity.MovieCascadeDetails;
import org.example.Entity.Movie_details;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper
{

    List<Movie_details> findlast12();

    MovieCascadeDetails findMovieWithScoreById(@Param("id") Long id);

    MovieCascadeDetails findMovieWithResourcesById(@Param("id") Long id);

}
