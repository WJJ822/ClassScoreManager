package com.classscore.servlet.teacher;

import com.classscore.model.Overview;
import com.classscore.service.StatsService;
import com.classscore.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

/**
 * 教师工作台总览。
 */
@WebServlet(name = "teacherDashboardServlet", urlPatterns = "/teacher/dashboard")
public class DashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StatsService statsService = new StatsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Overview overview = statsService.overview("all");
            request.setAttribute("overview", overview);
            request.setAttribute("classStats", statsService.classStats());
            request.setAttribute("courseStats", statsService.courseStats("all"));
            request.setAttribute("distribution", statsService.distribution("all"));
            request.setAttribute("topStudents", statsService.topStudents("all", 5));
            request.setAttribute("recentScores",
                    new com.classscore.service.ScoreService().listRecent(7));
            request.setAttribute("todayLabel",
                    new SimpleDateFormat("M月d日 EEEE", Locale.CHINA).format(new Date()));
            request.setAttribute("greeting", greeting());
            request.setAttribute("pageTitle", "数据总览");
            request.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(request, response);
        } catch (RuntimeException ex) {
            forwardError(request, response, 500, "数据加载失败", ex.getMessage());
        }
    }

    private String greeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 5) {
            return "夜深了";
        }
        if (hour < 11) {
            return "早上好";
        }
        if (hour < 14) {
            return "中午好";
        }
        if (hour < 18) {
            return "下午好";
        }
        return "晚上好";
    }
}
