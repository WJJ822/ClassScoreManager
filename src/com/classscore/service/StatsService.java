package com.classscore.service;

import com.classscore.dao.StatsDao;
import com.classscore.model.ClassStat;
import com.classscore.model.CourseStat;
import com.classscore.model.Distribution;
import com.classscore.model.Overview;
import com.classscore.model.RankItem;
import com.classscore.model.StudentSummary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计业务。全部指标由 SQL 聚合，避免在页面里做重复计算。
 */
public class StatsService {

    private final StatsDao statsDao = new StatsDao();

    public Overview overview(String className) {
        try {
            return statsDao.overview(className);
        } catch (SQLException ex) {
            throw new ServiceException("读取总览统计失败", ex);
        }
    }

    public List<ClassStat> classStats() {
        try {
            return statsDao.classStats();
        } catch (SQLException ex) {
            throw new ServiceException("读取班级统计失败", ex);
        }
    }

    public List<CourseStat> courseStats(String className) {
        try {
            return statsDao.courseStats(className);
        } catch (SQLException ex) {
            throw new ServiceException("读取课程统计失败", ex);
        }
    }

    public List<Distribution> distribution(String className) {
        try {
            return statsDao.distribution(className);
        } catch (SQLException ex) {
            throw new ServiceException("读取成绩分布失败", ex);
        }
    }

    public List<RankItem> topStudents(String className, int limit) {
        try {
            return statsDao.topStudents(className, limit);
        } catch (SQLException ex) {
            throw new ServiceException("读取排行榜失败", ex);
        }
    }

    public List<StudentSummary> summaries(String keyword, String className) {
        try {
            List<StudentSummary> all = statsDao.allStudentSummaries();
            String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
            String normalizedClass = className == null ? "" : className.trim();
            List<StudentSummary> filtered = new ArrayList<>();
            for (StudentSummary summary : all) {
                if (!normalizedClass.isEmpty() && !"all".equalsIgnoreCase(normalizedClass)
                        && !normalizedClass.equals(summary.getClassName())) {
                    continue;
                }
                if (!normalizedKeyword.isEmpty()
                        && !summary.getName().toLowerCase().contains(normalizedKeyword)
                        && !summary.getStudentNo().toLowerCase().contains(normalizedKeyword)) {
                    continue;
                }
                filtered.add(summary);
            }
            return filtered;
        } catch (SQLException ex) {
            throw new ServiceException("读取学生成绩汇总失败", ex);
        }
    }

    public StudentSummary summaryFor(int studentId) {
        try {
            for (StudentSummary summary : statsDao.allStudentSummaries()) {
                if (summary.getStudentId() == studentId) {
                    return summary;
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new ServiceException("读取学生成绩汇总失败", ex);
        }
    }

    public Map<Integer, Double> courseAverages(String className) {
        try {
            return statsDao.courseAverages(className);
        } catch (SQLException ex) {
            throw new ServiceException("读取班级课程均分失败", ex);
        }
    }

    public double classAverage(String className) {
        try {
            return statsDao.classAverage(className);
        } catch (SQLException ex) {
            throw new ServiceException("读取班级平均分失败", ex);
        }
    }
}
