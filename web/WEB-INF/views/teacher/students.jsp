<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activeNav" value="students"/>
<c:set var="pageTitle" value="学生管理"/>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="section-head reveal">
    <div>
        <h2>班级名册</h2>
        <p>维护学生信息与登录账号，新增学生默认密码为 123456</p>
    </div>
    <button class="btn btn-primary" type="button" data-open-dialog="#addStudentDialog">
        <svg class="icon"><use href="#i-plus"/></svg> 添加学生
    </button>
</div>

<div class="card toolbar-card reveal" data-delay="40">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/teacher/students">
            <div class="search-field">
                <svg class="icon"><use href="#i-search"/></svg>
                <input type="search" name="keyword" value="<c:out value="${keyword}"/>" placeholder="搜索姓名或学号">
            </div>
            <div class="select-wrap">
                <svg class="icon"><use href="#i-users"/></svg>
                <select class="form-select" name="class">
                    <option value="all">全部班级</option>
                    <c:forEach var="item" items="${classes}">
                        <option value="<c:out value="${item}"/>" ${item eq selectedClass ? 'selected' : ''}><c:out value="${item}"/></option>
                    </c:forEach>
                </select>
            </div>
            <button class="btn btn-soft" type="submit"><svg class="icon"><use href="#i-search"/></svg> 查询</button>
            <c:if test="${not empty keyword or selectedClass ne 'all'}">
                <a class="btn btn-ghost" href="${ctx}/teacher/students">重置</a>
            </c:if>
        </form>
        <div class="toolbar-right">
            <span class="pill pill-info">共 ${students.size()} 名学生</span>
        </div>
    </div>
</div>

<div class="card reveal" data-delay="80">
    <c:choose>
        <c:when test="${empty students}">
            <div class="empty-state">
                <span class="empty-icon"><svg class="icon"><use href="#i-users"/></svg></span>
                <h3>没有找到匹配的学生</h3>
                <p>试试更换关键词或班级，也可以直接添一名新学生。</p>
                <button class="btn btn-primary btn-sm" type="button" data-open-dialog="#addStudentDialog">
                    <svg class="icon"><use href="#i-plus"/></svg> 添加学生
                </button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table student-table">
                    <thead>
                    <tr>
                        <th>学生</th>
                        <th>学号</th>
                        <th>班级</th>
                        <th>性别</th>
                        <th>平均分</th>
                        <th>及格率</th>
                        <th>状态</th>
                        <th style="width:110px">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="student" items="${students}">
                        <tr>
                            <td>
                                <div class="cell-user">
                                    <span class="avatar avatar-grad"><c:out value="${student.initial}"/></span>
                                    <div>
                                        <strong><c:out value="${student.name}"/></strong>
                                        <small><c:choose><c:when test="${student.rank gt 0}">班级第 ${student.rank} 名</c:when><c:otherwise>暂无排名</c:otherwise></c:choose></small>
                                    </div>
                                </div>
                            </td>
                            <td class="cell-muted"><c:out value="${student.studentNo}"/></td>
                            <td><span class="pill pill-muted"><c:out value="${student.className}"/></span></td>
                            <td class="cell-muted"><c:out value="${student.gender}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${student.scoreCount gt 0}">
                                        <strong style="font-family:var(--font-num);font-size:14px"><fmt:formatNumber value="${student.average}" maxFractionDigits="1"/></strong>
                                        <div class="progress" style="width:92px;margin-top:6px"><span class="progress-fill" style="--w:${student.averageWidth}%"></span></div>
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
                            <td><span class="status-pill status-${student.statusClass}"><c:out value="${student.statusLabel}"/></span></td>
                            <td>
                                <div class="row-actions">
                                    <a class="icon-action" href="${ctx}/teacher/student?id=${student.studentId}" title="查看成绩">
                                        <svg class="icon"><use href="#i-eye"/></svg>
                                    </a>
                                    <form method="post" action="${ctx}/teacher/students" data-confirm-delete
                                          data-student-name="<c:out value="${student.name}"/>">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${student.studentId}">
                                        <input type="hidden" name="keyword" value="<c:out value="${keyword}"/>">
                                        <input type="hidden" name="class" value="<c:out value="${selectedClass}"/>">
                                        <button class="icon-action danger" type="submit" title="删除学生">
                                            <svg class="icon"><use href="#i-trash"/></svg>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<dialog class="modal" id="addStudentDialog" aria-labelledby="addStudentTitle">
    <form method="post" action="${ctx}/teacher/students">
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="keyword" value="<c:out value="${keyword}"/>">
        <input type="hidden" name="class" value="<c:out value="${selectedClass}"/>">
        <div class="modal-head">
            <div>
                <h3 id="addStudentTitle">添加学生</h3>
                <p>填写基本信息，系统会自动创建学生登录账号</p>
            </div>
            <button class="modal-close" type="button" data-close-dialog aria-label="关闭">
                <svg class="icon"><use href="#i-close"/></svg>
            </button>
        </div>
        <div class="modal-body">
            <div class="form-grid">
                <div class="form-field">
                    <label for="studentNo">学号 <em>*</em></label>
                    <input class="form-input" id="studentNo" name="studentNo" type="text" required
                           pattern="[A-Za-z0-9]{4,30}" placeholder="例如 20240109" autocomplete="off">
                </div>
                <div class="form-field">
                    <label for="studentName">姓名 <em>*</em></label>
                    <input class="form-input" id="studentName" name="name" type="text" required maxlength="20"
                           placeholder="例如 林知夏" autocomplete="off">
                </div>
                <div class="form-field">
                    <label for="studentClass">班级 <em>*</em></label>
                    <input class="form-input" id="studentClass" name="className" type="text" required maxlength="40"
                           list="classOptions" placeholder="例如 软件工程1班" autocomplete="off">
                    <datalist id="classOptions">
                        <c:forEach var="item" items="${classes}">
                            <option value="<c:out value="${item}"/>"></option>
                        </c:forEach>
                    </datalist>
                </div>
                <div class="form-field">
                    <label for="studentGender">性别</label>
                    <select class="form-select" id="studentGender" name="gender" style="padding-left:13px">
                        <option value="未填写">未填写</option>
                        <option value="女">女</option>
                        <option value="男">男</option>
                    </select>
                </div>
                <div class="form-hint full">初始登录密码为 <strong>123456</strong>，学生可用学号登录后查看成绩。</div>
            </div>
        </div>
        <div class="modal-actions">
            <button class="btn btn-ghost" type="button" data-close-dialog>取消</button>
            <button class="btn btn-primary" type="submit">
                <svg class="icon"><use href="#i-plus"/></svg> 确认添加
            </button>
        </div>
    </form>
</dialog>

<dialog class="modal dialog-danger" id="confirmDialog" aria-labelledby="confirmTitle">
    <div class="modal-head">
        <div class="dialog-icon"><svg class="icon"><use href="#i-trash"/></svg></div>
        <button class="modal-close" type="button" data-close-dialog aria-label="关闭">
            <svg class="icon"><use href="#i-close"/></svg>
        </button>
    </div>
    <div class="modal-body" style="padding-top:0">
        <h3 id="confirmTitle">删除这名学生？</h3>
        <p>将同时删除 <strong data-confirm-name style="color:var(--rose)">该学生</strong> 的全部成绩与登录账号，此操作不可恢复。</p>
    </div>
    <div class="modal-actions">
        <button class="btn btn-ghost" type="button" data-close-dialog>再想想</button>
        <button class="btn btn-danger" type="button" data-confirm-ok>确认删除</button>
    </div>
</dialog>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
