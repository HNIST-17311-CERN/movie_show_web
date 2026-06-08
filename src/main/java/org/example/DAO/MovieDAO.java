package org.example.DAO;


import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieDAO
{

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Movie_details> movieRowMapper = (rs, rowNum) ->
    {
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

        String sql = "SELECT * FROM movie WHERE type NOT LIKE '%动漫%' AND type NOT LIKE '%电视剧%' LIMIT ?, ?";
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

    /**
     * 根据多种条件筛选电影，支持分页和排序。
     *
     * @param type     电影类型（模糊匹配，如“喜剧”），若为“全部”或空则忽略
     * @param year     年份筛选条件：具体年份（如“2023”）、近三年（“近三年”）、年代（如“90年代”）
     * @param region   地区（模糊匹配）
     * @param language 语言（模糊匹配）
     * @param sort     排序字段：releaseDate/release_date（按上映时间降序）、duration（按时长降序）、name（按名称升序），其他值默认按创建时间降序
     * @param page     页码（从1开始，小于1时重置为1）
     * @param size     每页条数（小于1时重置为12）
     * @return 满足条件的电影列表
     */
    public List<Movie_details> Find_by_filter(String type, String year, String region, String language, String sort, int page, int size)
    {
        if (page < 1) page = 1;
        if (size < 1) size = 12;

        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM movie WHERE type NOT LIKE '%动漫%' AND type NOT LIKE '%电视剧%'");
        List<Object> params = new ArrayList<>();

        if (hasFilterValue(type)) {
            sql.append(" AND type LIKE ?");
            params.add("%" + type.trim() + "%");
        }

        appendYearFilter(sql, params, year);

        if (hasFilterValue(region)) {
            sql.append(" AND region LIKE ?");
            params.add("%" + region.trim() + "%");
        }

        if (hasFilterValue(language)) {
            sql.append(" AND language LIKE ?");
            params.add("%" + language.trim() + "%");
        }

        sql.append(" ORDER BY ").append(getOrderBy(sort));
        sql.append(" LIMIT ?, ?");
        params.add(offset);
        params.add(size);

        return jdbcTemplate.query(sql.toString(), params.toArray(), movieRowMapper);
    }

    /**
     * 判断筛选值是否有效（非空、非空白、不等于“全部”）。
     *
     * @param value 传入的筛选值，如类型、地区等
     * @return true 表示需要应用该筛选条件，false 表示忽略该条件
     */
    private boolean hasFilterValue(String value)
    {
        return value != null && !value.trim().isEmpty() && !"全部".equals(value.trim());
    }

    /**
     * 根据年份条件动态拼接 SQL 的年份筛选部分，并填充参数。
     * 支持三种格式：
     * 1. 具体年份（四位数字），例如 "2023" → YEAR(release_date) = 2023
     * 2. "近三年" → YEAR(release_date) >= YEAR(CURDATE()) - 3
     * 3. "XX年代"（两位数字，如 "90年代"）→ 转换为起始年份区间，如 1990~1999
     *
     * @param sql    待拼接的 StringBuilder SQL 语句
     * @param params 参数列表，用于存储占位符对应的值
     * @param year   用户传入的年份筛选字符串
     */
    private void appendYearFilter(StringBuilder sql, List<Object> params, String year)
    {
        if (!hasFilterValue(year)) {
            return;
        }

        String value = year.trim();
        if (value.matches("\\d{4}")) {
            sql.append(" AND YEAR(release_date) = ?");
            params.add(Integer.parseInt(value));
            return;
        }

        if ("近三年".equals(value)) {
            sql.append(" AND YEAR(release_date) >= YEAR(CURDATE()) - 3");
            return;
        }

        if (value.matches("\\d{2}年代")) {
            int decade = Integer.parseInt(value.substring(0, 2));
            int startYear = decade >= 60 ? 1900 + decade : 2000 + decade;
            sql.append(" AND YEAR(release_date) BETWEEN ? AND ?");
            params.add(startYear);
            params.add(startYear + 9);
        }
    }

    /**
     * 将前端传入的排序字段映射为 SQL 的 ORDER BY 子句内容。
     * 支持的映射：
     * - "releaseDate" 或 "release_date" → release_date DESC（上映时间倒序）
     * - "duration" → duration DESC（时长倒序）
     * - "name" → name ASC（名称正序）
     * - 其他任意值（包括 null）→ create_time DESC（创建时间倒序）
     *
     * @param sort 前端传入的排序标识
     * @return 可用于直接拼接在 ORDER BY 之后的字段及排序方向字符串
     */
    private String getOrderBy(String sort)
    {
        if (sort == null) {
            return "create_time DESC";
        }

        return switch (sort.trim()) {
            case "releaseDate", "release_date" -> "release_date DESC";
            case "duration" -> "duration DESC";
            case "name" -> "name ASC";
            default -> "create_time DESC";
        };
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

    /*---------------------------------------------------------------------------------*/
    //类型查找
    //查找动画标签（1页）
    public List<Movie_details> Find_animation(int page, int size)
    {
        if (page < 1) page = 1;

        int offset = (page - 1) * size;

        String sql ="select * from movie where locate('动画', description) > 0 LIMIT ?, ?";
        return jdbcTemplate.query(sql, new Object[]{offset, size}, movieRowMapper);
    }
}
