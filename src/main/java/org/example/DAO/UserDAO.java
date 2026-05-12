package org.example.DAO;

import org.example.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, rowNum) ->
    {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("Username"));
        u.setPassword(rs.getString("Password"));
        u.setEmail(rs.getString("Email"));
        u.setRole(rs.getString("Role"));
        return u;
    };

    public List<User> Find_All()
    {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public User Find_By_Name(String name)
    {
        String sql = "select * from users where username = ? limit 1";
        return jdbcTemplate.queryForObject(sql,new Object[]{name},rowMapper);
    }

    public List<String> Find_Permissions_By_UserId(Long userId)
    {
        String sql = """
                SELECT DISTINCT p.name
                FROM user_roles ur
                JOIN role_permissions rp ON ur.role_id = rp.role_id
                JOIN permissions p ON rp.permission_id = p.id
                WHERE ur.user_id = ?
                """;
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }


}
