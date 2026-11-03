<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="user" value="${sessionScope.authUser}"/>
<c:set var="statusCode" value="${empty errorCode ? requestScope['javax.servlet.error.status_code'] : errorCode}"/>
<c:set var="statusTitle" value="${empty errorTitle ? (statusCode eq 404 ? '页面走丢了' : '系统开了个小差') : errorTitle}"/>
<c:set var="statusMessage" value="${empty errorMessage ? '请稍后再试，或返回工作台继续操作。' : errorMessage}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${statusCode}"/> · 班级学生成绩管理系统</title>
    <link rel="stylesheet" href="${ctx}/assets/css/app.css">
</head>
<body class="error-body">
<div class="error-aurora" aria-hidden="true"></div>
<div class="error-wrap">
    <a class="brand" href="${ctx}/login">
        <span class="brand-mark"><span class="brand-core">G</span></span>
        <span class="brand-copy"><strong>GradeFlow</strong><small>班级成绩管理</small></span>
    </a>
    <div class="error-code"><c:out value="${empty statusCode ? '500' : statusCode}"/></div>
    <h1><c:out value="${statusTitle}"/></h1>
    <p><c:out value="${statusMessage}"/></p>
    <div class="error-actions">
        <c:choose>
            <c:when test="${not empty user}">
                <a class="btn btn-primary" href="${ctx}${user.teacher ? '/teacher/dashboard' : '/student/home'}">
                    返回工作台
                </a>
            </c:when>
            <c:otherwise>
                <a class="btn btn-primary" href="${ctx}/login">返回登录</a>
            </c:otherwise>
        </c:choose>
        <button class="btn btn-ghost" type="button" onclick="history.back()">回到上一页</button>
    </div>
    <div class="error-orbit" aria-hidden="true"><span></span><span></span><span></span></div>
</div>
</body>
</html>
