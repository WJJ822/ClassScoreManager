<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="query"/>
<c:set var="pageTitle" value="学生成绩详情"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<a class="back-link" href="${ctx}/teacher/query">
    <svg class="icon"><use href="#i-back"/></svg> 返回成绩查询
</a>

<section class="detail-hero reveal">
    <div class="profile-avatar"><c:out value="${student.initial}"/></div>
    <div class="profile-meta">
        <span class="eyebrow">STUDENT PROFILE</span>
        <h2><c:out value="${student.name}"/></h2>
        <div class="profile-tags">
            <span><svg class="icon"><use href="#i-users"/></svg> <c:out value="${student.className}"/></span>
            <span><svg class="icon"><use href="#i-at"/></svg> <c:out value="${student.studentNo}"/></span>
            <span><svg class="icon"><use href="#i-user"/></svg> <c:out value="${student.gender}"/></span>
        </div>
    </div>
    <div class="hero-metrics">
        <div class="hero-metric">
            <small>平均分</small>
            <strong><fmt:formatNumber value="${summary.average}" maxFractionDigits="1"/><em> 分</em></strong>
        </div>
        <div class="hero-metric">
            <small>及格率</small>
            <strong><fmt:formatNumber value="${summary.passRate}" maxFractionDigits="1"/><em> %</em></strong>
        </div>
        <div class="rank-ring" style="--rank-pct:${classSize gt 0 ? (classSize - summary.rank + 1) * 100.0 / classSize : 0}">
            <div class="rank-ring-inner">
                <strong>${summary.rank gt 0 ? summary.rank : '—'}</strong>
                <small>班级排名</small>
                <em>/ ${classSize} 人</em>
            </div>
        </div>
    </div>
</section>

<div class="detail-grid">
    <article class="card reveal">
        <div class="card-head" style="padding:22px 24px 14px">
            <div>
                <h3>成绩明细</h3>
                <p>期中与期末成绩、等级及班级均分对比</p>
            </div>
            <span class="pill pill-info">${summary.scoreCount} 条记录</span>
        </div>
        <c:choose>
            <c:when test="${empty scores}">
                <div class="empty-state">
                    <span class="empty-icon"><svg class="icon"><use href="#i-book"/></svg></span>
                    <h3>还没有成绩记录</h3>
                    <p>任课教师录入成绩后，这里会自动显示。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data-table score-detail-table">
                        <thead>
                        <tr>
                            <th>课程</th>
                            <th>考试</th>
                            <th>分数</th>
                            <th>等级</th>
                            <th>班级均分</th>
                            <th>差值</th>
                            <th>录入教师</th>
                            <th>更新时间</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="record" items="${scores}">
                            <tr>
                                <td>
                                    <div class="score-course">
                                        <span class="course-dot ${record.courseId % 4 eq 0 ? 'mint' : (record.courseId % 3 eq 0 ? 'blue' : (record.courseId % 2 eq 0 ? 'violet' : 'amber'))}"></span>
                                        <div>
                                            <strong style="font-size:12px"><c:out value="${record.courseName}"/></strong>
                                            <small style="display:block;color:var(--muted);font-size:9px"><c:out value="${record.courseCode}"/></small>
                                        </div>
                                    </div>
                                </td>
                                <td><span class="pill pill-muted"><c:out value="${record.examName}"/></span></td>
                                <td><span class="score-chip grade-${record.gradeClass}">${record.scoreText}</span></td>
                                <td class="cell-muted"><c:out value="${record.grade}"/></td>
                                <td class="cell-muted"><fmt:formatNumber value="${classAverages[record.courseId]}" maxFractionDigits="1"/></td>
                                <td>
                                    <c:set var="delta" value="${record.score - classAverages[record.courseId]}"/>
                                    <span class="compare ${delta ge 0 ? 'up' : 'down'}">
                                        <c:choose>
                                            <c:when test="${delta ge 0}">+</c:when>
                                            <c:otherwise>−</c:otherwise>
                                        </c:choose>
                                        <fmt:formatNumber value="${delta ge 0 ? delta : -delta}" maxFractionDigits="1"/>
                                    </span>
                                </td>
                                <td class="cell-muted"><c:out value="${record.teacherName}"/></td>
                                <td class="cell-muted">${record.updatedText}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </article>

    <aside style="display:flex;flex-direction:column;gap:18px">
        <article class="card panel reveal" data-delay="60">
            <div class="card-head" style="padding:0;margin-bottom:16px">
                <div>
                    <h3>学业画像</h3>
                    <p>与班级整体水平对比</p>
                </div>
            </div>
            <div class="class-list">
                <div class="class-row">
                    <span class="class-avatar" style="color:var(--primary);background:var(--primary-soft)">均</span>
                    <div class="class-info">
                        <strong>个人平均分</strong>
                        <small>全部课程与考试</small>
                    </div>
                    <div class="class-metrics"><strong><fmt:formatNumber value="${summary.average}" maxFractionDigits="1"/></strong><small>分</small></div>
                </div>
                <div class="class-row">
                    <span class="class-avatar" style="color:#0b8a70;background:var(--mint-soft)">班</span>
                    <div class="class-info">
                        <strong>班级平均分</strong>
                        <small><c:out value="${student.className}"/></small>
                    </div>
                    <div class="class-metrics"><strong><fmt:formatNumber value="${classAverage}" maxFractionDigits="1"/></strong><small>分</small></div>
                </div>
                <div class="class-row">
                    <span class="class-avatar" style="color:#b07516;background:var(--amber-soft)">率</span>
                    <div class="class-info">
                        <strong>个人及格率</strong>
                        <small>${summary.scoreCount} 条成绩记录</small>
                    </div>
                    <div class="class-metrics"><strong><fmt:formatNumber value="${summary.passRate}" maxFractionDigits="1"/>%</strong><small>及格</small></div>
                </div>
            </div>
            <div class="insight-banner" style="margin-top:16px">
                <span class="insight-icon"><svg class="icon"><use href="#i-spark"/></svg></span>
                <div>
                    <h4>学习建议</h4>
                    <p>
                        <c:choose>
                            <c:when test="${summary.average ge classAverage}">当前平均分高于班级水平，建议保持节奏并尝试拓展提升。</c:when>
                            <c:otherwise>当前平均分略低于班级水平，建议针对薄弱课程安排专项复习。</c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>
        </article>

        <article class="card panel reveal" data-delay="110">
            <div class="card-head" style="padding:0;margin-bottom:14px">
                <div>
                    <h3>成绩状态</h3>
                    <p>最近更新与整体表现</p>
                </div>
            </div>
            <div style="display:flex;flex-direction:column;gap:13px">
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span style="font-size:11px;color:var(--muted)">整体状态</span>
                    <span class="status-pill status-${summary.statusClass}"><c:out value="${summary.statusLabel}"/></span>
                </div>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span style="font-size:11px;color:var(--muted)">优秀率</span>
                    <strong style="font-family:var(--font-num);font-size:13px"><fmt:formatNumber value="${summary.excellentRate}" maxFractionDigits="1"/>%</strong>
                </div>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span style="font-size:11px;color:var(--muted)">班级排名</span>
                    <strong style="font-family:var(--font-num);font-size:13px">${summary.rank gt 0 ? summary.rank : '—'} / ${classSize}</strong>
                </div>
                <div class="progress"><span class="progress-fill mint" style="--w:${summary.passRate}%"></span></div>
            </div>
        </article>
    </aside>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
