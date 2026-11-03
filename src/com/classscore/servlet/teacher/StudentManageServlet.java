package com.classscore.servlet.teacher;

import com.classscore.dao.StudentDao;
import com.classscore.model.Student;
import com.classscore.service.ServiceException;
import com.classscore.service.StatsService;
import com.classscore.servlet.BaseServlet;
import com.classscore.util.PasswordUtil;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * 学生管理与成绩查询使用的学生列表。
 */
@WebServlet(name = "teacherStudentsServlet", urlPatterns = "/teacher/students")
public class StudentManageServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern STUDENT_NO = Pattern.compile("^[A-Za-z0-9]{4,30}$");

    private final StudentDao studentDao = new StudentDao();
    private final StatsService statsService = new StatsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = WebUtil.blankToNull(request.getParameter("keyword"));
            String className = WebUtil.blankToNull(request.getParameter("class"));
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedClass", className == null ? "all" : className);
            request.setAttribute("classes", studentDao.listClasses());
            request.setAttribute("students", statsService.summaries(keyword, className));
            request.setAttribute("pageTitle", "学生管理");
            request.getRequestDispatcher("/WEB-INF/views/teacher/students.jsp").forward(request, response);
        } catch (SQLException ex) {
            forwardError(request, response, 500, "学生列表加载失败", ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                addStudent(request);
            } else if ("delete".equals(action)) {
                deleteStudent(request);
            } else {
                throw new ServiceException("未知操作");
            }
        } catch (ServiceException ex) {
            WebUtil.flash(request.getSession(), "error", ex.getMessage());
        } catch (SQLException ex) {
            WebUtil.flash(request.getSession(), "error", "数据库操作失败：" + ex.getMessage());
        }
        String keyword = request.getParameter("keyword");
        String className = request.getParameter("class");
        String target = "/teacher/students?";
        if (keyword != null && !keyword.trim().isEmpty()) {
            target += "keyword=" + java.net.URLEncoder.encode(keyword,
                    java.nio.charset.StandardCharsets.UTF_8) + "&";
        }
        if (className != null && !className.trim().isEmpty()) {
            target += "class=" + java.net.URLEncoder.encode(className,
                    java.nio.charset.StandardCharsets.UTF_8);
        }
        redirect(request, response, target);
    }

    private void addStudent(HttpServletRequest request) throws SQLException {
        String no = WebUtil.blankToNull(request.getParameter("studentNo"));
        String name = WebUtil.blankToNull(request.getParameter("name"));
        String className = WebUtil.blankToNull(request.getParameter("className"));
        String gender = WebUtil.blankToNull(request.getParameter("gender"));
        if (no == null || !STUDENT_NO.matcher(no).matches()) {
            throw new ServiceException("学号需为 4–30 位字母或数字");
        }
        if (name == null || name.length() > 20) {
            throw new ServiceException("请输入 1–20 个字符的姓名");
        }
        if (className == null || className.length() > 40) {
            throw new ServiceException("请输入有效的班级名称");
        }
        if (gender == null || !("男".equals(gender) || "女".equals(gender) || "未填写".equals(gender))) {
            gender = "未填写";
        }
        if (studentDao.existsByNo(no)) {
            throw new ServiceException("学号 " + no + " 已存在");
        }
        Student student = new Student();
        student.setStudentNo(no);
        student.setName(name);
        student.setClassName(className);
        student.setGender(gender);
        studentDao.insert(student, no, PasswordUtil.hash("123456"));
        WebUtil.flash(request.getSession(), "success",
                "已添加学生「" + name + "」，初始密码 123456");
    }

    private void deleteStudent(HttpServletRequest request) throws SQLException {
        Integer id = WebUtil.intOrNull(request.getParameter("id"));
        if (id == null) {
            throw new ServiceException("学生编号无效");
        }
        Student student = studentDao.findById(id);
        if (student == null) {
            throw new ServiceException("学生不存在或已被删除");
        }
        studentDao.delete(id);
        WebUtil.flash(request.getSession(), "success", "已删除学生「" + student.getName() + "」");
    }
}
