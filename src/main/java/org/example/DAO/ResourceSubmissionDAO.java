package org.example.DAO;

import org.example.Entity.Resource_Submission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ResourceSubmissionDAO {

    private final JdbcTemplate jdbcTemplate;

    public ResourceSubmissionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Resource_Submission> rowMapper = (rs, rowNum) ->
    {
        Resource_Submission sub = new Resource_Submission();

        sub.setId(rs.getInt("id"));
        sub.setMovie_id(rs.getInt("movie_id"));
        sub.setMovie_name(rs.getString("movie_name"));
        sub.setResouce_name(rs.getString("name"));
        sub.setUrl(rs.getString("url"));
        sub.setType(rs.getString("type"));
        sub.setQuality(rs.getString("quality"));
        sub.setSize(rs.getString("size"));
        sub.setSubmitter(rs.getString("submitter"));
        sub.setSubmitter_id(rs.getObject("submitter_id", Long.class));
        sub.setStatus(rs.getString("status"));
        sub.setReview_msg(rs.getString("review_msg"));
        sub.setNote(rs.getString("note"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            sub.setCreateTime(createTime.toLocalDateTime());
        }

        return sub;
    };

    public int insert(Resource_Submission sub)
    {
        String sql = "INSERT INTO resource_submission (movie_id, movie_name, name, url, type, quality, size, submitter, submitter_id, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                sub.getMovie_id(),
                sub.getMovie_name(),
                sub.getResouce_name(),
                sub.getUrl(),
                sub.getType(),
                sub.getQuality(),
                sub.getSize(),
                sub.getSubmitter(),
                sub.getSubmitter_id(),
                sub.getNote()
        );
    }

    public List<Resource_Submission> findByStatus(String status)
    {
        String sql = "SELECT * FROM resource_submission WHERE status = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public List<Resource_Submission> findAll()
    {
        String sql = "SELECT * FROM resource_submission ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int updateStatus(int id, String status, String reviewMsg)
    {
        String sql = "UPDATE resource_submission SET status = ?, review_msg = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, reviewMsg, id);
    }

    public int deleteById(int id)
    {
        String sql = "DELETE FROM resource_submission WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
