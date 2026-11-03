package com.classscore.dao;

import com.classscore.model.ScoreRecord;
import com.classscore.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩数据访问。成绩录入按“学生 + 课程 + 考试”唯一键做 upsert。
 */
public class ScoreDao {

    public Map<Integer, Double> loadScores(int courseId, String examName, String className)
            throws SQLException {
        String sql = "SELECT sc.student_id, sc.score FROM score sc "
                + "JOIN student s ON s.id = sc.student_id "
                + "WHERE sc.course_id = ? AND sc.exam_name = ? AND s.class_name = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, examName);
            ps.setString(3, className);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Double> scores = new LinkedHashMap<>();
                while (rs.next()) {
                    scores.put(rs.getInt(1), rs.getDouble(2));
                }
                return scores;
            }
        }
    }

    public List<ScoreRecord> listByStudent(int studentId) throws SQLException {
        String sql = "SELECT sc.id, sc.student_id, s.student_no, s.name AS student_name, s.class_name, "
                + "sc.course_id, c.code AS course_code, c.name AS course_name, sc.exam_name, sc.score, "
                + "t.name AS teacher_name, sc.updated_at "
                + "FROM score sc "
                + "JOIN student s ON s.id = sc.student_id "
                + "JOIN course c ON c.id = sc.course_id "
                + "LEFT JOIN teacher t ON t.id = sc.teacher_id "
                + "WHERE sc.student_id = ? "
                + "ORDER BY c.id, CASE WHEN sc.exam_name = '期中考试' THEN 0 ELSE 1 END";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ScoreRecord> records = new ArrayList<>();
                while (rs.next()) {
                    records.add(map(rs));
                }
                return records;
            }
        }
    }

    public List<ScoreRecord> listRecent(int limit) throws SQLException {
        String sql = "SELECT sc.id, sc.student_id, s.student_no, s.name AS student_name, s.class_name, "
                + "sc.course_id, c.code AS course_code, c.name AS course_name, sc.exam_name, sc.score, "
                + "t.name AS teacher_name, sc.updated_at "
                + "FROM score sc "
                + "JOIN student s ON s.id = sc.student_id "
                + "JOIN course c ON c.id = sc.course_id "
                + "LEFT JOIN teacher t ON t.id = sc.teacher_id "
                + "ORDER BY sc.updated_at DESC, sc.id DESC LIMIT ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<ScoreRecord> records = new ArrayList<>();
                while (rs.next()) {
                    records.add(map(rs));
                }
                return records;
            }
        }
    }

    /**
     * 批量保存。value 为 null 表示删除该条成绩。
     */
    public int saveBatch(int teacherId, int courseId, String examName,
                         Map<Integer, Double> scores) throws SQLException {
        String mergeSql = "MERGE INTO score (student_id, course_id, exam_name, score, teacher_id, updated_at) "
                + "KEY (student_id, course_id, exam_name) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String deleteSql = "DELETE FROM score WHERE student_id = ? AND course_id = ? AND exam_name = ?";
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            int affected = 0;
            try (PreparedStatement merge = connection.prepareStatement(mergeSql);
                 PreparedStatement delete = connection.prepareStatement(deleteSql)) {
                for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
                    if (entry.getValue() == null) {
                        delete.setInt(1, entry.getKey());
                        delete.setInt(2, courseId);
                        delete.setString(3, examName);
                        affected += delete.executeUpdate();
                    } else {
                        merge.setInt(1, entry.getKey());
                        merge.setInt(2, courseId);
                        merge.setString(3, examName);
                        merge.setDouble(4, entry.getValue());
                        merge.setInt(5, teacherId);
                        affected += merge.executeUpdate();
                    }
                }
                connection.commit();
                return affected;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM score";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private ScoreRecord map(ResultSet rs) throws SQLException {
        ScoreRecord record = new ScoreRecord();
        record.setId(rs.getInt("id"));
        record.setStudentId(rs.getInt("student_id"));
        record.setStudentNo(rs.getString("student_no"));
        record.setStudentName(rs.getString("student_name"));
        record.setClassName(rs.getString("class_name"));
        record.setCourseId(rs.getInt("course_id"));
        record.setCourseCode(rs.getString("course_code"));
        record.setCourseName(rs.getString("course_name"));
        record.setExamName(rs.getString("exam_name"));
        double value = rs.getDouble("score");
        record.setScore(rs.wasNull() ? null : value);
        record.setTeacherName(rs.getString("teacher_name"));
        record.setUpdatedAt(rs.getTimestamp("updated_at"));
        return record;
    }
}
