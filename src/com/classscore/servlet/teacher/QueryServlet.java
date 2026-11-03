package com.classscore.servlet.teacher;

import com.classscore.dao.StudentDao;
import com.classscore.model.StudentSummary;
import com.classscore.service.ServiceException;
import com.classscore.service.StatsService;
import com.classscore.servlet.BaseServlet;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 教师按姓名、学号、班级查询学生成绩。
 */
@WebServlet(name = "teacherQueryServlet", urlPatterns = "/teacher/query")
public class QueryServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StatsService statsService = new StatsService();
    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = WebUtil.blankToNull(request.getParameter("keyword"));
            String className = WebUtil.blankToNull(request.getParameter("class"));
            if (className != null && "all".equals(className)) {
                className = null;
            }
            List<StudentSummary> summaries = statsService.summaries(keyword, className);
            double total = 0;
            int pass = 0;
            for (StudentSummary summary : summaries) {
                total += summary.getAverage();
                if (summary.getScoreCount() > 0 && summary.getPassRate() >= 100) {
                    pass++;
                }
            }
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedClass", className == null ? "all" : className);
            request.setAttribute("classes", studentDao.listClasses());
            request.setAttribute("summaries", summaries);
            request.setAttribute("resultCount", summaries.size());
            request.setAttribute("resultAverage", summaries.isEmpty() ? 0 : total / summaries.size());
            request.setAttribute("resultPassCount", pass);
            request.setAttribute("pageTitle", "成绩查询");
            request.getRequestDispatcher("/WEB-INF/views/teacher/query.jsp").forward(request, response);
        } catch (ServiceException | SQLException ex) {
            forwardError(request, response, 500, "成绩查询失败", ex.getMessage());
        }
    }
}
