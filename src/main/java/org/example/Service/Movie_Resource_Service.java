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
    }//根据id查询资源

    public int insert(Movie_Resource resource)
    {
        return movieResourceDAO.insert(resource);
    }//新增资源

    public int update(Movie_Resource resource)
    {
        return movieResourceDAO.update(resource);
    }//编辑资源

    public int deleteById(Long id)
    {
        return movieResourceDAO.deleteById(id);
    }//删除资源


}
