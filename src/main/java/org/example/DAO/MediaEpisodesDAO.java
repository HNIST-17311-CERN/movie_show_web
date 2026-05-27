package org.example.DAO;

import org.example.Entity.MediaEpisodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MediaEpisodesDAO {

    private final JdbcTemplate jdbcTemplate;

    public MediaEpisodesDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MediaEpisodes> rowMapper = (rs, rowNum) -> {
        MediaEpisodes me = new MediaEpisodes();
        me.setId(rs.getLong("id"));
        me.setTvId(rs.getObject("tv_id", Long.class));
        me.setAnimeId(rs.getObject("anime_id", Long.class));
        me.setTotalEpisodes(rs.getObject("total_episodes", Integer.class));
        me.setUpdateStatus(rs.getString("update_status"));
        return me;
    };

    public MediaEpisodes findByAnimeId(Long animeId) {
        String sql = "SELECT * FROM media_episodes WHERE anime_id = ?";
        List<MediaEpisodes> list = jdbcTemplate.query(sql, rowMapper, animeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public MediaEpisodes findByTvId(Long tvId) {
        String sql = "SELECT * FROM media_episodes WHERE tv_id = ?";
        List<MediaEpisodes> list = jdbcTemplate.query(sql, rowMapper, tvId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(MediaEpisodes me) {
        String sql = "INSERT INTO media_episodes (tv_id, anime_id, total_episodes, update_status) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, me.getTvId(), me.getAnimeId(), me.getTotalEpisodes(), me.getUpdateStatus());
    }

    public int update(MediaEpisodes me) {
        String sql = "UPDATE media_episodes SET total_episodes = ?, update_status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, me.getTotalEpisodes(), me.getUpdateStatus(), me.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM media_episodes WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
