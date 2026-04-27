package org.example.Mapper;

import org.example.Entity.Movie_details;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper
{

    List<Movie_details> findlast12();

}