package com.classscore.servlet;

import com.classscore.filter.AuthFilter;
import com.classscore.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet 基类，集中处理当前用户与错误页。
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(AuthFilter.SESSION_USER);
    }

    protected int teacherId(HttpServletRequest request) {
        User user = currentUser(request);
        if (user == null || !user.isTeacher()) {
            throw new IllegalStateException("当前会话不是教师");
        }
        return user.getRefId();
    }

    protected void forwardError(HttpServletRequest request, HttpServletResponse response,
                                int status, String title, String message)
            throws ServletException, IOException {
        request.setAttribute("errorCode", String.valueOf(status));
        request.setAttribute("errorTitle", title);
        request.setAttribute("errorMessage", message);
        response.setStatus(status);
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
