package com.classscore.servlet.teacher;

import com.classscore.dao.StudentDao;
import com.classscore.model.Student;
import com.classscore.model.StudentSummary;
import com.classscore.service.ServiceException;
import com.classscore.service.ScoreService;
import com.classscore.service.StatsService;
import com.classscore.servlet.BaseServlet;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 单个学生的成绩详情。
 */
@WebServlet(name = "teacherStudentDetailServlet", urlPatterns = "/teacher/student")
public class StudentDetailServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDao studentDao = new StudentDao();
    private final StatsService statsService = new StatsService();
    private final ScoreService scoreService = new ScoreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = WebUtil.intOrNull(request.getParameter("id"));
        if (id == null) {
            forwardError(request, response, 400, "参数不正确", "缺少学生编号");
            return;
        }
        try {
            Student student = studentDao.findById(id);
            if (student == null) {
                forwardError(request, response, 404, "学生不存在", "该学生可能已被删除");
                return;
            }
            StudentSummary summary = statsService.summaryFor(id);
            Map<Integer, Double> classAverages = statsService.courseAverages(student.getClassName());
            request.setAttribute("student", student);
            request.setAttribute("summary", summary);
            request.setAttribute("scores", scoreService.listStudentScores(id));
            request.setAttribute("classAverages", classAverages);
            request.setAttribute("classAverage", statsService.classAverage(student.getClassName()));
            request.setAttribute("classSize", studentDao.listByClass(student.getClassName()).size());
            request.setAttribute("pageTitle", "学生成绩详情");
            request.getRequestDispatcher("/WEB-INF/views/teacher/student-detail.jsp").forward(request, response);
        } catch (ServiceException | SQLException ex) {
            forwardError(request, response, 500, "学生成绩加载失败", ex.getMessage());
        }
    }
}
