package com.classscore.model;

/**
 * 学生成绩汇总，用于查询列表和个人成绩页。
 */
public class StudentSummary {

    private int studentId;
    private String studentNo;
    private String name;
    private String className;
    private String gender;
    private double average;
    private double passRate;
    private double excellentRate;
    private int scoreCount;
    private int rank;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public double getExcellentRate() {
        return excellentRate;
    }

    public void setExcellentRate(double excellentRate) {
        this.excellentRate = excellentRate;
    }

    public int getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(int scoreCount) {
        this.scoreCount = scoreCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getInitial() {
        return name == null || name.isEmpty() ? "?" : name.substring(0, 1);
    }

    public String getStatusLabel() {
        if (scoreCount == 0) {
            return "暂无成绩";
        }
        return passRate >= 100 ? "全部及格" : (passRate >= 80 ? "状态良好" : "需要关注");
    }

    public String getStatusClass() {
        if (scoreCount == 0) {
            return "muted";
        }
        return passRate >= 100 ? "success" : (passRate >= 80 ? "info" : "warning");
    }

    public int getAverageWidth() {
        return (int) Math.round(Math.max(0, Math.min(100, average)));
    }
}
