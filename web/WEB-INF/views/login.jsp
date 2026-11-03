<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="selectedRole" value="${empty role ? 'teacher' : role}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#0d1024">
    <title>登录 · 班级学生成绩管理系统</title>
    <link rel="stylesheet" href="${ctx}/assets/css/app.css">
    <script>document.documentElement.classList.add('js');</script>
</head>
<body class="login-body" data-role="${selectedRole}">
<%@ include file="/WEB-INF/jspf/sprite.jspf" %>

<div class="auth-bg" aria-hidden="true">
    <span class="aurora aurora-1"></span>
    <span class="aurora aurora-2"></span>
    <span class="aurora aurora-3"></span>
    <span class="grid-plane"></span>
    <span class="particle p1"></span>
    <span class="particle p2"></span>
    <span class="particle p3"></span>
    <span class="particle p4"></span>
</div>

<div class="auth-shell">
    <section class="auth-story">
        <a class="brand brand-light" href="${ctx}/login">
            <span class="brand-mark"><span class="brand-core">G</span></span>
            <span class="brand-copy">
                <strong>GradeFlow</strong>
                <small>班级成绩管理</small>
            </span>
        </a>

        <div class="story-copy">
            <span class="story-kicker"><span class="live-dot"></span> CLASS SCORE SYSTEM · 2026</span>
            <h1>让每一次成长，<br><em>都有迹可循。</em></h1>
            <p>面向教师与学生的班级成绩工作台。录入、查询、分析，在同一处优雅发生。</p>
        </div>

        <div class="auth-visual" aria-hidden="true">
            <div class="orbit orbit-1"></div>
            <div class="orbit orbit-2"></div>
            <div class="orbit-core">
                <span>本学期</span>
                <strong data-count="91.4" data-decimals="1">0</strong>
                <small>平均及格率 %</small>
            </div>
            <div class="float-card float-a">
                <span class="float-icon blue"><svg class="icon"><use href="#i-chart"/></svg></span>
                <div><small>班级平均分</small><strong><span data-count="75.6" data-decimals="1">0</span><i>分</i></strong></div>
                <span class="float-trend"><svg class="icon"><use href="#i-trend"/></svg> 稳步提升</span>
            </div>
            <div class="float-card float-b">
                <span class="float-icon mint"><svg class="icon"><use href="#i-award"/></svg></span>
                <div><small>优秀率</small><strong><span data-count="10.2" data-decimals="1">0</span><i>%</i></strong></div>
            </div>
            <div class="float-pill"><span class="avatar avatar-xs avatar-grad">王</span> 王老师刚刚更新了成绩</div>
        </div>

        <div class="auth-quote">
            <span class="quote-mark">“</span>
            <p>教育不是灌满一桶水，而是点燃一把火。</p>
            <span class="quote-author">— W. B. Yeats</span>
        </div>
    </section>

    <section class="auth-panel">
        <div class="login-card">
            <div class="login-head">
                <span class="login-badge"><svg class="icon"><use href="#i-spark"/></svg> WELCOME BACK</span>
                <h2>欢迎回来</h2>
                <p>选择你的身份，进入专属成绩空间</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error" role="alert">
                    <span class="alert-icon"><svg class="icon"><use href="#i-info"/></svg></span>
                    <span><c:out value="${errorMessage}"/></span>
                </div>
            </c:if>
            <c:if test="${not empty infoMessage}">
                <div class="alert alert-success" role="status">
                    <span class="alert-icon"><svg class="icon"><use href="#i-check"/></svg></span>
                    <span><c:out value="${infoMessage}"/></span>
                </div>
            </c:if>

            <form class="login-form" method="post" action="${ctx}/login" data-login-form>
                <div class="role-switch" role="tablist" aria-label="选择登录身份">
                    <button class="role-btn ${selectedRole eq 'teacher' ? 'is-active' : ''}" type="button"
                            data-role-btn="teacher" role="tab" aria-selected="${selectedRole eq 'teacher'}">
                        <svg class="icon"><use href="#i-graduation"/></svg> 教师入口
                    </button>
                    <button class="role-btn ${selectedRole eq 'student' ? 'is-active' : ''}" type="button"
                            data-role-btn="student" role="tab" aria-selected="${selectedRole eq 'student'}">
                        <svg class="icon"><use href="#i-user"/></svg> 学生入口
                    </button>
                    <span class="role-glider" aria-hidden="true"></span>
                </div>
                <input type="hidden" name="role" id="roleInput" value="${selectedRole}">

                <label class="field">
                    <span class="field-label">账号</span>
                    <span class="input-wrap">
                        <svg class="icon input-icon"><use href="#i-at"/></svg>
                        <input type="text" name="username" id="username" value="<c:out value="${username}"/>"
                               placeholder="教师账号 / 学生学号" autocomplete="username" required>
                    </span>
                </label>

                <label class="field">
                    <span class="field-label">密码</span>
                    <span class="input-wrap">
                        <svg class="icon input-icon"><use href="#i-lock"/></svg>
                        <input type="password" name="password" id="password" placeholder="请输入登录密码"
                               autocomplete="current-password" required>
                        <button class="password-toggle" type="button" data-password-toggle aria-label="显示密码">
                            <svg class="icon"><use href="#i-eye"/></svg>
                        </button>
                    </span>
                </label>

                <div class="login-row">
                    <label class="check"><input type="checkbox" id="rememberAccount"> <span>记住账号</span></label>
                    <span class="login-help">忘记密码？请联系任课教师</span>
                </div>

                <button class="btn btn-primary btn-block btn-login" type="submit" data-submit-btn>
                    <span class="btn-label">进入系统</span>
                    <span class="btn-spinner" aria-hidden="true"></span>
                    <svg class="icon btn-arrow"><use href="#i-arrow"/></svg>
                </button>
            </form>

            <div class="demo-box">
                <div class="demo-title"><span>演示账号</span><small>点击即可填充</small></div>
                <div class="demo-list">
                    <button class="demo-account" type="button" data-demo="teacher"
                            data-user="teacher" data-pass="123456">
                        <span class="demo-avatar teacher">师</span>
                        <span><strong>教师体验</strong><small>teacher / 123456</small></span>
                        <svg class="icon"><use href="#i-arrow"/></svg>
                    </button>
                    <button class="demo-account" type="button" data-demo="student"
                            data-user="20240101" data-pass="123456">
                        <span class="demo-avatar student">生</span>
                        <span><strong>学生体验</strong><small>20240101 / 123456</small></span>
                        <svg class="icon"><use href="#i-arrow"/></svg>
                    </button>
                </div>
            </div>

            <p class="login-foot">
                <svg class="icon"><use href="#i-info"/></svg>
                数据保存在本机，账号由教师统一维护
            </p>
        </div>
    </section>
</div>

<div class="toast-stack" id="toastStack" aria-live="polite"></div>
<script src="${ctx}/assets/js/app.js"></script>
</body>
</html>
