package com.classscore.servlet;

import com.classscore.filter.AuthFilter;
import com.classscore.model.User;
import com.classscore.service.AuthService;
import com.classscore.service.ServiceException;
import com.classscore.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录页与登录提交。
 */
@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User current = currentUser(request);
        if (current != null) {
            redirect(request, response, authService.homePath(current));
            return;
        }
        if ("1".equals(request.getParameter("expired"))) {
            request.setAttribute("errorMessage", "登录状态已过期，请重新登录");
        }
        if ("1".equals(request.getParameter("logout"))) {
            request.setAttribute("infoMessage", "你已安全退出系统");
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        try {
            User user = authService.login(username, password, role);
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(AuthFilter.SESSION_USER, user);
            session.setMaxInactiveInterval(30 * 60);
            WebUtil.flash(session, "success", "欢迎回来，" + user.getDisplayName());
            redirect(request, response, authService.homePath(user));
        } catch (ServiceException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.setAttribute("username", username);
            request.setAttribute("role", WebUtil.blankToNull(role));
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
