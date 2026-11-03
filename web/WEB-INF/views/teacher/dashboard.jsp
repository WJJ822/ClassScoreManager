<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="dashboard"/>
<c:set var="pageTitle" value="数据总览"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<section class="welcome reveal">
    <div class="welcome-copy">
        <span class="welcome-kicker"><span class="live-dot"></span> <c:out value="${todayLabel}"/> · 数据已同步</span>
        <h2><c:out value="${greeting}"/>，<c:out value="${user.displayName}"/>老师 <span class="wave">👋</span></h2>
        <p>今天也一起把班级的每一分进步，变成看得见的成长轨迹。</p>
        <div class="welcome-actions">
            <a class="btn btn-primary" href="${ctx}/teacher/scores">
                <svg class="icon"><use href="#i-edit"/></svg> 录入本次成绩
            </a>
            <a class="btn btn-ghost" href="${ctx}/teacher/stats">
                <svg class="icon"><use href="#i-chart"/></svg> 查看统计分析
            </a>
        </div>
    </div>
    <div class="welcome-art" aria-hidden="true">
        <div class="orb-ring"></div>
        <div class="orb-center">
            <span>整体及格率</span>
            <strong data-count="${overview.passRate}" data-decimals="1">0</strong>
            <small>PASS RATE %</small>
        </div>
        <div class="welcome-chip chip-a"><svg class="icon"><use href="#i-users"/></svg> ${overview.studentCount} 名学生</div>
        <div class="welcome-chip chip-b"><svg class="icon"><use href="#i-trend"/></svg> ${overview.scoreCount} 条成绩</div>
    </div>
</section>

<div class="stat-grid">
    <article class="stat-card tone-violet reveal" data-delay="40">
        <div class="stat-top">
            <span class="stat-icon"><svg class="icon"><use href="#i-users"/></svg></span>
            <div class="spark-bars" aria-hidden="true">
                <span style="--h:42%;--d:.05s"></span><span style="--h:64%;--d:.1s"></span><span style="--h:50%;--d:.15s"></span><span style="--h:78%;--d:.2s"></span><span style="--h:62%;--d:.25s"></span><span style="--h:92%;--d:.3s"></span>
            </div>
        </div>
        <span class="stat-label">学生总数</span>
        <div class="stat-value"><span data-count="${overview.studentCount}">0</span><small>人</small></div>
        <div class="stat-meta">分布在 ${overview.classCount} 个班级 <span class="up"><svg class="icon"><use href="#i-trend"/></svg> 全员在册</span></div>
    </article>
    <article class="stat-card tone-blue reveal" data-delay="80">
        <div class="stat-top">
            <span class="stat-icon"><svg class="icon"><use href="#i-chart"/></svg></span>
            <div class="spark-bars" aria-hidden="true">
                <span style="--h:58%;--d:.1s"></span><span style="--h:48%;--d:.15s"></span><span style="--h:72%;--d:.2s"></span><span style="--h:66%;--d:.25s"></span><span style="--h:84%;--d:.3s"></span><span style="--h:76%;--d:.35s"></span>
            </div>
        </div>
        <span class="stat-label">整体平均分</span>
        <div class="stat-value"><span data-count="${overview.average}" data-decimals="1">0</span><small>分</small></div>
        <div class="stat-meta">共 ${overview.scoreCount} 条成绩记录 <span class="up"><svg class="icon"><use href="#i-trend"/></svg> 稳定发挥</span></div>
    </article>
    <article class="stat-card tone-mint reveal" data-delay="120">
        <div class="stat-top">
            <span class="stat-icon"><svg class="icon"><use href="#i-check"/></svg></span>
            <div class="spark-bars" aria-hidden="true">
                <span style="--h:80%;--d:.15s"></span><span style="--h:70%;--d:.2s"></span><span style="--h:88%;--d:.25s"></span><span style="--h:78%;--d:.3s"></span><span style="--h:94%;--d:.35s"></span><span style="--h:85%;--d:.4s"></span>
            </div>
        </div>
        <span class="stat-label">整体及格率</span>
        <div class="stat-value"><span data-count="${overview.passRate}" data-decimals="1">0</span><small>%</small></div>
        <div class="stat-meta">${overview.scoreCount - overview.failCount} 人次及格 · ${overview.failCount} 人次待提升</div>
    </article>
    <article class="stat-card tone-amber reveal" data-delay="160">
        <div class="stat-top">
            <span class="stat-icon"><svg class="icon"><use href="#i-award"/></svg></span>
            <div class="spark-bars" aria-hidden="true">
                <span style="--h:30%;--d:.2s"></span><span style="--h:44%;--d:.25s"></span><span style="--h:38%;--d:.3s"></span><span style="--h:58%;--d:.35s"></span><span style="--h:48%;--d:.4s"></span><span style="--h:70%;--d:.45s"></span>
            </div>
        </div>
        <span class="stat-label">优秀率</span>
        <div class="stat-value"><span data-count="${overview.excellentRate}" data-decimals="1">0</span><small>%</small></div>
        <div class="stat-meta">90 分以上人次占比 <span class="up"><svg class="icon"><use href="#i-trend"/></svg> 持续向上</span></div>
    </article>
</div>

<div class="section-head">
    <div>
        <h2>教学概览</h2>
        <p>成绩分布与班级表现，一屏掌握</p>
    </div>
    <a class="link-arrow" href="${ctx}/teacher/stats">完整统计 <svg class="icon"><use href="#i-arrow"/></svg></a>
</div>

