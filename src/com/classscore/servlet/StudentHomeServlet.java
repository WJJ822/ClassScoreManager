package com.classscore.servlet;

import com.classscore.dao.StudentDao;
import com.classscore.model.Student;
import com.classscore.model.StudentSummary;
import com.classscore.model.User;
import com.classscore.service.ScoreService;
import com.classscore.service.ServiceException;
import com.classscore.service.StatsService;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 学生端“我的成绩”。只读取当前登录学生本人数据。
 */
@WebServlet(name = "studentHomeServlet", urlPatterns = "/student/home")
public class StudentHomeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDao studentDao = new StudentDao();
    private final StatsService statsService = new StatsService();
    private final ScoreService scoreService = new ScoreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null || !user.isStudent()) {
            redirect(request, response, "/login");
            return;
        }
        try {
            Student student = studentDao.findById(user.getRefId());
            if (student == null) {
                forwardError(request, response, 404, "学生信息不存在", "请联系任课教师核对账号");
                return;
            }
            StudentSummary summary = statsService.summaryFor(student.getId());
            Map<Integer, Double> classAverages = statsService.courseAverages(student.getClassName());
            request.setAttribute("student", student);
            request.setAttribute("summary", summary);
            request.setAttribute("scores", scoreService.listStudentScores(student.getId()));
            request.setAttribute("classAverages", classAverages);
            request.setAttribute("classAverage", statsService.classAverage(student.getClassName()));
            request.setAttribute("classSize", studentDao.listByClass(student.getClassName()).size());
            int hour = java.time.LocalTime.now().getHour();
            request.setAttribute("greeting", hour < 11 ? "早上好" : (hour < 14 ? "中午好" : (hour < 18 ? "下午好" : "晚上好")));
            request.setAttribute("todayLabel", new java.text.SimpleDateFormat(
                    "M月d日 EEEE", java.util.Locale.CHINA).format(new java.util.Date()));
            request.setAttribute("pageTitle", "我的成绩");
            request.getRequestDispatcher("/WEB-INF/views/student/home.jsp").forward(request, response);
        } catch (ServiceException | SQLException ex) {
            forwardError(request, response, 500, "个人成绩加载失败", ex.getMessage());
        }
    }
}
