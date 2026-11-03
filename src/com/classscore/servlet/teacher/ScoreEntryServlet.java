package com.classscore.servlet.teacher;

import com.classscore.model.Course;
import com.classscore.service.ScoreService;
import com.classscore.service.ServiceException;
import com.classscore.servlet.BaseServlet;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师成绩录入：GET 渲染整班录入表，POST 批量 upsert。
 */
@WebServlet(name = "teacherScoresServlet", urlPatterns = "/teacher/scores")
public class ScoreEntryServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ScoreService scoreService = new ScoreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<String> classes = scoreService.listClassNames();
            List<Course> courses = scoreService.listCourses();
            if (classes.isEmpty() || courses.isEmpty()) {
                forwardError(request, response, 500, "基础数据缺失", "请先维护班级与课程数据");
                return;
            }
            String className = request.getParameter("class");
            if (className == null || !classes.contains(className)) {
                className = classes.get(0);
            }
            final int requestedCourseId = WebUtil.intOrDefault(
                    request.getParameter("courseId"), courses.get(0).getId());
            Course selectedCourse = courses.stream()
                    .filter(course -> course.getId() == requestedCourseId)
                    .findFirst().orElse(courses.get(0));
            int courseId = selectedCourse.getId();
            courseId = selectedCourse.getId();
            String exam = request.getParameter("exam");
            if (exam == null || !Arrays.asList(ScoreService.EXAMS).contains(exam)) {
                exam = "期末考试";
            }

            request.setAttribute("classes", classes);
            request.setAttribute("courses", courses);
            request.setAttribute("selectedClass", className);
            request.setAttribute("selectedCourse", selectedCourse);
            request.setAttribute("selectedCourseId", courseId);
            request.setAttribute("selectedExam", exam);
            request.setAttribute("exams", ScoreService.EXAMS);
            request.setAttribute("rows", scoreService.loadEntryRows(className, courseId, exam));
            request.setAttribute("pageTitle", "成绩录入");
            request.getRequestDispatcher("/WEB-INF/views/teacher/scores.jsp").forward(request, response);
        } catch (ServiceException ex) {
            forwardError(request, response, 500, "成绩录入页加载失败", ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String className = request.getParameter("class");
        int courseId = WebUtil.intOrDefault(request.getParameter("courseId"), 0);
        String exam = request.getParameter("exam");
        Map<String, String> rawScores = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (entry.getKey().startsWith("score_") && entry.getValue().length > 0) {
                rawScores.put(entry.getKey().substring("score_".length()), entry.getValue()[0]);
            }
        }
        try {
            int saved = scoreService.saveBatch(teacherId(request), className, courseId, exam, rawScores);
            WebUtil.flash(request.getSession(), "success", "成绩已保存，共更新 " + saved + " 条记录");
        } catch (ServiceException ex) {
            WebUtil.flash(request.getSession(), "error", ex.getMessage());
        }
        String target = "/teacher/scores?class=" + URLEncoder.encode(
                className == null ? "" : className, StandardCharsets.UTF_8)
                + "&courseId=" + courseId
                + "&exam=" + URLEncoder.encode(exam == null ? "" : exam, StandardCharsets.UTF_8);
        redirect(request, response, target);
    }
}
