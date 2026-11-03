/* ==========================================================================
   GradeFlow · 交互脚本
   无第三方依赖：导航、页面过渡、数字滚动、成绩实时统计、弹窗、Toast
   ========================================================================== */
(function () {
    'use strict';

    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const supportsCrossDocumentVT = 'startViewTransition' in document;
    const $ = (selector, root) => (root || document).querySelector(selector);
    const $$ = (selector, root) => Array.from((root || document).querySelectorAll(selector));

    /* ------------------------------ 日期显示 ------------------------------ */
    function renderToday() {
        const el = $('#todayPill');
        if (!el) return;
        const now = new Date();
        const text = new Intl.DateTimeFormat('zh-CN', {
            month: 'long', day: 'numeric', weekday: 'short'
        }).format(now);
        el.textContent = text;
    }

    /* ------------------------------ 数字滚动 ------------------------------ */
    function animateCount(el) {
        if (el.dataset.counted === '1') return;
        el.dataset.counted = '1';
        const target = parseFloat(el.dataset.count || '0');
        const decimals = parseInt(el.dataset.decimals || '0', 10);
        if (reduceMotion || !isFinite(target)) {
            el.textContent = target.toFixed(decimals);
            return;
        }
        const duration = 1250;
        const start = performance.now();
        const from = 0;
        function frame(now) {
            const progress = Math.min(1, (now - start) / duration);
            const eased = 1 - Math.pow(1 - progress, 4);
            el.textContent = (from + (target - from) * eased).toFixed(decimals);
            if (progress < 1) requestAnimationFrame(frame);
            else el.textContent = target.toFixed(decimals);
        }
        requestAnimationFrame(frame);
    }

    function initCounters() {
        $$('[data-count]').forEach((el) => {
            if (reduceMotion) {
                el.textContent = parseFloat(el.dataset.count || '0')
                    .toFixed(parseInt(el.dataset.decimals || '0', 10));
                return;
            }
            const observer = new IntersectionObserver((entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        animateCount(entry.target);
                        observer.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.25 });
            observer.observe(el);
        });
    }

    /* ------------------------------ 入场动画 ------------------------------ */
    function initReveal() {
        const items = $$('.reveal');
        if (!items.length) return;
        if (reduceMotion || !('IntersectionObserver' in window)) {
            items.forEach((el) => el.classList.add('is-visible'));
            return;
        }
        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    const el = entry.target;
                    const delay = parseInt(el.dataset.delay || '0', 10);
                    setTimeout(() => el.classList.add('is-visible'), delay);
                    observer.unobserve(el);
                }
            });
        }, { threshold: 0.08, rootMargin: '0px 0px -36px 0px' });
        items.forEach((el) => observer.observe(el));
    }

    /* ------------------------------ 侧边栏 ------------------------------ */
    function initSidebar() {
        const sidebar = $('#sidebar');
        const scrim = $('.sidebar-scrim');
        if (!sidebar) return;
        const open = () => {
            sidebar.classList.add('is-open');
            if (scrim) scrim.classList.add('is-visible');
            document.body.classList.add('nav-open');
        };
        const close = () => {
            sidebar.classList.remove('is-open');
            if (scrim) scrim.classList.remove('is-visible');
            document.body.classList.remove('nav-open');
        };
        $$('[data-sidebar-toggle]').forEach((btn) => btn.addEventListener('click', open));
        $$('[data-sidebar-close]').forEach((btn) => btn.addEventListener('click', close));
        $$('.sidebar .nav-link').forEach((link) => link.addEventListener('click', close));
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') close();
        });
        window.addEventListener('resize', () => {
            if (window.innerWidth > 900) close();
        });
    }

    /* ------------------------------ 页面过渡 ------------------------------ */
    function initPageTransitions() {
        const overlay = $('#pageTransition');
        if (!overlay) return;

        // Chromium 使用跨文档 View Transitions（CSS @view-transition），无需遮罩
        if (supportsCrossDocumentVT) {
            overlay.remove();
            return;
        }

        document.addEventListener('click', (event) => {
            if (event.defaultPrevented || event.button !== 0
                || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
                return;
            }
            const link = event.target.closest('a[href]');
            if (!link) return;
            if (link.target && link.target !== '_self') return;
            if (link.hasAttribute('download')) return;
            const rawHref = link.getAttribute('href');
            if (!rawHref || rawHref.startsWith('#') || rawHref.startsWith('javascript:')
                || rawHref.startsWith('mailto:') || rawHref.startsWith('tel:')) return;
            const url = new URL(link.href, window.location.href);
            if (url.origin !== window.location.origin) return;
            if (url.pathname === window.location.pathname && url.search === window.location.search
                && url.hash) return;
            event.preventDefault();
            overlay.classList.add('is-active');
            window.setTimeout(() => { window.location.href = url.href; }, reduceMotion ? 0 : 180);
        });

        window.addEventListener('pageshow', () => overlay.classList.remove('is-active'));
    }

    /* ------------------------------ Toast ------------------------------ */
    function dismissToast(toast) {
        if (!toast || toast.classList.contains('is-out')) return;
        toast.classList.add('is-out');
        setTimeout(() => toast.remove(), 320);
    }

    function showToast(message, type) {
        const stack = $('#toastStack');
        if (!stack) return;
        const toast = document.createElement('div');
        toast.className = 'toast toast-' + (type || 'info');
        toast.setAttribute('role', 'status');
        const iconId = type === 'error' ? '#i-info' : '#i-check';
        toast.innerHTML = '<span class="toast-icon"><svg class="icon"><use href="' + iconId + '"></use></svg></span>'
            + '<p></p><button class="toast-close" type="button" aria-label="关闭提示">'
            + '<svg class="icon"><use href="#i-close"></use></svg></button>';
        toast.querySelector('p').textContent = message;
        stack.appendChild(toast);
        toast.querySelector('[data-toast-close], .toast-close')
            .addEventListener('click', () => dismissToast(toast));
        setTimeout(() => dismissToast(toast), 4200);
    }

    function initToasts() {
        $$('[data-toast]').forEach((toast) => {
            toast.querySelectorAll('[data-toast-close]').forEach((btn) => {
                btn.addEventListener('click', () => dismissToast(toast));
            });
            setTimeout(() => dismissToast(toast), 4200);
        });
    }

    /* ------------------------------ 表单提交态 ------------------------------ */
    function initSubmitStates() {
        $$('form').forEach((form) => {
            form.addEventListener('submit', () => {
                if (form.dataset.skipSubmitState === '1') return;
                form.classList.add('is-submitting');
                const btn = form.querySelector('[data-submit-btn], button[type="submit"]');
                if (btn) {
                    btn.classList.add('is-loading');
                    btn.setAttribute('aria-busy', 'true');
                }
            });
        });
    }

    /* ------------------------------ 登录页 ------------------------------ */
    function initLogin() {
        const form = $('[data-login-form]');
        if (!form) return;
        const roleInput = $('#roleInput');
        const roleSwitch = $('.role-switch');
        const username = $('#username');
        const password = $('#password');
        const demoButtons = $$('[data-demo]');

        function setRole(role) {
            if (roleInput) roleInput.value = role;
            if (roleSwitch) roleSwitch.dataset.role = role;
            $$('[data-role-btn]').forEach((btn) => {
                const active = btn.dataset.roleBtn === role;
                btn.classList.toggle('is-active', active);
                btn.setAttribute('aria-selected', String(active));
            });
            if (username) {
                username.placeholder = role === 'teacher' ? '教师账号 / 学生学号' : '请输入学生学号';
            }
            demoButtons.forEach((btn) => btn.classList.toggle('is-demo-active', btn.dataset.demo === role));
        }

        $$('[data-role-btn]').forEach((btn) => {
            btn.addEventListener('click', () => setRole(btn.dataset.roleBtn));
        });
        setRole(roleInput ? roleInput.value : 'teacher');

        demoButtons.forEach((btn) => {
            btn.addEventListener('click', () => {
                setRole(btn.dataset.demo);
                if (username) username.value = btn.dataset.user;
                if (password) password.value = btn.dataset.pass;
                if (username) username.focus({ preventScroll: true });
                showToast('已填入演示账号，点击“进入系统”即可体验', 'info');
            });
        });

        const toggle = $('[data-password-toggle]');
        if (toggle && password) {
            toggle.addEventListener('click', () => {
                const show = password.type === 'password';
                password.type = show ? 'text' : 'password';
                toggle.classList.toggle('is-visible', show);
                toggle.setAttribute('aria-label', show ? '隐藏密码' : '显示密码');
            });
        }

        const remember = $('#rememberAccount');
        if (remember && username) {
            try {
                const saved = localStorage.getItem('gradeflow.username');
                if (saved) {
                    username.value = saved;
                    remember.checked = true;
                }
            } catch (e) { /* 忽略隐私模式限制 */ }
        }
        form.addEventListener('submit', () => {
            if (remember && username) {
                try {
                    if (remember.checked) localStorage.setItem('gradeflow.username', username.value);
                    else localStorage.removeItem('gradeflow.username');
                } catch (e) { /* 忽略 */ }
            }
        });

        if (username && !username.value) username.focus({ preventScroll: true });
    }

    /* ------------------------------ 成绩实时统计 ------------------------------ */
    function gradeClass(value) {
        if (value === '' || isNaN(value)) return 'none';
        if (value >= 90) return 'great';
        if (value >= 80) return 'good';
        if (value >= 70) return 'mid';
        if (value >= 60) return 'pass';
        return 'fail';
    }

    function initScoreForm() {
        const form = $('[data-score-form]');
        if (!form) return;
        const inputs = $$('[data-score-input]', form);
        const countEl = $('[data-live-count]');
        const avgEl = $('[data-live-avg]');
        const passEl = $('[data-live-pass]');
        const failEl = $('[data-live-fail]');
        const button = $('[data-save-scores]');

        function refresh() {
            let filled = 0;
            let sum = 0;
            let pass = 0;
            let fail = 0;
            inputs.forEach((input) => {
                const raw = input.value.trim();
                const value = parseFloat(raw);
                const tone = gradeClass(value);
                const labels = { great: '优秀', good: '良好', mid: '中等', pass: '及格', fail: '不及格', none: '—' };
                const chip = input.closest('tr') ? input.closest('tr').querySelector('[data-grade-chip]') : null;
                input.classList.remove('grade-great', 'grade-good', 'grade-mid', 'grade-pass', 'grade-fail', 'is-invalid');
                if (raw === '') {
                    input.classList.remove('is-filled');
                    if (chip) {
                        chip.className = 'score-chip grade-none';
                        chip.textContent = '—';
                    }
                    return;
                }
                if (!isFinite(value) || value < 0 || value > 100) {
                    input.classList.add('is-invalid');
                    if (chip) {
                        chip.className = 'score-chip grade-none';
                        chip.textContent = '—';
                    }
                    return;
                }
                input.classList.add('is-filled', 'grade-' + tone);
                if (chip) {
                    chip.className = 'score-chip grade-' + tone;
                    chip.textContent = labels[tone] || '—';
                }
                filled++;
                sum += value;
                if (value >= 60) pass++; else fail++;
            });
            if (countEl) countEl.textContent = filled;
            if (avgEl) avgEl.textContent = filled ? (sum / filled).toFixed(1) : '0.0';
            if (passEl) passEl.textContent = pass;
            if (failEl) failEl.textContent = fail;
            if (button) button.disabled = filled === 0;
        }

        inputs.forEach((input) => {
            input.addEventListener('input', refresh);
            input.addEventListener('blur', () => {
                const value = parseFloat(input.value);
                if (input.value.trim() !== '' && isFinite(value)) {
                    input.value = (Math.round(value * 10) / 10).toString();
                    refresh();
                }
            });
        });

        form.addEventListener('submit', (event) => {
            let invalid = null;
            inputs.forEach((input) => {
                const raw = input.value.trim();
                if (raw === '') return;
                const value = parseFloat(raw);
                if (!isFinite(value) || value < 0 || value > 100) {
                    input.classList.add('is-invalid');
                    if (!invalid) invalid = input;
                }
            });
            if (invalid) {
                event.preventDefault();
                form.classList.remove('is-submitting');
                if (button) button.classList.remove('is-loading');
                invalid.focus();
                invalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                showToast('请检查标红的分数：需在 0–100 之间', 'error');
            }
        });

        const reset = $('[data-reset-scores]');
        if (reset) {
            reset.addEventListener('click', () => {
                inputs.forEach((input) => {
                    input.value = input.dataset.initial || '';
                    input.dispatchEvent(new Event('input'));
                });
                showToast('已恢复到最近一次保存的分数', 'info');
            });
        }
        refresh();
    }

    /* ------------------------------ 弹窗与删除确认 ------------------------------ */
    function initDialogs() {
        $$('[data-open-dialog]').forEach((btn) => {
            btn.addEventListener('click', () => {
                const dialog = $(btn.dataset.openDialog);
                if (dialog && typeof dialog.showModal === 'function') dialog.showModal();
            });
        });
        $$('dialog.modal').forEach((dialog) => {
            $$('[data-close-dialog]', dialog).forEach((btn) => {
                btn.addEventListener('click', () => dialog.close());
            });
            dialog.addEventListener('click', (event) => {
                if (event.target === dialog) dialog.close();
            });
        });

        const confirmDialog = $('#confirmDialog');
        if (!confirmDialog) return;
        let targetForm = null;
        const nameEl = $('[data-confirm-name]', confirmDialog);
        $$('form[data-confirm-delete]').forEach((form) => {
            form.addEventListener('submit', (event) => {
                if (form.dataset.confirmed === '1') return;
                event.preventDefault();
                targetForm = form;
                if (nameEl) nameEl.textContent = form.dataset.studentName || '该学生';
                if (typeof confirmDialog.showModal === 'function') confirmDialog.showModal();
            });
        });
        const confirmBtn = $('[data-confirm-ok]', confirmDialog);
        if (confirmBtn) {
            confirmBtn.addEventListener('click', () => {
                if (!targetForm) return;
                targetForm.dataset.confirmed = '1';
                confirmDialog.close();
                targetForm.requestSubmit();
            });
        }
    }

    /* ------------------------------ 滚动到锚点 ------------------------------ */
    function initAnchors() {
        if (!window.location.hash) return;
        const target = document.querySelector(window.location.hash);
        if (!target) return;
        window.setTimeout(() => target.scrollIntoView({ behavior: reduceMotion ? 'auto' : 'smooth', block: 'start' }), 180);
    }

    /* ------------------------------ 初始化 ------------------------------ */
    function init() {
        renderToday();
        initCounters();
        initReveal();
        initSidebar();
        initPageTransitions();
        initToasts();
        initSubmitStates();
        initLogin();
        initScoreForm();
        initDialogs();
        initAnchors();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    window.GradeFlow = { showToast: showToast };
})();
