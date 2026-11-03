package com.classscore.model;

/**
 * 成绩录入表格中的一行。
 */
public class ScoreEntryRow {

    private Student student;
    private Double score;

    public ScoreEntryRow() {
    }

    public ScoreEntryRow(Student student, Double score) {
        this.student = student;
        this.score = score;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isRecorded() {
        return score != null;
    }

    public String getScoreText() {
        return score == null ? "" : (score == Math.rint(score)
                ? String.valueOf(score.intValue()) : String.format("%.1f", score));
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
}
