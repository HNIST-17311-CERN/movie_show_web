package org.example.Service;

import org.example.DAO.Movie_ResourceDAO;
import org.example.Entity.Movie_Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Movie_Resource_Service
{
    @Autowired
    Movie_ResourceDAO movieResourceDAO;

    public List<Movie_Resource> get_resource_by_id(Long id)
    {
        return movieResourceDAO.findByMovieId(id);
    }


}
