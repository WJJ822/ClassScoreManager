<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="stats"/>
<c:set var="pageTitle" value="统计分析"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="entry-head reveal">
    <div class="entry-title">
        <span class="entry-icon"><svg class="icon"><use href="#i-chart"/></svg></span>
        <div>
            <h2>班级成绩分析</h2>
            <p>平均分、及格率、优秀率与分数段分布，实时计算</p>
        </div>
    </div>
    <div class="filter-chips">
        <c:url var="allUrl" value="/teacher/stats"/>
        <a class="filter-chip ${selectedClass eq 'all' ? 'is-active' : ''}" href="${allUrl}">全部班级</a>
        <c:forEach var="item" items="${classes}">
            <c:url var="classUrl" value="/teacher/stats">
                <c:param name="class" value="${item}"/>
            </c:url>
            <a class="filter-chip ${item eq selectedClass ? 'is-active' : ''}" href="${classUrl}"><c:out value="${item}"/></a>
        </c:forEach>
    </div>
</div>

<div class="stat-grid">
    <article class="stat-card tone-violet reveal" data-delay="30">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-users"/></svg></span></div>
        <span class="stat-label">统计学生</span>
        <div class="stat-value"><span data-count="${overview.studentCount}">0</span><small>人</small></div>
        <div class="stat-meta">${overview.classCount} 个班级 · ${overview.courseCount} 门课程</div>
    </article>
    <article class="stat-card tone-blue reveal" data-delay="70">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-chart"/></svg></span></div>
        <span class="stat-label">平均分</span>
        <div class="stat-value"><span data-count="${overview.average}" data-decimals="1">0</span><small>分</small></div>
        <div class="stat-meta">基于 ${overview.scoreCount} 条成绩</div>
    </article>
    <article class="stat-card tone-mint reveal" data-delay="110">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-check"/></svg></span></div>
        <span class="stat-label">及格率</span>
        <div class="stat-value"><span data-count="${overview.passRate}" data-decimals="1">0</span><small>%</small></div>
        <div class="stat-meta">${passCount} 人次及格 · ${overview.failCount} 人次未及格</div>
    </article>
    <article class="stat-card tone-amber reveal" data-delay="150">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-award"/></svg></span></div>
        <span class="stat-label">优秀率</span>
        <div class="stat-value"><span data-count="${overview.excellentRate}" data-decimals="1">0</span><small>%</small></div>
        <div class="stat-meta">90 分及以上人次占比</div>
    </article>
</div>

<div class="content-grid">
    <article class="card donut-card reveal">
        <div class="donut" style="--pass:${overview.passRate}">
            <div class="donut-center">
                <strong><span data-count="${overview.passRate}" data-decimals="1">0</span><em>%</em></strong>
                <small>整体及格率</small>
            </div>
        </div>
        <div class="donut-legend">
            <div class="donut-legend-item"><i style="background:#6b6bff"></i><span>及格人次</span><strong>${passCount}</strong></div>
            <div class="donut-legend-item"><i style="background:#ffd4dc"></i><span>待提升人次</span><strong>${overview.failCount}</strong></div>
            <div class="donut-legend-item"><i style="background:#23c9aa"></i><span>优秀人次</span><strong><fmt:formatNumber value="${overview.scoreCount * overview.excellentRate / 100}" maxFractionDigits="0"/></strong></div>
            <div class="insight-banner" style="margin-top:6px">
                <span class="insight-icon"><svg class="icon"><use href="#i-target"/></svg></span>
                <div>
                    <h4>目标参考</h4>
                    <p>及格率高于 90% 属于优秀水平，当前已${overview.passRate ge 90 ? '达到' : '接近'}该目标。</p>
                </div>
            </div>
        </div>
    </article>

    <article class="card panel reveal" data-delay="70">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>分数段分布</h3>
                <p>各分数段人次与占比</p>
            </div>
            <span class="pill pill-info">${overview.scoreCount} 条</span>
        </div>
        <div class="dist-bars" style="height:190px">
            <c:forEach var="item" items="${distribution}" varStatus="status">
                <div class="dist-bar dist-${item.tone}" style="--h:${item.barHeight}%">
                    <div class="bar-track">
                        <div class="bar-fill" style="--h:${item.barHeight}%;--d:${status.index * 0.08}s"></div>
                        <span class="bar-count">${item.count}</span>
                    </div>
                    <span class="bar-label"><c:out value="${item.label}"/><strong><fmt:formatNumber value="${item.percent}" maxFractionDigits="1"/>%</strong></span>
                </div>
            </c:forEach>
        </div>
    </article>
</div>

