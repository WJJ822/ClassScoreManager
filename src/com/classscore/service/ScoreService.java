package com.classscore.service;

import com.classscore.dao.CourseDao;
import com.classscore.dao.ScoreDao;
import com.classscore.dao.StudentDao;
import com.classscore.model.Course;
import com.classscore.model.ScoreEntryRow;
import com.classscore.model.ScoreRecord;
import com.classscore.model.Student;
import com.classscore.util.WebUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务：录入视图、批量保存、学生成绩查询。
 */
public class ScoreService {

    public static final String[] EXAMS = {"期中考试", "期末考试"};

    private final StudentDao studentDao = new StudentDao();
    private final CourseDao courseDao = new CourseDao();
    private final ScoreDao scoreDao = new ScoreDao();

    public List<String> listClassNames() {
        try {
            return studentDao.listClasses();
        } catch (SQLException ex) {
            throw new ServiceException("读取班级列表失败", ex);
        }
    }

    public List<Course> listCourses() {
        try {
            return courseDao.listAll();
        } catch (SQLException ex) {
            throw new ServiceException("读取课程列表失败", ex);
        }
    }

    public List<ScoreEntryRow> loadEntryRows(String className, int courseId, String examName) {
        try {
            List<Student> students = studentDao.listByClass(className);
            Map<Integer, Double> scores = scoreDao.loadScores(courseId, examName, className);
            List<ScoreEntryRow> rows = new ArrayList<>();
            for (Student student : students) {
                rows.add(new ScoreEntryRow(student, scores.get(student.getId())));
            }
            return rows;
        } catch (SQLException ex) {
            throw new ServiceException("读取成绩录入表失败", ex);
        }
    }

    public int saveBatch(int teacherId, String className, int courseId, String examName,
                         Map<String, String> rawScores) {
        if (WebUtil.blankToNull(className) == null) {
            throw new ServiceException("请选择班级");
        }
        if (WebUtil.blankToNull(examName) == null) {
            throw new ServiceException("请选择考试");
        }
        try {
            Course course = courseDao.findById(courseId);
            if (course == null) {
                throw new ServiceException("课程不存在");
            }
            List<Student> students = studentDao.listByClass(className);
            if (students.isEmpty()) {
                throw new ServiceException("该班级暂无学生");
            }
            Map<Integer, Double> values = new LinkedHashMap<>();
            for (Student student : students) {
                String raw = rawScores.get(String.valueOf(student.getId()));
                if (raw == null) {
                    continue;
                }
                String trimmed = raw.trim();
                if (trimmed.isEmpty()) {
                    values.put(student.getId(), null);
                    continue;
                }
                double score;
                try {
                    score = Double.parseDouble(trimmed);
                } catch (NumberFormatException ex) {
                    throw new ServiceException("「" + student.getName() + "」的分数格式不正确");
                }
                if (score < 0 || score > 100) {
                    throw new ServiceException("「" + student.getName() + "」的分数必须在 0 到 100 之间");
                }
                values.put(student.getId(), Math.round(score * 10.0) / 10.0);
            }
            if (values.isEmpty()) {
                throw new ServiceException("没有可保存的成绩");
            }
            int affected = scoreDao.saveBatch(teacherId, courseId, examName, values);
            return Math.max(affected, values.size());
        } catch (SQLException ex) {
            throw new ServiceException("保存成绩失败，请稍后重试", ex);
        }
    }

    public List<ScoreRecord> listStudentScores(int studentId) {
        try {
            return scoreDao.listByStudent(studentId);
        } catch (SQLException ex) {
            throw new ServiceException("读取成绩失败", ex);
        }
    }

    public List<ScoreRecord> listRecent(int limit) {
        try {
            return scoreDao.listRecent(limit);
        } catch (SQLException ex) {
            throw new ServiceException("读取最近成绩失败", ex);
        }
    }
}
