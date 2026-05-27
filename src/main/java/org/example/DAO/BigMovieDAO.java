package org.example.DAO;

import org.example.Entity.Movie_details;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class BigMovieDAO {

    private final JdbcTemplate jdbcTemplate;

    public BigMovieDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Movie_details> rowMapper = (rs, rowNum) -> {
        Movie_details m = new Movie_details();
        m.setId(rs.getLong("id"));
        m.setName(rs.getString("name"));
        m.setCover(rs.getString("cover"));
        m.setDirector(rs.getString("director"));
        m.setActors(rs.getString("actors"));
        m.setType(rs.getString("type"));
        m.setRegion(rs.getString("region"));
        m.setLanguage(rs.getString("language"));
        Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) m.setReleaseDate(releaseDate);
        int duration = rs.getInt("duration");
        if (!rs.wasNull()) m.setDuration(duration);
        m.setDescription(rs.getString("description"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) m.setCreateTime(createTime);
        return m;
    };

    public List<Movie_details> findAll() {
        String sql = "SELECT * FROM big_movie ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Movie_details> findTop3() {
        String sql = "SELECT * FROM big_movie ORDER BY create_time DESC LIMIT 3";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