<div class="content-grid">
    <article class="card panel reveal">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>课程表现</h3>
                <p>各课程平均分、及格率与极值</p>
            </div>
        </div>
        <div class="course-stat-list">
            <c:forEach var="course" items="${courseStats}">
                <div class="course-stat-row">
                    <div class="course-stat-top">
                        <div class="course-stat-name">
                            <span class="course-dot ${course.accent}"></span>
                            <span><c:out value="${course.name}"/></span>
                            <span style="font-weight:500"><c:out value="${course.code}"/></span>
                        </div>
                        <div class="course-stat-values">
                            <div><small>均分</small><strong><fmt:formatNumber value="${course.average}" maxFractionDigits="1"/></strong></div>
                            <div><small>及格率</small><strong><fmt:formatNumber value="${course.passRate}" maxFractionDigits="1"/>%</strong></div>
                            <div><small>最高 / 最低</small><strong><fmt:formatNumber value="${course.maxScore}" maxFractionDigits="0"/> / <fmt:formatNumber value="${course.minScore}" maxFractionDigits="0"/></strong></div>
                        </div>
                    </div>
                    <div class="progress"><span class="progress-fill" style="--w:${course.passRate}%"></span></div>
                </div>
            </c:forEach>
        </div>
    </article>

    <article class="card panel reveal" data-delay="70">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>班级对比</h3>
                <p>平均分与及格率横向比较</p>
            </div>
        </div>
        <div class="class-list">
            <c:forEach var="stat" items="${classStats}" varStatus="status">
                <div class="class-row">
                    <span class="class-avatar">${status.index + 1}</span>
                    <div class="class-info">
                        <strong><c:out value="${stat.className}"/></strong>
                        <small>${stat.studentCount} 名学生 · ${stat.scoreCount} 条成绩</small>
                        <div class="mini-progress" style="width:100%;margin-top:8px"><span style="--w:${stat.averageWidth}%"></span></div>
                    </div>
                    <div class="class-metrics">
                        <strong><fmt:formatNumber value="${stat.average}" maxFractionDigits="1"/></strong>
                        <small>均分 · 及格 <fmt:formatNumber value="${stat.passRate}" maxFractionDigits="0"/>%</small>
                    </div>
                </div>
            </c:forEach>
        </div>
    </article>
</div>

<div class="content-grid">
    <article class="card panel reveal">
        <div class="card-head" style="padding:0;margin-bottom:4px">
            <div>
                <h3>成绩排行榜</h3>
                <p>按全部成绩平均分排序 · 前 8 名</p>
            </div>
            <span class="pill pill-success">Top 8</span>
        </div>
        <div class="rank-list">
            <c:choose>
                <c:when test="${empty topStudents}">
                    <div class="empty-state" style="padding:30px 10px"><p>暂无排行数据</p></div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="rank" items="${topStudents}">
                        <div class="rank-row">
                            <span class="rank-index ${rank.rankClass}">${rank.rank}</span>
                            <span class="avatar avatar-grad" style="width:32px;height:32px;border-radius:9px;font-size:10px"><c:out value="${rank.initial}"/></span>
                            <div class="rank-user">
                                <strong><c:out value="${rank.name}"/></strong>
                                <small><c:out value="${rank.className}"/> · ${rank.scoreCount} 条成绩</small>
                            </div>
                            <div class="rank-bar"><span style="--w:${rank.averageWidth}%"></span></div>
                            <span class="rank-score"><fmt:formatNumber value="${rank.average}" maxFractionDigits="1"/></span>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </article>

    <article class="card advice-card reveal" data-delay="70">
        <span class="advice-badge"><svg class="icon" style="width:12px;height:12px"><use href="#i-spark"/></svg> TEACHING INSIGHT</span>
        <h3>
            <c:choose>
                <c:when test="${overview.passRate ge 95}">整体表现非常稳健</c:when>
                <c:when test="${overview.passRate ge 90}">整体表现良好，仍有提升空间</c:when>
                <c:when test="${overview.passRate ge 80}">重点关注临界学生</c:when>
                <c:otherwise>建议开展分层辅导</c:otherwise>
            </c:choose>
        </h3>
        <p>
            当前及格率为 <fmt:formatNumber value="${overview.passRate}" maxFractionDigits="1"/>%，
            平均分为 <fmt:formatNumber value="${overview.average}" maxFractionDigits="1"/> 分。
            建议结合分数段分布与课程差异，安排下一阶段的教学重点。
        </p>
        <div class="advice-list">
            <span><svg class="icon"><use href="#i-check"/></svg> 关注 ${overview.failCount} 人次不及格记录</span>
            <span><svg class="icon"><use href="#i-check"/></svg> 对 60–69 分段学生进行专项巩固</span>
            <span><svg class="icon"><use href="#i-check"/></svg> 保持优秀学生的拓展练习节奏</span>
        </div>
    </article>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
