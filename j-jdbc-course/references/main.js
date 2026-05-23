/* ===== j-jdbc 课程 — 交互元素 JavaScript ===== */

document.addEventListener('DOMContentLoaded', function() {
  initNavigation();
  initProgressBar();
  initQuizzes();
  initFlowAnimations();
  initScrollSnap();
});

/* ===== 导航点 ===== */
function initNavigation() {
  const dots = document.querySelectorAll('.nav-dot');
  const modules = document.querySelectorAll('.module');
  if (!dots.length || !modules.length) return;

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        const idx = Array.from(modules).indexOf(entry.target);
        dots.forEach((d, i) => d.classList.toggle('active', i === idx));
      }
    });
  }, { threshold: 0.5 });

  modules.forEach(m => observer.observe(m));

  dots.forEach((dot, i) => {
    dot.addEventListener('click', () => {
      modules[i].scrollIntoView({ behavior: 'smooth' });
    });
  });
}

/* ===== 滚动进度条 ===== */
function initProgressBar() {
  const bar = document.querySelector('.progress-bar');
  if (!bar) return;
  window.addEventListener('scroll', () => {
    const scrollTop = window.scrollY;
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    const progress = docHeight > 0 ? (scrollTop / docHeight) * 100 : 0;
    bar.style.width = progress + '%';
  });
}

/* ===== 测验系统 ===== */
function initQuizzes() {
  document.querySelectorAll('.quiz-card').forEach(card => {
    const options = card.querySelectorAll('.quiz-option');
    const feedback = card.querySelector('.quiz-feedback');
    if (!options.length) return;

    options.forEach(opt => {
      opt.addEventListener('click', () => {
        // 如果已作答，不再处理
        if (card.querySelector('.quiz-option.selected')) return;

        const isCorrect = opt.dataset.correct === 'true';
        opt.classList.add('selected');
        opt.classList.add(isCorrect ? 'correct' : 'wrong');

        // 显示正确答案
        options.forEach(o => {
          if (o.dataset.correct === 'true') o.classList.add('correct');
          if (o !== opt && opt.dataset.correct !== 'true' && o.dataset.correct !== 'true') {
            o.style.opacity = '0.5';
          }
        });

        if (feedback) {
          feedback.classList.add('show');
          feedback.classList.add(isCorrect ? 'correct' : 'wrong');
          feedback.textContent = isCorrect
            ? '✓ 正确！' + (feedback.dataset.explain || '')
            : '✗ 再想想看。' + (feedback.dataset.explain || '');
        }
      });
    });
  });
}

/* ===== 数据流动画（通过 data-steps JSON） ===== */
function initFlowAnimations() {
  document.querySelectorAll('.flow-animation[data-steps]').forEach(container => {
    try {
      const steps = JSON.parse(container.dataset.steps);
      // 如果已经有节点，使用已有的；否则从 data-steps 生成
      if (!container.querySelector('.flow-node')) {
        renderFlowSteps(container, steps);
      }
    } catch (e) {
      /* data-steps JSON 解析失败，跳过 */
    }
  });
}

function renderFlowSteps(container, steps) {
  container.innerHTML = '';
  steps.forEach((step, i) => {
    if (i > 0) {
      const arrow = document.createElement('span');
      arrow.className = 'flow-arrow';
      arrow.textContent = '→';
      container.appendChild(arrow);
    }
    const node = document.createElement('div');
    node.className = 'flow-node';
    node.innerHTML = `
      <div class="flow-node-icon ${step.iconClass || ''}">${step.icon || '●'}</div>
      <div class="flow-node-label">${step.label}</div>
    `;
    container.appendChild(node);
  });
}

/* ===== 滚动捕捉辅助 ===== */
function initScrollSnap() {
  // 确保 scroll-snap 在内容加载后正常触发
  if (CSS.supports('scroll-snap-type', 'y proximity')) return;
  // 降级：不支持 scroll-snap 时手动校准
  const modules = document.querySelectorAll('.module');
  if (modules.length < 2) return;

  let isScrolling = false;
  window.addEventListener('scroll', () => {
    if (isScrolling) return;
    isScrolling = true;
    clearTimeout(window._snapTimer);
    window._snapTimer = setTimeout(() => {
      let closest = null;
      let minDist = Infinity;
      const mid = window.innerHeight / 2;
      modules.forEach(m => {
        const rect = m.getBoundingClientRect();
        const dist = Math.abs(rect.top);
        if (dist < minDist) { minDist = dist; closest = m; }
      });
      if (closest && minDist < window.innerHeight * 0.4) {
        closest.scrollIntoView({ behavior: 'smooth' });
      }
      isScrolling = false;
    }, 150);
  });
}

/* ===== 拖拽测验（简单版：点击匹配） ===== */
function initDragQuiz(containerId) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const items = container.querySelectorAll('.drag-item');
  const slots = container.querySelectorAll('.drag-slot');

  items.forEach(item => {
    item.addEventListener('click', () => {
      const slot = Array.from(slots).find(s => !s.querySelector('.slot-value'));
      if (!slot) return;
      const slotVal = slot.querySelector('.slot-value');
      if (slotVal) slotVal.textContent = item.textContent;
      item.style.display = 'none';
    });
  });
}
