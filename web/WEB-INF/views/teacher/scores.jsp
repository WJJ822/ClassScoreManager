<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="scores"/>
<c:set var="pageTitle" value="成绩录入"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="entry-head reveal">
    <div class="entry-title">
        <span class="entry-icon"><svg class="icon"><use href="#i-edit"/></svg></span>
        <div>
            <h2><c:out value="${selectedClass}"/> · <c:out value="${selectedCourse.name}"/></h2>
            <p><c:out value="${selectedExam}"/> · 整班批量录入，保存后立即计入统计</p>
        </div>
    </div>
    <div class="live-stats">
        <div class="live-item"><small>已录入</small><strong><span data-live-count>0</span><em> 人</em></strong></div>
        <div class="live-item"><small>本次均分</small><strong><span data-live-avg>0.0</span></strong></div>
        <div class="live-item"><small>及格</small><strong><span data-live-pass>0</span><em> 人</em></strong></div>
        <div class="live-item"><small>待提升</small><strong><span data-live-fail>0</span><em> 人</em></strong></div>
    </div>
</div>

<div class="card toolbar-card reveal" data-delay="50">
    <form class="filter-form" method="get" action="${ctx}/teacher/scores">
        <div class="field-inline">
            <span>班级</span>
            <div class="select-wrap">
                <svg class="icon"><use href="#i-users"/></svg>
                <select class="form-select" name="class">
                    <c:forEach var="item" items="${classes}">
                        <option value="<c:out value="${item}"/>" ${item eq selectedClass ? 'selected' : ''}><c:out value="${item}"/></option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="field-inline">
            <span>课程</span>
            <div class="select-wrap">
                <svg class="icon"><use href="#i-book"/></svg>
                <select class="form-select" name="courseId">
                    <c:forEach var="course" items="${courses}">
                        <option value="${course.id}" ${course.id eq selectedCourseId ? 'selected' : ''}><c:out value="${course.name}"/></option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="field-inline">
            <span>考试</span>
            <div class="select-wrap">
                <svg class="icon"><use href="#i-calendar"/></svg>
                <select class="form-select" name="exam">
                    <c:forEach var="item" items="${exams}">
                        <option value="<c:out value="${item}"/>" ${item eq selectedExam ? 'selected' : ''}><c:out value="${item}"/></option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <button class="btn btn-soft" type="submit">
            <svg class="icon"><use href="#i-filter"/></svg> 切换录入表
        </button>
        <span class="legend" style="margin-left:auto">
            <span class="lg-fail"><i></i>不及格</span>
            <span class="lg-pass"><i></i>及格</span>
            <span class="lg-mid"><i></i>中等</span>
            <span class="lg-good"><i></i>良好</span>
            <span class="lg-great"><i></i>优秀</span>
        </span>
    </form>
</div>

<c:choose>
    <c:when test="${empty rows}">
        <div class="card reveal">
            <div class="empty-state">
                <span class="empty-icon"><svg class="icon"><use href="#i-users"/></svg></span>
                <h3>这个班级还没有学生</h3>
                <p>可以先去「学生管理」添加学生，再回来录入成绩。</p>
                <a class="btn btn-soft btn-sm" href="${ctx}/teacher/students">
                    <svg class="icon"><use href="#i-plus"/></svg> 添加学生
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <form method="post" action="${ctx}/teacher/scores" data-score-form class="card reveal" data-delay="80">
            <input type="hidden" name="class" value="<c:out value="${selectedClass}"/>">
            <input type="hidden" name="courseId" value="${selectedCourseId}">
            <input type="hidden" name="exam" value="<c:out value="${selectedExam}"/>">

            <div class="card-head" style="padding:20px 22px 12px">
                <div>
                    <h3>整班成绩表</h3>
                    <p>分数范围 0–100，可保留一位小数；留空并保存会清除该生本次成绩</p>
                </div>
                <span class="pill pill-info">${rows.size()} 名学生</span>
            </div>

            <div class="table-wrap">
                <table class="data-table score-table">
                    <thead>
                    <tr>
                        <th style="width:48px">#</th>
                        <th>学生</th>
                        <th>学号</th>
                        <th>班级</th>
                        <th>状态</th>
                        <th style="width:150px">本次分数</th>
                        <th style="width:96px">等级</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${rows}" varStatus="status">
                        <tr>
                            <td class="score-index">${status.index + 1}</td>
                            <td>
                                <div class="cell-user">
                                    <span class="avatar avatar-grad"><c:out value="${row.student.initial}"/></span>
                                    <div>
                                        <strong><c:out value="${row.student.name}"/></strong>
                                        <small><c:out value="${row.student.gender}"/></small>
                                    </div>
                                </div>
                            </td>
                            <td class="cell-muted"><c:out value="${row.student.studentNo}"/></td>
                            <td class="cell-muted"><c:out value="${row.student.className}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${row.recorded}"><span class="status-pill status-success">已录入</span></c:when>
                                    <c:otherwise><span class="status-pill status-muted">未录入</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <input class="score-input" type="text" inputmode="decimal"
                                       name="score_${row.student.id}" data-score-input
                                       data-initial="<c:out value="${row.scoreText}"/>"
                                       value="<c:out value="${row.scoreText}"/>"
                                       placeholder="0–100" autocomplete="off" aria-label="<c:out value="${row.student.name}"/>的分数">
                            </td>
                            <td class="score-status">
                                <span class="score-chip grade-${row.gradeClass}" data-grade-chip>${row.gradeClass eq 'none' ? '—' : (row.score ge 90 ? '优秀' : (row.score ge 80 ? '良好' : (row.score ge 70 ? '中等' : (row.score ge 60 ? '及格' : '不及格'))))}</span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="save-bar">
                <div class="save-hint">
                    <svg class="icon"><use href="#i-info"/></svg>
                    正在编辑：<strong style="color:var(--ink-2)"><c:out value="${selectedClass}"/> · <c:out value="${selectedCourse.name}"/> · <c:out value="${selectedExam}"/></strong>
                </div>
                <div class="save-actions">
                    <button class="btn btn-ghost" type="button" data-reset-scores>
                        <svg class="icon"><use href="#i-clock"/></svg> 恢复
                    </button>
                    <button class="btn btn-primary" type="submit" data-save-scores data-submit-btn>
                        <svg class="icon"><use href="#i-check"/></svg> 保存本页成绩
                    </button>
                </div>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
