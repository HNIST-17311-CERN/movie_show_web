package org.example.Service;

import org.example.DAO.MovieDAO;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService
{

    @Autowired
    MovieDAO movieDAO;

    // 所有电影
    public List<Movie_details> get_all() {
        return movieDAO.Find_All();
    }

    // 分页查询
    public List<Movie_details> get_one_p(int page, int size) {
        return movieDAO.Find_one_movie(page, size);
    }

    // 根据id查询电影详情
    public Movie_details get_one_details(int id) {
        Movie_details movie = movieDAO.Find_one_by_id(id);

        // ✅ 防止查不到直接报错
        if (movie == null) {
            throw new RuntimeException("电影不存在，id=" + id);
        }

        return movie;
    }

    // ✅ 根据名字搜索（模糊查询）
    public List<Movie_details> search_by_name(String name) {
        return movieDAO.Find_by_name(name);
    }

    // ✅ 添加电影
    public boolean add_movie(Movie_details movie) {
        int rows = movieDAO.Add_movie(movie);
        return rows > 0;
    }

    // ✅ 删除电影
    public boolean delete_movie(int id) {
        int rows = movieDAO.Delete_movie(id);
        return rows > 0;
    }

    // ✅ 更新电影
    public boolean update_movie(Movie_details movie) {
        int rows = movieDAO.Update_movie(movie);
        return rows > 0;
    }
}