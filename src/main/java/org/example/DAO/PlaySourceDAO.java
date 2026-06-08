package org.example.DAO;

import org.example.Entity.PlaySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PlaySourceDAO {

    private final JdbcTemplate jdbcTemplate;

    public PlaySourceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PlaySource> rowMapper = (rs, rowNum) -> {
        PlaySource p = new PlaySource();

        p.setId(rs.getLong("id"));
        p.setMovieId(rs.getObject("movie_id") != null ? rs.getLong("movie_id") : null);
        p.setUrl(rs.getString("url"));
        p.setName(rs.getString("name"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            p.setCreateTime(createTime.toLocalDateTime());
        }

        return p;
    };

    // 查询全部
    public List<PlaySource> findAll() {
        String sql = "SELECT * FROM movie_play_source ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // 根据ID查一条
    public PlaySource findById(Long id) {
        String sql = "SELECT * FROM movie_play_source WHERE id = ?";
        List<PlaySource> list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // 根据电影ID查询播放资源
    public List<PlaySource> findByMovieId(Long movieId) {
        String sql = "SELECT * FROM movie_play_source WHERE movie_id = ?";
        return jdbcTemplate.query(sql, rowMapper, movieId);
    }

    // 插入
    public int insert(PlaySource resource) {
        String sql = "INSERT INTO movie_play_source (movie_id, url, name, create_time) " +
                "VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                resource.getMovieId(),
                resource.getUrl(),
                resource.getName(),
                resource.getCreateTime()
        );
    }

    // 更新
    public int update(PlaySource resource) {
        String sql = "UPDATE movie_play_source SET url = ?, name = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                resource.getUrl(),
                resource.getName(),
                resource.getId()
        );
    }

    // 删除
    public int deleteById(Long id) {
        String sql = "DELETE FROM movie_play_source WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
