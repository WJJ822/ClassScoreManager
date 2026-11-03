package com.classscore.dao;

import com.classscore.model.Student;
import com.classscore.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问。
 */
public class StudentDao {

    public List<Student> list(String keyword, String className) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT id, student_no, name, class_name, gender FROM student WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(name) LIKE ? OR LOWER(student_no) LIKE ?)");
            String like = "%" + keyword.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (className != null && !className.trim().isEmpty()) {
            sql.append(" AND class_name = ?");
            params.add(className.trim());
        }
        sql.append(" ORDER BY class_name, student_no");
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (rs.next()) {
                    students.add(map(rs));
                }
                return students;
            }
        }
    }

    public List<Student> listByClass(String className) throws SQLException {
        return list(null, className);
    }

    public Student findById(int id) throws SQLException {
        String sql = "SELECT id, student_no, name, class_name, gender FROM student WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<String> listClasses() throws SQLException {
        String sql = "SELECT DISTINCT class_name FROM student ORDER BY class_name";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> classes = new ArrayList<>();
            while (rs.next()) {
                classes.add(rs.getString(1));
            }
            return classes;
        }
    }

    public boolean existsByNo(String studentNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM student WHERE student_no = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int insert(Student student, String username, String passwordHash) throws SQLException {
        String studentSql = "INSERT INTO student (student_no, name, class_name, gender) VALUES (?, ?, ?, ?)";
        String userSql = "INSERT INTO app_user (username, password_hash, role, ref_id, display_name) "
                + "VALUES (?, ?, 'student', ?, ?)";
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int studentId;
                try (PreparedStatement ps = connection.prepareStatement(
                        studentSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, student.getStudentNo());
                    ps.setString(2, student.getName());
                    ps.setString(3, student.getClassName());
                    ps.setString(4, student.getGender());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        studentId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = connection.prepareStatement(userSql)) {
                    ps.setString(1, username);
                    ps.setString(2, passwordHash);
                    ps.setInt(3, studentId);
                    ps.setString(4, student.getName());
                    ps.executeUpdate();
                }
                connection.commit();
                return studentId;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean delete(int studentId) throws SQLException {
        String deleteUser = "DELETE FROM app_user WHERE role = 'student' AND ref_id = ?";
        String deleteStudent = "DELETE FROM student WHERE id = ?";
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement ps = connection.prepareStatement(deleteUser)) {
                    ps.setInt(1, studentId);
                    ps.executeUpdate();
                }
                int affected;
                try (PreparedStatement ps = connection.prepareStatement(deleteStudent)) {
                    ps.setInt(1, studentId);
                    affected = ps.executeUpdate();
                }
                connection.commit();
                return affected > 0;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM student";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentNo(rs.getString("student_no"));
        student.setName(rs.getString("name"));
        student.setClassName(rs.getString("class_name"));
        student.setGender(rs.getString("gender"));
        return student;
    }

    private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
