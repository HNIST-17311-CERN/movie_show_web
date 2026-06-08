package org.example.Service;

import org.example.DAO.PlaySourceDAO;
import org.example.Entity.PlaySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaySourceService
{
    @Autowired
    PlaySourceDAO playSourceDAO;

    public List<PlaySource> get_all()
    {
        return playSourceDAO.findAll();
    }//查询全部播放资源

    public PlaySource get_by_id(Long id)
    {
        return playSourceDAO.findById(id);
    }//根据ID查询单条

    public List<PlaySource> get_by_movie_id(Long movieId)
    {
        return playSourceDAO.findByMovieId(movieId);
    }//根据电影ID查询播放资源

    public int insert(PlaySource resource)
    {
        return playSourceDAO.insert(resource);
    }//新增播放资源

    public int update(PlaySource resource)
    {
        return playSourceDAO.update(resource);
    }//编辑播放资源

    public int deleteById(Long id)
    {
        return playSourceDAO.deleteById(id);
    }//删除播放资源

}
