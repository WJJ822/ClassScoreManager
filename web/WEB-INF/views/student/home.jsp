<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="student-home"/>
<c:set var="pageTitle" value="我的成绩"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<section class="student-hero reveal">
    <div class="profile-avatar"><c:out value="${student.initial}"/></div>
    <div class="profile-meta">
        <span class="eyebrow">MY SCORE SPACE · <c:out value="${todayLabel}"/></span>
        <h2><c:out value="${greeting}"/>，<c:out value="${student.name}"/></h2>
        <div class="profile-tags">
            <span><svg class="icon"><use href="#i-users"/></svg> <c:out value="${student.className}"/></span>
            <span><svg class="icon"><use href="#i-at"/></svg> <c:out value="${student.studentNo}"/></span>
            <span><svg class="icon"><use href="#i-check"/></svg> <c:out value="${summary.statusLabel}"/></span>
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

<div class="stat-grid">
    <article class="stat-card tone-violet reveal" data-delay="30">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-chart"/></svg></span></div>
        <span class="stat-label">我的平均分</span>
        <div class="stat-value"><span data-count="${summary.average}" data-decimals="1">0</span><small>分</small></div>
        <div class="stat-meta">共 ${summary.scoreCount} 条成绩记录</div>
    </article>
    <article class="stat-card tone-mint reveal" data-delay="70">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-check"/></svg></span></div>
        <span class="stat-label">及格率</span>
        <div class="stat-value"><span data-count="${summary.passRate}" data-decimals="1">0</span><small>%</small></div>
        <div class="stat-meta"><c:out value="${summary.statusLabel}"/></div>
    </article>
    <article class="stat-card tone-blue reveal" data-delay="110">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-users"/></svg></span></div>
        <span class="stat-label">班级平均分</span>
        <div class="stat-value"><span data-count="${classAverage}" data-decimals="1">0</span><small>分</small></div>
        <div class="stat-meta">
            <c:choose>
                <c:when test="${summary.average ge classAverage}"><span class="up"><svg class="icon"><use href="#i-trend"/></svg> 高于班级均分</span></c:when>
                <c:otherwise><span style="color:var(--rose)">低于班级均分</span></c:otherwise>
            </c:choose>
        </div>
    </article>
    <article class="stat-card tone-amber reveal" data-delay="150">
        <div class="stat-top"><span class="stat-icon"><svg class="icon"><use href="#i-award"/></svg></span></div>
        <span class="stat-label">班级排名</span>
        <div class="stat-value"><span data-count="${summary.rank}">0</span><small>/ ${classSize}</small></div>
        <div class="stat-meta">优秀率 <fmt:formatNumber value="${summary.excellentRate}" maxFractionDigits="1"/>%</div>
    </article>
</div>

<div class="section-head" id="score-cards">
    <div>
        <h2>我的成绩单</h2>
        <p>每门课程的期中、期末成绩与班级均分对比</p>
    </div>
    <span class="pill pill-info">${summary.scoreCount} 条记录</span>
</div>

<c:choose>
    <c:when test="${empty scores}">
        <div class="card reveal">
            <div class="empty-state">
                <span class="empty-icon"><svg class="icon"><use href="#i-book"/></svg></span>
                <h3>成绩还在路上</h3>
                <p>任课教师录入后，你的成绩单会自动出现在这里。</p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="score-card-grid">
            <c:forEach var="record" items="${scores}" varStatus="status">
                <c:set var="delta" value="${record.score - classAverages[record.courseId]}"/>
                <article class="personal-score-card ${status.index % 4 eq 0 ? 'tone-violet' : (status.index % 4 eq 1 ? 'tone-blue' : (status.index % 4 eq 2 ? 'tone-mint' : 'tone-amber'))} reveal"
                         data-delay="${status.index * 40}">
                    <div class="personal-score-head">
                        <div>
                            <h4><c:out value="${record.courseName}"/></h4>
                            <small><c:out value="${record.courseCode}"/> · <c:out value="${record.examName}"/></small>
                        </div>
                        <span class="score-chip grade-${record.gradeClass}">${record.grade}</span>
                    </div>
                    <div class="score-big">${record.scoreText}<small>分</small></div>
                    <div class="progress"><span class="progress-fill ${status.index % 4 eq 2 ? 'mint' : (status.index % 4 eq 3 ? 'amber' : '')}" style="--w:${record.scoreWidth}%"></span></div>
                    <div class="score-compare">
                        <span>班级均分 <strong><fmt:formatNumber value="${classAverages[record.courseId]}" maxFractionDigits="1"/></strong></span>
                        <span class="compare ${delta ge 0 ? 'up' : 'down'}">
                            <c:choose><c:when test="${delta ge 0}">+</c:when><c:otherwise>−</c:otherwise></c:choose>
                            <fmt:formatNumber value="${delta ge 0 ? delta : -delta}" maxFractionDigits="1"/>
                        </span>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<div class="section-head" id="analysis" style="margin-top:28px">
    <div>
        <h2>学习分析</h2>
        <p>找到自己的节奏，也看见下一步的方向</p>
    </div>
