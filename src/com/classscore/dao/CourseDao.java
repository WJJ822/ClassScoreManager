package com.classscore.dao;

import com.classscore.model.Course;
import com.classscore.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问。
 */
public class CourseDao {

    public List<Course> listAll() throws SQLException {
        String sql = "SELECT id, code, name, credit FROM course ORDER BY id";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCode(rs.getString("code"));
                course.setName(rs.getString("name"));
                course.setCredit(rs.getDouble("credit"));
                courses.add(course);
            }
            return courses;
        }
    }

    public Course findById(int id) throws SQLException {
        String sql = "SELECT id, code, name, credit FROM course WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCode(rs.getString("code"));
                course.setName(rs.getString("name"));
                course.setCredit(rs.getDouble("credit"));
                return course;
            }
        }
    }
}
