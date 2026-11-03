package com.classscore.servlet.teacher;

import com.classscore.model.Overview;
import com.classscore.service.ServiceException;
import com.classscore.service.StatsService;
import com.classscore.servlet.BaseServlet;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统计分析页：平均分、及格率、优秀率、分布、课程与班级对比。
 */
@WebServlet(name = "teacherStatsServlet", urlPatterns = "/teacher/stats")
public class StatsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StatsService statsService = new StatsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            java.util.List<String> classes = new java.util.ArrayList<>();
            classes.addAll(new com.classscore.dao.StudentDao().listClasses());
            String selectedClass = WebUtil.blankToNull(request.getParameter("class"));
            if (selectedClass == null || (!"all".equals(selectedClass) && !classes.contains(selectedClass))) {
                selectedClass = "all";
            }
            String filter = "all".equals(selectedClass) ? null : selectedClass;
            Overview overview = statsService.overview(filter);
            request.setAttribute("classes", classes);
            request.setAttribute("selectedClass", selectedClass);
            request.setAttribute("overview", overview);
            request.setAttribute("passCount", Math.max(0, overview.getScoreCount() - overview.getFailCount()));
            request.setAttribute("classStats", statsService.classStats());
            request.setAttribute("courseStats", statsService.courseStats(filter));
            request.setAttribute("distribution", statsService.distribution(filter));
            request.setAttribute("topStudents", statsService.topStudents(filter, 8));
            request.setAttribute("pageTitle", "统计分析");
            request.getRequestDispatcher("/WEB-INF/views/teacher/stats.jsp").forward(request, response);
        } catch (ServiceException | java.sql.SQLException ex) {
            forwardError(request, response, 500, "统计分析加载失败", ex.getMessage());
        }
    }
}
