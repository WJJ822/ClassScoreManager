package com.classscore.dao;

import com.classscore.model.ClassStat;
import com.classscore.model.CourseStat;
import com.classscore.model.Distribution;
import com.classscore.model.Overview;
import com.classscore.model.RankItem;
import com.classscore.model.StudentSummary;
import com.classscore.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计查询。平均分、及格率、优秀率都在数据库端聚合。
 */
public class StatsDao {

    public Overview overview(String className) throws SQLException {
        boolean filtered = hasText(className);
        String where = filtered ? " WHERE s.class_name = ?" : "";
        String sql = "SELECT COUNT(DISTINCT s.id) AS student_count, "
                + "COUNT(sc.id) AS score_count, "
                + "COALESCE(AVG(sc.score), 0) AS avg_score, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS pass_rate, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS excellent_rate, "
                + "COALESCE(SUM(CASE WHEN sc.score < 60 THEN 1 ELSE 0 END), 0) AS fail_count "
                + "FROM student s LEFT JOIN score sc ON sc.student_id = s.id" + where;
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (filtered) {
                ps.setString(1, className.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                Overview overview = new Overview();
                if (rs.next()) {
                    overview.setStudentCount(rs.getInt("student_count"));
                    overview.setScoreCount(rs.getInt("score_count"));
                    overview.setAverage(rs.getDouble("avg_score"));
                    overview.setPassRate(rs.getDouble("pass_rate"));
                    overview.setExcellentRate(rs.getDouble("excellent_rate"));
                    overview.setFailCount(rs.getInt("fail_count"));
                }
                overview.setClassCount(filtered ? 1
                        : count("SELECT COUNT(DISTINCT class_name) FROM student"));
                overview.setCourseCount(count("SELECT COUNT(*) FROM course"));
                return overview;
            }
        }
    }

