package org.example.DAO;

import org.example.Entity.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class MessageDAO {

    private final JdbcTemplate jdbcTemplate;

    public MessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Message> rowMapper = (rs, rowNum) -> {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setUserId(rs.getLong("user_id"));
        m.setUsername(rs.getString("username"));
        m.setContent(rs.getString("content"));
        m.setType(rs.getString("type"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) m.setCreateTime(ct);
        return m;
    };

    // ==================== 已审核发布表 ====================

    public List<Message> findApproved() {
        String sql = "SELECT * FROM messages ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int insertApproved(Message msg) {
        String sql = "INSERT INTO messages (user_id, username, content, type) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, msg.getUserId(), msg.getUsername(), msg.getContent(), msg.getType());
    }

    // ==================== 待审核表 ====================

    public List<Message> findPending() {
        String sql = "SELECT * FROM message_pending ORDER BY create_time ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int insertPending(Message msg) {
        String sql = "INSERT INTO message_pending (user_id, username, content, type) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, msg.getUserId(), msg.getUsername(), msg.getContent(), msg.getType());
    }

    public Message findPendingById(Long id) {
        String sql = "SELECT * FROM message_pending WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public int deletePending(Long id) {
        String sql = "DELETE FROM message_pending WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int deleteApproved(Long id) {
        String sql = "DELETE FROM messages WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
