package org.example.DAO;

import org.example.Entity.Movie_Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class Movie_ResourceDAO {

    private final JdbcTemplate jdbcTemplate;

    public Movie_ResourceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 样例
    private final RowMapper<Movie_Resource> movieResourceRowMapper = (rs, rowNum) -> {
        Movie_Resource resource = new Movie_Resource();

        resource.setId(rs.getLong("id"));
        resource.setMovieId(rs.getLong("movie_id"));
        resource.setName(rs.getString("name"));
        resource.setQuality(rs.getString("quality"));
        resource.setSize(rs.getString("size"));
        resource.setType(rs.getString("type"));
        resource.setUrl(rs.getString("url"));
        resource.setSubtitle(rs.getString("subtitle"));

        // 处理创建时间
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            resource.setCreateTime(createTime.toLocalDateTime());
        }

        return resource;
    };

    // 根据电影ID查询资源
    public List<Movie_Resource> findByMovieId(Long movieId) {
        String sql = "SELECT * FROM movie_resource WHERE movie_id = ?";
        return jdbcTemplate.query(sql, movieResourceRowMapper, movieId);
    }

    // 插入新资源
    public int insert(Movie_Resource resource) {
        String sql = "INSERT INTO movie_resource (movie_id, name, quality, size, type, url, create_time, subtitle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                resource.getMovieId(),
                resource.getName(),
                resource.getQuality(),
                resource.getSize(),
                resource.getType(),
                resource.getUrl(),
                resource.getCreateTime(),
                resource.getSubtitle()
        );
    }

    // 更新资源
    public int update(Movie_Resource resource) {
        String sql = "UPDATE movie_resource SET name = ?, quality = ?, size = ?, type = ?, url = ?, create_time = ?, subtitle = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql,
                resource.getName(),
                resource.getQuality(),
                resource.getSize(),
                resource.getType(),
                resource.getUrl(),
                resource.getCreateTime(),
                resource.getSubtitle(),
                resource.getId()
        );
    }

    // 根据ID删除资源
    public int deleteById(Long id) {
        String sql = "DELETE FROM movie_resource WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}