package com.classscore.filter;

import com.classscore.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录与角色权限过滤器。
 */
@WebFilter(filterName = "authFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    public static final String SESSION_USER = "authUser";

    @Override
    public void init(FilterConfig filterConfig) {
        // 无需初始化
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            if (path.startsWith("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"ok\":false,\"message\":\"请先登录\"}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login?expired=1");
            }
            return;
        }

        if (path.startsWith("/teacher/") && !user.isTeacher()) {
            deny(req, resp, "教师页面仅对教师账号开放");
            return;
        }
        if (path.startsWith("/student/") && !user.isStudent()) {
            deny(req, resp, "学生页面仅对学生账号开放");
            return;
        }

        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.isEmpty()
                || "/".equals(path)
                || "/login".equals(path)
                || "/logout".equals(path)
                || "/favicon.ico".equals(path)
                || path.startsWith("/assets/")
                || path.startsWith("/error");
    }

    private void deny(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorCode", String.valueOf(HttpServletResponse.SC_FORBIDDEN));
        request.setAttribute("errorTitle", "没有访问权限");
        request.setAttribute("errorMessage", message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        // 无需释放
    }
}
