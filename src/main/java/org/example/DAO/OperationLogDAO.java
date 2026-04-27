package org.example.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogDAO {

    private final JdbcTemplate jdbcTemplate;

    public OperationLogDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
}