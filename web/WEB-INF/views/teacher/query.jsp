<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="query"/>
<c:set var="pageTitle" value="成绩查询"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<section class="query-hero reveal">
    <span class="story-kicker"><span class="live-dot"></span> SCORE SEARCH · 全班级检索</span>
    <h2>找到每一位学生的成绩轨迹</h2>
    <p>按姓名或学号快速检索，支持班级筛选与成绩排名。</p>
    <form class="query-form" method="get" action="${ctx}/teacher/query">
        <input type="hidden" name="class" value="<c:out value="${selectedClass}"/>">
        <div class="query-input-wrap">
            <svg class="icon"><use href="#i-search"/></svg>
            <input class="query-input" type="search" name="keyword" value="<c:out value="${keyword}"/>"
                   placeholder="输入姓名或学号，例如：张梓涵 / 20240101" autocomplete="off">
        </div>
        <button class="btn btn-primary" type="submit">立即查询</button>
    </form>
    <div class="class-chips">
        <c:url var="allUrl" value="/teacher/query">
            <c:param name="keyword" value="${keyword}"/>
        </c:url>
        <a class="chip ${selectedClass eq 'all' ? 'is-active' : ''}" href="${allUrl}">全部班级</a>
        <c:forEach var="item" items="${classes}">
            <c:url var="classUrl" value="/teacher/query">
                <c:param name="keyword" value="${keyword}"/>
                <c:param name="class" value="${item}"/>
            </c:url>
            <a class="chip ${item eq selectedClass ? 'is-active' : ''}" href="${classUrl}"><c:out value="${item}"/></a>
        </c:forEach>
    </div>
</section>

<div class="query-results reveal" data-delay="60">
    <div>
        <h2 style="margin:0;font-size:18px">
            <c:choose>
                <c:when test="${not empty keyword}">“<c:out value="${keyword}"/>” 的查询结果</c:when>
                <c:otherwise>全部学生</c:otherwise>
            </c:choose>
        </h2>
        <p style="margin:4px 0 0;color:var(--muted);font-size:11.5px">共找到 ${resultCount} 名学生 · 点击右侧查看完整成绩单</p>
    </div>
    <div class="result-metrics">
        <div class="result-metric"><small>平均分</small><strong><fmt:formatNumber value="${resultAverage}" maxFractionDigits="1"/></strong></div>
        <div class="result-metric"><small>全部及格</small><strong>${resultPassCount}</strong></div>
    </div>
</div>

<div class="card reveal" data-delay="100">
    <c:choose>
        <c:when test="${empty summaries}">
            <div class="empty-state">
                <span class="empty-icon"><svg class="icon"><use href="#i-search"/></svg></span>
                <h3>没有找到匹配的学生</h3>
                <p>请检查姓名或学号是否正确，也可以切换到其他班级试试。</p>
                <a class="btn btn-soft btn-sm" href="${ctx}/teacher/query">清空查询条件</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table student-table">
                    <thead>
                    <tr>
                        <th style="width:70px">排名</th>
                        <th>学生</th>
                        <th>学号</th>
                        <th>班级</th>
                        <th>平均分</th>
                        <th>及格率</th>
                        <th>成绩数</th>
                        <th>状态</th>
                        <th style="width:90px">详情</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="student" items="${summaries}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${student.rank eq 1}"><span class="rank-index gold">1</span></c:when>
                                    <c:when test="${student.rank eq 2}"><span class="rank-index silver">2</span></c:when>
                                    <c:when test="${student.rank eq 3}"><span class="rank-index bronze">3</span></c:when>
                                    <c:when test="${student.rank gt 0}"><span class="rank-index">${student.rank}</span></c:when>
                                    <c:otherwise><span class="cell-muted">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="cell-user">
                                    <span class="avatar avatar-grad"><c:out value="${student.initial}"/></span>
                                    <div>
                                        <strong><c:out value="${student.name}"/></strong>
                                        <small><c:out value="${student.gender}"/></small>
                                    </div>
                                </div>
                            </td>
                            <td class="cell-muted"><c:out value="${student.studentNo}"/></td>
                            <td><span class="pill pill-muted"><c:out value="${student.className}"/></span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${student.scoreCount gt 0}">
                                        <strong style="font-family:var(--font-num);font-size:14px"><fmt:formatNumber value="${student.average}" maxFractionDigits="1"/></strong>
                                        <div class="progress" style="width:86px;margin-top:6px"><span class="progress-fill" style="--w:${student.averageWidth}%"></span></div>
                                    </c:when>
                                    <c:otherwise><span class="cell-muted">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${student.scoreCount gt 0}">
                                        <span class="score-chip ${student.passRate ge 100 ? 'grade-great' : (student.passRate ge 80 ? 'grade-good' : 'grade-fail')}"><fmt:formatNumber value="${student.passRate}" maxFractionDigits="0"/>%</span>
                                    </c:when>
                                    <c:otherwise><span class="cell-muted">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="cell-muted">${student.scoreCount} 条</td>
                            <td><span class="status-pill status-${student.statusClass}"><c:out value="${student.statusLabel}"/></span></td>
                            <td>
                                <a class="btn btn-soft btn-sm" href="${ctx}/teacher/student?id=${student.studentId}">
                                    查看 <svg class="icon" style="width:13px;height:13px"><use href="#i-arrow"/></svg>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
