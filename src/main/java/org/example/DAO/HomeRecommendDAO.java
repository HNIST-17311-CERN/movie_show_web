package org.example.DAO;

import org.example.Entity.Movie_details;
import org.example.Entity.RecommendItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class HomeRecommendDAO {

    private final JdbcTemplate jdbcTemplate;

    public HomeRecommendDAO(JdbcTemplate jdbcTemplate) {
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

    private final RowMapper<RecommendItem> itemRowMapper = (rs, rowNum) -> {
        RecommendItem item = new RecommendItem();
        item.setMovieId(rs.getLong("id"));
        item.setMovieName(rs.getString("name"));
        item.setCover(rs.getString("cover"));
        item.setSortOrder(rs.getInt("sort_order"));
        Timestamp hmCreateTime = rs.getTimestamp("hm_create_time");
        if (hmCreateTime != null) item.setCreateTime(hmCreateTime);
        return item;
    };

    /*==========================================================================
     *                              电影推荐
     *==========================================================================*/

    public List<RecommendItem> findMovieItems() {
        String sql = """
                SELECT m.id, m.name, m.cover, hm.sort_order, hm.create_time AS hm_create_time
                FROM home_movie hm
                INNER JOIN movie m ON m.id = hm.movie_id
                ORDER BY hm.sort_order ASC
                """;
        return jdbcTemplate.query(sql, itemRowMapper);
    }

    public List<Movie_details> findMovieRecommend(int limit) {
        String sql = """
                SELECT m.* FROM movie m
                INNER JOIN home_movie hm ON m.id = hm.movie_id
                ORDER BY hm.create_time DESC, hm.sort_order ASC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public int insertMovieRecommend(Long movieId, int sortOrder) {
        String sql = "INSERT INTO home_movie (movie_id, sort_order) VALUES (?, ?)";
        return jdbcTemplate.update(sql, movieId, sortOrder);
    }

    public int deleteMovieRecommend(Long movieId) {
        String sql = "DELETE FROM home_movie WHERE movie_id = ?";
        return jdbcTemplate.update(sql, movieId);
    }

    public int updateMovieSortOrder(Long movieId, int sortOrder) {
        String sql = "UPDATE home_movie SET sort_order = ? WHERE movie_id = ?";
        return jdbcTemplate.update(sql, sortOrder, movieId);
    }

    /*==========================================================================
     *                              剧集推荐
     *==========================================================================*/

    public List<RecommendItem> findTvItems() {
        String sql = """
                SELECT m.id, m.name, m.cover, ht.sort_order, ht.create_time AS hm_create_time
                FROM home_tv ht
                INNER JOIN movie m ON m.id = ht.tv_id
                ORDER BY ht.sort_order ASC
                """;
        return jdbcTemplate.query(sql, itemRowMapper);
    }

    public List<Movie_details> findTvRecommend(int limit) {
        String sql = """
                SELECT m.* FROM movie m
                INNER JOIN home_tv ht ON m.id = ht.tv_id
                ORDER BY ht.create_time DESC, ht.sort_order ASC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public int insertTvRecommend(Long movieId, int sortOrder) {
        String sql = "INSERT INTO home_tv (tv_id, sort_order) VALUES (?, ?)";
        return jdbcTemplate.update(sql, movieId, sortOrder);
    }

    public int deleteTvRecommend(Long movieId) {
        String sql = "DELETE FROM home_tv WHERE tv_id = ?";
        return jdbcTemplate.update(sql, movieId);
    }

    public int updateTvSortOrder(Long movieId, int sortOrder) {
        String sql = "UPDATE home_tv SET sort_order = ? WHERE tv_id = ?";
        return jdbcTemplate.update(sql, sortOrder, movieId);
    }

    /*==========================================================================
     *                              动漫推荐
     *==========================================================================*/

    public List<RecommendItem> findAnimeItems() {
        String sql = """
                SELECT m.id, m.name, m.cover, ha.sort_order, ha.create_time AS hm_create_time
                FROM home_anime ha
                INNER JOIN movie m ON m.id = ha.anime_id
                ORDER BY ha.sort_order ASC
                """;
        return jdbcTemplate.query(sql, itemRowMapper);
    }

    public List<Movie_details> findAnimeRecommend(int limit) {
        String sql = """
                SELECT m.* FROM movie m
                INNER JOIN home_anime ha ON m.id = ha.anime_id
                ORDER BY ha.create_time DESC, ha.sort_order ASC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public int insertAnimeRecommend(Long movieId, int sortOrder) {
        String sql = "INSERT INTO home_anime (anime_id, sort_order) VALUES (?, ?)";
        return jdbcTemplate.update(sql, movieId, sortOrder);
    }

    public int deleteAnimeRecommend(Long movieId) {
        String sql = "DELETE FROM home_anime WHERE anime_id = ?";
        return jdbcTemplate.update(sql, movieId);
    }

    public int updateAnimeSortOrder(Long movieId, int sortOrder) {
        String sql = "UPDATE home_anime SET sort_order = ? WHERE anime_id = ?";
        return jdbcTemplate.update(sql, sortOrder, movieId);
    }
}
