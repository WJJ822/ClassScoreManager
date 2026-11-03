package com.classscore.service;

import com.classscore.dao.UserDao;
import com.classscore.model.User;
import com.classscore.util.PasswordUtil;
import com.classscore.util.WebUtil;

import java.sql.SQLException;

/**
 * 登录校验。
 */
public class AuthService {

    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    private final UserDao userDao = new UserDao();

    public User login(String username, String password, String expectedRole) {
        String account = WebUtil.blankToNull(username);
        if (account == null || password == null || password.isEmpty()) {
            throw new ServiceException("请输入账号和密码");
        }
        try {
            User user = userDao.findByUsername(account);
            String hash = userDao.findPasswordHash(account);
            if (user == null || hash == null || !PasswordUtil.verify(password, hash)) {
                throw new ServiceException("账号或密码不正确，请重新输入");
            }
            if (expectedRole != null && !expectedRole.isEmpty()
                    && !expectedRole.equals(user.getRole())) {
                throw new ServiceException("登录身份与账号不匹配，请切换为"
                        + (ROLE_TEACHER.equals(user.getRole()) ? "教师" : "学生") + "入口");
            }
            return user;
        } catch (SQLException ex) {
            throw new ServiceException("登录服务暂时不可用，请稍后重试", ex);
        }
    }

    public String homePath(User user) {
        return user != null && user.isTeacher() ? "/teacher/dashboard" : "/student/home";
    }
}
