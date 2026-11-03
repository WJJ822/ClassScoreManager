package com.classscore.model;

/**
 * 排行榜条目。
 */
public class RankItem {

    private int rank;
    private int studentId;
    private String name;
    private String className;
    private double average;
    private int scoreCount;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(int scoreCount) {
        this.scoreCount = scoreCount;
    }

    public String getInitial() {
        return name == null || name.isEmpty() ? "?" : name.substring(0, 1);
    }

    public String getRankClass() {
        return rank == 1 ? "gold" : (rank == 2 ? "silver" : (rank == 3 ? "bronze" : "plain"));
    }

    public int getAverageWidth() {
        return (int) Math.round(Math.max(0, Math.min(100, average)));
    }
}
