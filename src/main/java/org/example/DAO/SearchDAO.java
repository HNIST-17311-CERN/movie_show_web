package org.example.DAO;

import org.example.Entity.Movie_details;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SearchDAO {

    private final JdbcTemplate jdbcTemplate;

    public SearchDAO(JdbcTemplate jdbcTemplate) {
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

    /**
     * 生成非连续匹配的 LIKE 模式：输入 "挽救计划" → '%挽%救%计%划%'
     */
    private String buildCharLike(String q) {
        StringBuilder sb = new StringBuilder();
        for (char c : q.replaceAll("\\s+", "").toCharArray()) {
            sb.append('%').append(c);
        }
        sb.append('%');
        return sb.toString();
    }

    /**
     * 根据 type 参数生成 SQL 筛选条件
     * @return {whereClause, params...} — whereClause 不含 "WHERE" 前缀
     */
    private Object[] buildTypeFilter(String type) {
        return switch (type) {
            case "movie" -> new Object[]{"type NOT LIKE ? AND type NOT LIKE ?", "%动漫%", "%电视剧%"};
            case "tv"   -> new Object[]{"type LIKE ? AND type NOT LIKE ?", "%电视剧%", "%动漫%"};
            case "anime"-> new Object[]{"type LIKE ?", "%动漫%"};
            default     -> new Object[]{"1=1"};
        };
    }

    public List<Movie_details> search(String q, int mode, String type) {
        Object[] filter = buildTypeFilter(type);
        String typeWhere = (String) filter[0];

        List<Object> params = new ArrayList<>();

        String sql;
        if (mode == 3) {
            sql = "SELECT * FROM movie WHERE " + typeWhere + " AND (name = ? OR director = ? OR actors = ?)";
            for (int i = 1; i < filter.length; i++) params.add(filter[i]);
            params.add(q);
            params.add(q);
            params.add(q);
        } else {
            String like = buildCharLike(q);
            sql = "SELECT * FROM movie WHERE " + typeWhere + " AND (name LIKE ? OR director LIKE ? OR actors LIKE ?)";
            for (int i = 1; i < filter.length; i++) params.add(filter[i]);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        return jdbcTemplate.query(sql, params.toArray(), rowMapper);
    }
}
