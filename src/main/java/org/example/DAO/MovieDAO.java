package org.example.DAO;


import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MovieDAO
{

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Movie_details> movieRowMapper = (rs, rowNum) -> {
        Movie_details movie = new Movie_details();

        movie.setId(rs.getLong("id"));
        movie.setName(rs.getString("name"));
        movie.setCover(rs.getString("cover"));
        movie.setDirector(rs.getString("director"));
        movie.setActors(rs.getString("actors"));
        movie.setType(rs.getString("type"));
        movie.setRegion(rs.getString("region"));
        movie.setLanguage(rs.getString("language"));

        // 处理可能为空的日期字段
        Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            movie.setReleaseDate(releaseDate);
        }

        // 处理可能为空的整型字段
        int duration = rs.getInt("duration");
        if (!rs.wasNull()) {
            movie.setDuration(duration);
        }

        movie.setDescription(rs.getString("description"));

        // 处理创建时间
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            movie.setCreateTime(createTime);
        }

        return movie;
    };

    //返回全部电影
    public List<Movie_details> Find_All()
    {
        String sql = "SELECT * FROM movie";
        return jdbcTemplate.query(sql, movieRowMapper);
    }

    //返回一页电影
    public List<Movie_details> Find_one_movie(int page, int size) {
        if (page < 1) page = 1;

        int offset = (page - 1) * size;

        String sql = "SELECT * FROM movie LIMIT ?, ?";
        return jdbcTemplate.query(sql, new Object[]{offset, size}, movieRowMapper);
    }

    //根据id查询电影
    public Movie_details Find_one_by_id(int id)
    {
        String sql = "SELECT * FROM movie WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, movieRowMapper);
    }

    //根据name查询电影
    public List<Movie_details> Find_by_name(String name)
    {
        String sql = "SELECT * FROM movie WHERE name LIKE ?";
        return jdbcTemplate.query(sql,
                new Object[]{"%" + name + "%"},
                movieRowMapper);
    }

    //添加一部电影
    public int Add_movie(Movie_details movie)
    {
        String sql = "INSERT INTO movie " +
                "(name, cover, director, actors, type, region, language, release_date, duration, description, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                movie.getName(),
                movie.getCover(),
                movie.getDirector(),
                movie.getActors(),
                movie.getType(),
                movie.getRegion(),
                movie.getLanguage(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getDescription(),
                new Timestamp(System.currentTimeMillis()) // 自动生成创建时间
        );
    }

    //根据id删除一部电影
    public int Delete_movie(int id)
    {
        String sql = "DELETE FROM movie WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    //根据id更新电影
    public int Update_movie(Movie_details movie) {
        String sql = "UPDATE movie SET " +
                "name=?, cover=?, director=?, actors=?, type=?, region=?, language=?, " +
                "release_date=?, duration=?, description=? " +
                "WHERE id=?";

        return jdbcTemplate.update(sql,
                movie.getName(),
                movie.getCover(),
                movie.getDirector(),
                movie.getActors(),
                movie.getType(),
                movie.getRegion(),
                movie.getLanguage(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getDescription(),
                movie.getId()
        );
    }
}