    public List<ClassStat> classStats() throws SQLException {
        String sql = "SELECT s.class_name, COUNT(DISTINCT s.id) AS student_count, "
                + "COUNT(sc.id) AS score_count, COALESCE(AVG(sc.score), 0) AS avg_score, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS pass_rate, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS excellent_rate "
                + "FROM student s LEFT JOIN score sc ON sc.student_id = s.id "
                + "GROUP BY s.class_name ORDER BY s.class_name";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ClassStat> stats = new ArrayList<>();
            while (rs.next()) {
                ClassStat stat = new ClassStat();
                stat.setClassName(rs.getString("class_name"));
                stat.setStudentCount(rs.getInt("student_count"));
                stat.setScoreCount(rs.getInt("score_count"));
                stat.setAverage(rs.getDouble("avg_score"));
                stat.setPassRate(rs.getDouble("pass_rate"));
                stat.setExcellentRate(rs.getDouble("excellent_rate"));
                stats.add(stat);
            }
            return stats;
        }
    }

    public List<CourseStat> courseStats(String className) throws SQLException {
        boolean filtered = hasText(className);
        String sql = "SELECT c.id, c.code, c.name, COUNT(sc.id) AS score_count, "
                + "COALESCE(AVG(sc.score), 0) AS avg_score, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS pass_rate, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS excellent_rate, "
                + "COALESCE(MAX(sc.score), 0) AS max_score, COALESCE(MIN(sc.score), 0) AS min_score "
                + "FROM course c LEFT JOIN score sc ON sc.course_id = c.id "
                + "LEFT JOIN student s ON s.id = sc.student_id "
                + (filtered ? "WHERE s.class_name = ? " : "")
                + "GROUP BY c.id, c.code, c.name ORDER BY c.id";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (filtered) {
                ps.setString(1, className.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<CourseStat> stats = new ArrayList<>();
                int index = 0;
                String[] accents = {"blue", "violet", "mint", "amber"};
                while (rs.next()) {
                    CourseStat stat = new CourseStat();
                    stat.setCourseId(rs.getInt("id"));
                    stat.setCode(rs.getString("code"));
                    stat.setName(rs.getString("name"));
                    stat.setScoreCount(rs.getInt("score_count"));
                    stat.setAverage(rs.getDouble("avg_score"));
                    stat.setPassRate(rs.getDouble("pass_rate"));
                    stat.setExcellentRate(rs.getDouble("excellent_rate"));
                    stat.setMaxScore(rs.getDouble("max_score"));
                    stat.setMinScore(rs.getDouble("min_score"));
                    stat.setAccent(accents[index % accents.length]);
                    stats.add(stat);
                    index++;
                }
                return stats;
            }
        }
    }

    public List<Distribution> distribution(String className) throws SQLException {
        boolean filtered = hasText(className);
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN sc.score < 60 THEN 1 ELSE 0 END), 0) AS d0, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 60 AND sc.score < 70 THEN 1 ELSE 0 END), 0) AS d1, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 70 AND sc.score < 80 THEN 1 ELSE 0 END), 0) AS d2, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 80 AND sc.score < 90 THEN 1 ELSE 0 END), 0) AS d3, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END), 0) AS d4 "
                + "FROM score sc JOIN student s ON s.id = sc.student_id"
                + (filtered ? " WHERE s.class_name = ?" : "");
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            if (filtered) {
                ps.setString(1, className.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                int[] counts = new int[5];
                if (rs.next()) {
                    for (int i = 0; i < counts.length; i++) {
                        counts[i] = rs.getInt("d" + i);
                    }
                }
                int total = 0;
                int max = 0;
                for (int count : counts) {
                    total += count;
                    max = Math.max(max, count);
                }
                String[] labels = {"不及格", "及格", "中等", "良好", "优秀"};
                String[] ranges = {"0–59", "60–69", "70–79", "80–89", "90–100"};
                String[] tones = {"fail", "pass", "mid", "good", "great"};
                List<Distribution> result = new ArrayList<>();
                for (int i = 0; i < counts.length; i++) {
                    Distribution item = new Distribution(labels[i], ranges[i], tones[i]);
                    item.setCount(counts[i]);
                    item.setPercent(total == 0 ? 0 : counts[i] * 100.0 / total);
                    item.setBarHeight(max == 0 ? 0 : Math.max(counts[i] == 0 ? 0 : 8,
                            counts[i] * 100.0 / max));
                    result.add(item);
                }
                return result;
            }
        }
    }

    public List<RankItem> topStudents(String className, int limit) throws SQLException {
        boolean filtered = hasText(className);
        String sql = "SELECT s.id, s.name, s.class_name, AVG(sc.score) AS avg_score, "
                + "COUNT(sc.id) AS score_count "
                + "FROM student s JOIN score sc ON sc.student_id = s.id "
                + (filtered ? "WHERE s.class_name = ? " : "")
                + "GROUP BY s.id, s.name, s.class_name "
                + "ORDER BY avg_score DESC, s.student_no LIMIT ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (filtered) {
                ps.setString(index++, className.trim());
            }
            ps.setInt(index, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<RankItem> ranking = new ArrayList<>();
                int rank = 1;
                while (rs.next()) {
                    RankItem item = new RankItem();
                    item.setRank(rank++);
                    item.setStudentId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setClassName(rs.getString("class_name"));
                    item.setAverage(rs.getDouble("avg_score"));
                    item.setScoreCount(rs.getInt("score_count"));
                    ranking.add(item);
                }
                return ranking;
            }
        }
    }

    public List<StudentSummary> allStudentSummaries() throws SQLException {
        String sql = "SELECT s.id, s.student_no, s.name, s.class_name, s.gender, "
                + "COUNT(sc.id) AS score_count, COALESCE(AVG(sc.score), 0) AS avg_score, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS pass_rate, "
                + "COALESCE(SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END) * 100.0 "
                + "  / NULLIF(COUNT(sc.id), 0), 0) AS excellent_rate "
                + "FROM student s LEFT JOIN score sc ON sc.student_id = s.id "
                + "GROUP BY s.id, s.student_no, s.name, s.class_name, s.gender "
                + "ORDER BY s.class_name, avg_score DESC, s.student_no";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<StudentSummary> summaries = new ArrayList<>();
            while (rs.next()) {
                StudentSummary summary = new StudentSummary();
                summary.setStudentId(rs.getInt("id"));
                summary.setStudentNo(rs.getString("student_no"));
                summary.setName(rs.getString("name"));
                summary.setClassName(rs.getString("class_name"));
                summary.setGender(rs.getString("gender"));
                summary.setScoreCount(rs.getInt("score_count"));
                summary.setAverage(rs.getDouble("avg_score"));
                summary.setPassRate(rs.getDouble("pass_rate"));
                summary.setExcellentRate(rs.getDouble("excellent_rate"));
                summaries.add(summary);
            }
            assignRanks(summaries);
            return summaries;
        }
    }

    public Map<Integer, Double> courseAverages(String className) throws SQLException {
        String sql = "SELECT sc.course_id, AVG(sc.score) AS avg_score "
                + "FROM score sc JOIN student s ON s.id = sc.student_id "
                + "WHERE s.class_name = ? GROUP BY sc.course_id";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, className);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Double> averages = new HashMap<>();
                while (rs.next()) {
                    averages.put(rs.getInt("course_id"), rs.getDouble("avg_score"));
                }
                return averages;
            }
        }
    }

    public double classAverage(String className) throws SQLException {
        String sql = "SELECT COALESCE(AVG(sc.score), 0) FROM score sc "
                + "JOIN student s ON s.id = sc.student_id WHERE s.class_name = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, className);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private void assignRanks(List<StudentSummary> summaries) {
        String currentClass = null;
        int rank = 0;
        double previousAverage = -1;
        int indexInClass = 0;
        for (StudentSummary summary : summaries) {
            if (!summary.getClassName().equals(currentClass)) {
                currentClass = summary.getClassName();
                rank = 0;
                indexInClass = 0;
                previousAverage = -1;
            }
            indexInClass++;
            if (summary.getScoreCount() == 0) {
                summary.setRank(0);
                continue;
            }
            if (summary.getAverage() != previousAverage) {
                rank = indexInClass;
                previousAverage = summary.getAverage();
            }
            summary.setRank(rank);
        }
    }

    private int count(String sql) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty() && !"all".equalsIgnoreCase(value.trim());
    }
}
