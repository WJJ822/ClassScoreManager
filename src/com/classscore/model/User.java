package com.classscore.model;

import java.io.Serializable;

/**
 * 登录用户。refId 指向教师或学生主键。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String role;
    private int refId;
    private String displayName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isTeacher() {
        return "teacher".equals(role);
    }

    public boolean isStudent() {
        return "student".equals(role);
    }

    public String getInitial() {
        return displayName == null || displayName.isEmpty()
                ? "?" : displayName.substring(0, 1);
    }

    public String getRoleLabel() {
        return isTeacher() ? "教师" : "学生";
    }
}