</div>

<div class="analysis-grid">
    <article class="card panel reveal">
        <div class="card-head" style="padding:0;margin-bottom:18px">
            <div>
                <h3>个人 vs 班级</h3>
                <p>平均分与及格率横向对比</p>
            </div>
        </div>
        <div style="display:flex;flex-direction:column;gap:18px">
            <div>
                <div style="display:flex;justify-content:space-between;font-size:11px;margin-bottom:7px">
                    <span>我的平均分</span><strong style="font-family:var(--font-num)"><fmt:formatNumber value="${summary.average}" maxFractionDigits="1"/> 分</strong>
                </div>
                <div class="progress"><span class="progress-fill" style="--w:${summary.averageWidth}%"></span></div>
            </div>
            <div>
                <div style="display:flex;justify-content:space-between;font-size:11px;margin-bottom:7px">
                    <span>班级平均分</span><strong style="font-family:var(--font-num)"><fmt:formatNumber value="${classAverage}" maxFractionDigits="1"/> 分</strong>
                </div>
                <div class="progress"><span class="progress-fill mint" style="--w:${classAverage}%"></span></div>
            </div>
            <div style="display:flex;justify-content:space-between;align-items:center;padding-top:14px;border-top:1px solid var(--line)">
                <div>
                    <div style="font-size:11px;font-weight:600">及格率</div>
                    <div style="font-size:9.5px;color:var(--muted)">${summary.scoreCount} 条成绩中及格的比例</div>
                </div>
                <span class="score-chip grade-${summary.passRate ge 100 ? 'great' : (summary.passRate ge 80 ? 'good' : 'fail')}"><fmt:formatNumber value="${summary.passRate}" maxFractionDigits="1"/>%</span>
            </div>
        </div>
    </article>

    <article class="card advice-card reveal" data-delay="70">
        <span class="advice-badge"><svg class="icon" style="width:12px;height:12px"><use href="#i-spark"/></svg> FOR YOU</span>
        <h3>
            <c:choose>
                <c:when test="${summary.average ge 90}">保持优秀，继续向前</c:when>
                <c:when test="${summary.average ge classAverage}">你正在稳步向上</c:when>
                <c:when test="${summary.average ge 60}">找到薄弱点，逐步突破</c:when>
                <c:otherwise>别急，从一门课开始</c:otherwise>
            </c:choose>
        </h3>
        <p>
            你当前的平均分为 <fmt:formatNumber value="${summary.average}" maxFractionDigits="1"/> 分，
            班级平均分为 <fmt:formatNumber value="${classAverage}" maxFractionDigits="1"/> 分。
            每一次认真订正，都会成为下一次进步的伏笔。
        </p>
        <div class="advice-list">
            <span><svg class="icon"><use href="#i-check"/></svg> 关注低于 60 分的科目，优先补齐基础</span>
            <span><svg class="icon"><use href="#i-check"/></svg> 对照班级均分，找到自己的优势课程</span>
            <span><svg class="icon"><use href="#i-check"/></svg> 期中之后及时复盘，期末更有方向</span>
        </div>
    </article>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
