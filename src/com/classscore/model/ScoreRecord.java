package com.classscore.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一条成绩记录（含学生、课程、教师联表信息）。
 */
public class ScoreRecord {

    private int id;
    private int studentId;
    private String studentNo;
    private String studentName;
    private String className;
    private int courseId;
    private String courseCode;
    private String courseName;
    private String examName;
    private Double score;
    private String teacherName;
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getScoreText() {
        return score == null ? "—" : String.format("%.1f", score);
    }

    public String getInitial() {
        return studentName == null || studentName.isEmpty() ? "?" : studentName.substring(0, 1);
    }

    public int getScoreWidth() {
        if (score == null) {
            return 0;
        }
        return (int) Math.round(Math.max(0, Math.min(100, score)));
    }

    public String getGrade() {
        if (score == null) {
            return "未录入";
        }
        if (score >= 90) {
            return "优秀";
        }
        if (score >= 80) {
            return "良好";
        }
        if (score >= 70) {
            return "中等";
        }
        if (score >= 60) {
            return "及格";
        }
        return "不及格";
    }

    public String getGradeClass() {
        if (score == null) {
            return "none";
        }
        if (score >= 90) {
            return "great";
        }
        if (score >= 80) {
            return "good";
        }
        if (score >= 70) {
            return "mid";
        }
        if (score >= 60) {
            return "pass";
        }
        return "fail";
    }

    public String getUpdatedText() {
        return updatedAt == null ? "" : new SimpleDateFormat("MM-dd HH:mm").format(updatedAt);
    }
}
