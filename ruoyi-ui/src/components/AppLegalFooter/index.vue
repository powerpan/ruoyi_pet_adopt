<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <footer class="legal-footer" :class="[`legal-footer--${theme}`, { 'legal-footer--compact': compact }]">
    <div class="legal-brand">
      <strong>宠物领养平台</strong>
      <a href="https://github.com/powerpan/ruoyi_pet_adopt.git" target="_blank" rel="noopener noreferrer">Copyright © 2026 https://github.com/powerpan/ruoyi_pet_adopt.git</a>
    </div>
    <div class="legal-links">
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">京ICP备2026000000号-1</a>
      <a href="http://www.beian.gov.cn/portal/registerSystemInfo" target="_blank" rel="noopener noreferrer">京公网安备 11010502060000号</a>
      <span>京宠信备 2026-000001号</span>
      <span>后台管理本地开发环境</span>
    </div>
    <div class="legal-meta">
      <span>服务邮箱：support@ruoyi-pet-adopt.local</span>
      <span>违法和不良信息举报：report@ruoyi-pet-adopt.local</span>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'AppLegalFooter',
  props: {
    theme: {
      type: String,
      default: 'light'
    },
    compact: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>
.legal-footer {
  display: grid;
  gap: 8px;
  color: #7b8794;
  font-size: 12px;
  line-height: 1.7;
  letter-spacing: 0;
  text-align: center;
}
.legal-brand,
.legal-links,
.legal-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px 18px;
  flex-wrap: wrap;
}
.legal-brand strong {
  color: #263445;
  font-size: 13px;
}
.legal-brand a,
.legal-links a,
.legal-links span,
.legal-meta span {
  color: inherit;
  text-decoration: none;
}
.legal-brand a:hover,
.legal-links a:hover {
  color: var(--current-color, #409EFF);
}
.legal-footer--dark {
  color: rgba(255, 255, 255, .82);
  text-shadow: 0 1px 2px rgba(0, 0, 0, .22);
}
.legal-footer--dark .legal-brand strong,
.legal-footer--dark .legal-brand a:hover,
.legal-footer--dark .legal-links a:hover {
  color: #fff;
}
.legal-footer--compact {
  gap: 4px;
  font-size: 11px;
}
.legal-footer--compact .legal-brand strong {
  font-size: 12px;
}
@media (max-width: 640px) {
  .legal-brand,
  .legal-links,
  .legal-meta {
    gap: 4px 10px;
  }
}
</style>