<div class="content-grid">
    <article class="card panel reveal">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>成绩分布</h3>
                <p>全部成绩按分数段统计 · 共 ${overview.scoreCount} 条</p>
            </div>
            <span class="pill pill-info">平均 <fmt:formatNumber value="${overview.average}" maxFractionDigits="1"/> 分</span>
        </div>
        <div class="dist-bars">
            <c:forEach var="item" items="${distribution}" varStatus="status">
                <div class="dist-bar dist-${item.tone}" style="--h:${item.barHeight}%">
                    <div class="bar-track">
                        <div class="bar-fill" style="--h:${item.barHeight}%;--d:${status.index * 0.08}s"></div>
                        <span class="bar-count">${item.count}</span>
                    </div>
                    <span class="bar-label"><c:out value="${item.label}"/><strong><c:out value="${item.range}"/></strong></span>
                </div>
            </c:forEach>
        </div>
    </article>

    <article class="card panel reveal" data-delay="80">
        <div class="card-head" style="padding:0;margin-bottom:8px">
            <div>
                <h3>班级表现</h3>
                <p>平均分与及格率对比</p>
            </div>
            <a class="link-arrow" href="${ctx}/teacher/query">查看 <svg class="icon"><use href="#i-arrow"/></svg></a>
        </div>
        <div class="class-list">
            <c:forEach var="stat" items="${classStats}" varStatus="status">
                <div class="class-row">
                    <span class="class-avatar">${status.index + 1}</span>
                    <div class="class-info">
                        <strong><c:out value="${stat.className}"/></strong>
                        <small>${stat.studentCount} 名学生 · ${stat.scoreCount} 条成绩</small>
                    </div>
                    <div class="class-metrics">
                        <strong><fmt:formatNumber value="${stat.average}" maxFractionDigits="1"/></strong>
                        <small>平均分</small>
                        <div class="mini-progress"><span style="--w:${stat.passRate}%"></span></div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="insight-banner" style="margin-top:18px">
            <span class="insight-icon"><svg class="icon"><use href="#i-spark"/></svg></span>
            <div>
                <h4>教学提示</h4>
                <p>整体及格率 <fmt:formatNumber value="${overview.passRate}" maxFractionDigits="1"/>%，建议重点关注不及格的 ${overview.failCount} 人次，并在下次课前安排针对性辅导。</p>
            </div>
        </div>
    </article>
</div>

<div class="section-head">
    <div>
        <h2>课程表现</h2>
        <p>四门课程的均分与及格率</p>
    </div>
</div>
<div class="course-grid">
    <c:forEach var="course" items="${courseStats}" varStatus="status">
        <article class="course-card ${course.accent} reveal" data-delay="${status.index * 60}">
            <div class="course-top">
                <span class="course-code"><c:out value="${course.code}"/></span>
                <svg class="icon"><use href="#i-book"/></svg>
            </div>
            <h3><c:out value="${course.name}"/></h3>
            <div class="course-score"><fmt:formatNumber value="${course.average}" maxFractionDigits="1"/><small>平均分</small></div>
            <div class="course-meta">
                <span>及格率 <strong><fmt:formatNumber value="${course.passRate}" maxFractionDigits="1"/>%</strong></span>
                <span>最高 <strong><fmt:formatNumber value="${course.maxScore}" maxFractionDigits="0"/></strong></span>
            </div>
            <div class="progress course-progress"><span class="progress-fill" style="--w:${course.passRate}%"></span></div>
        </article>
    </c:forEach>
</div>

<div class="content-grid equal">
    <article class="card panel reveal">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>学习榜单</h3>
                <p>按全部成绩平均分排序 · Top 5</p>
            </div>
            <span class="pill pill-success">优秀</span>
        </div>
        <div class="rank-list">
            <c:choose>
                <c:when test="${empty topStudents}">
                    <div class="empty-state" style="padding:26px 10px">
                        <span class="empty-icon"><svg class="icon"><use href="#i-award"/></svg></span>
                        <p>还没有可排行的成绩数据</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="rank" items="${topStudents}">
                        <div class="rank-row">
                            <span class="rank-index ${rank.rankClass}">${rank.rank}</span>
                            <span class="avatar avatar-grad" style="width:32px;height:32px;border-radius:9px;font-size:10px"><c:out value="${rank.initial}"/></span>
                            <div class="rank-user">
                                <strong><c:out value="${rank.name}"/></strong>
                                <small><c:out value="${rank.className}"/></small>
                            </div>
                            <div class="rank-bar"><span style="--w:${rank.averageWidth}%"></span></div>
                            <span class="rank-score"><fmt:formatNumber value="${rank.average}" maxFractionDigits="1"/></span>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </article>

    <article class="card panel reveal" data-delay="70">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>最近录入</h3>
                <p>最新更新的成绩记录</p>
            </div>
            <a class="link-arrow" href="${ctx}/teacher/scores">去录入 <svg class="icon"><use href="#i-arrow"/></svg></a>
        </div>
        <div class="recent-list">
            <c:forEach var="record" items="${recentScores}">
                <div class="recent-row">
                    <span class="avatar avatar-grad"><c:out value="${record.initial}"/></span>
                    <div class="recent-main">
                        <strong><c:out value="${record.studentName}"/> · <c:out value="${record.courseName}"/></strong>
                        <small><c:out value="${record.examName}"/> · <c:out value="${record.className}"/></small>
                    </div>
                    <div class="recent-score">
                        <strong class="score-chip grade-${record.gradeClass}">${record.scoreText}</strong>
                        <small>${record.updatedText}</small>
                    </div>
                </div>
            </c:forEach>
        </div>
    </article>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
