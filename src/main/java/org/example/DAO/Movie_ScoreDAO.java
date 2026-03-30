package org.example.DAO;

import org.example.Entity.Movie_Score;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class Movie_ScoreDAO {

    private final JdbcTemplate jdbcTemplate;

    public Movie_ScoreDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 映射数据库记录到实体
    private final RowMapper<Movie_Score> movieScoreRowMapper = (rs, rowNum) -> {
        Movie_Score score = new Movie_Score();
        score.setId(rs.getLong("id"));
        score.setMovieId(rs.getLong("movie_id"));
        score.setSource(rs.getString("source"));
        score.setScore(rs.getBigDecimal("score"));
        score.setCount(rs.getInt("count"));
        return score;
    };

    // 查询所有评分
    public List<Movie_Score> findAll() {
        String sql = "SELECT * FROM movie_score";
        return jdbcTemplate.query(sql, movieScoreRowMapper);
    }

    // 根据电影ID查询评分列表
    public List<Movie_Score> findByMovieId(Long movieId) {
        String sql = "SELECT * FROM movie_score WHERE movie_id = ?";
        return jdbcTemplate.query(sql, movieScoreRowMapper, movieId);
    }

    // 插入新的评分
    public int insert(Movie_Score score) {
        String sql = "INSERT INTO movie_score (movie_id, source, score, count) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                score.getMovieId(),
                score.getSource(),
                score.getScore(),
                score.getCount()
        );
    }

    // 更新评分
    public int update(Movie_Score score) {
        String sql = "UPDATE movie_score SET source = ?, score = ?, count = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                score.getSource(),
                score.getScore(),
                score.getCount(),
                score.getId()
        );
    }

    // 根据ID删除评分
    public int deleteById(Long id) {
        String sql = "DELETE FROM movie_score WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}