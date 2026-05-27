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
public class TV_DAO {

    private final JdbcTemplate jdbcTemplate;

    public TV_DAO(JdbcTemplate jdbcTemplate) {
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

    /*-----------------------------------------------------------------*/
    /*                         动漫（type 包含"动漫"）                   */
    /*-----------------------------------------------------------------*/

    public List<Movie_details> animeFindAll() {
        String sql = "SELECT * FROM movie WHERE type LIKE ? ORDER BY release_date DESC";
        return jdbcTemplate.query(sql, rowMapper, "%动漫%");
    }

    public List<Movie_details> animeFindPage(int page, int size) {
        if (page < 1) page = 1;
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM movie WHERE type LIKE ? ORDER BY release_date DESC LIMIT ?, ?";
        return jdbcTemplate.query(sql, rowMapper, "%动漫%", offset, size);
    }

    public List<Movie_details> animeFilter(String type, String year, String region, String language,
                                            String sort, int page, int size) {
        return filterByCategory("%动漫%", type, year, region, language, sort, page, size);
    }

    public Movie_details animeFindById(Long id) {
        String sql = "SELECT * FROM movie WHERE id = ? AND type LIKE ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id, "%动漫%");
    }

    public List<Movie_details> animeFindByName(String name) {
        String sql = "SELECT * FROM movie WHERE type LIKE ? AND name LIKE ?";
        return jdbcTemplate.query(sql, rowMapper, "%动漫%", "%" + name + "%");
    }

    /*-----------------------------------------------------------------*/
    /*                    电视剧（type 包含"剧"且不包含"动漫"）            */
    /*-----------------------------------------------------------------*/

    public List<Movie_details> tvFindAll() {
        String sql = "SELECT * FROM movie WHERE type LIKE ? AND type NOT LIKE ? ORDER BY release_date DESC";
        return jdbcTemplate.query(sql, rowMapper, "%电视剧%", "%动漫%");
    }

    public List<Movie_details> tvFindPage(int page, int size) {
        if (page < 1) page = 1;
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM movie WHERE type LIKE ? AND type NOT LIKE ? ORDER BY release_date DESC LIMIT ?, ?";
        return jdbcTemplate.query(sql, rowMapper, "%电视剧%", "%动漫%", offset, size);
    }

    public List<Movie_details> tvFilter(String type, String year, String region, String language,
                                         String sort, int page, int size) {
        return filterByCategory("%电视剧%", type, year, region, language, sort, page, size);
    }

    public Movie_details tvFindById(Long id) {
        String sql = "SELECT * FROM movie WHERE id = ? AND type LIKE ? AND type NOT LIKE ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id, "%电视剧%", "%动漫%");
    }

    public List<Movie_details> tvFindByName(String name) {
        String sql = "SELECT * FROM movie WHERE type LIKE ? AND type NOT LIKE ? AND name LIKE ?";
        return jdbcTemplate.query(sql, rowMapper, "%电视剧%", "%动漫%", "%" + name + "%");
    }

    /*-----------------------------------------------------------------*/
    /*                         通用筛选逻辑                              */
    /*-----------------------------------------------------------------*/

    private List<Movie_details> filterByCategory(String categoryFilter, String type, String year,
                                                  String region, String language, String sort,
                                                  int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 12;
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder("SELECT * FROM movie WHERE type LIKE ?");
        List<Object> params = new ArrayList<>();
        params.add(categoryFilter);

        if (type != null && !type.trim().isEmpty() && !"全部".equals(type.trim())) {
            sql.append(" AND type LIKE ?");
            params.add("%" + type.trim() + "%");
        }
        if (region != null && !region.trim().isEmpty() && !"全部".equals(region.trim())) {
            sql.append(" AND region LIKE ?");
            params.add("%" + region.trim() + "%");
        }
        if (language != null && !language.trim().isEmpty() && !"全部".equals(language.trim())) {
            sql.append(" AND language LIKE ?");
            params.add("%" + language.trim() + "%");
        }

        sql.append(" ORDER BY ");
        if ("releaseDate".equals(sort)) {
            sql.append("release_date DESC");
        } else {
            sql.append("create_time DESC");
        }

        sql.append(" LIMIT ?, ?");
        params.add(offset);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper);
    }
}
