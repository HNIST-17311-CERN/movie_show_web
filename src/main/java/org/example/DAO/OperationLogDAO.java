package org.example.DAO;

import org.example.Entity.OperationLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class OperationLogDAO {

    private final JdbcTemplate jdbcTemplate;

    public OperationLogDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OperationLog> rowMapper = (rs, rowNum) -> {
        OperationLog log = new OperationLog();
        log.setId(rs.getLong("id"));
        log.setUsername(rs.getString("username"));
        log.setMethod(rs.getString("method"));
        log.setOperation(rs.getString("operation"));
        log.setParams(rs.getString("params"));
        log.setResult(rs.getString("result"));
        log.setErrorMsg(rs.getString("error_msg"));
        log.setExecuteTime(rs.getLong("execute_time"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) log.setCreateTime(ct);
        return log;
    };

    public void insertLog(String username,
            String method,
            String operation,
            String params,
            String result,
            String errorMsg,
            long executeTime) {

        String sql = "INSERT INTO operation_log " +
                "(username, method, operation, params, result, error_msg, execute_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                username,
                method,
                operation,
                params,
                result,
                errorMsg,
                executeTime
        );
    }

    public List<OperationLog> findRecent(int limit) {
        String sql = "SELECT * FROM operation_log ORDER BY create_time DESC LIMIT ?";
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public List<OperationLog> findByUser(String username, int limit) {
        String sql = "SELECT * FROM operation_log WHERE username = ? ORDER BY create_time DESC LIMIT ?";
        return jdbcTemplate.query(sql, rowMapper, username, limit);
    }
}