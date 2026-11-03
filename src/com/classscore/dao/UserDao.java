package com.classscore.dao;

import com.classscore.model.User;
import com.classscore.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 登录账号查询。
 */
public class UserDao {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, role, ref_id, display_name FROM app_user WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setRefId(rs.getInt("ref_id"));
                user.setDisplayName(rs.getString("display_name"));
                return user;
            }
        }
    }

    public String findPasswordHash(String username) throws SQLException {
        String sql = "SELECT password_hash FROM app_user WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }
}
