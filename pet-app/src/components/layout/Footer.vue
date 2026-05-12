<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="footer">
    <div class="footer-inner">
      <div>
        <strong>宠物领养平台</strong>
      </div>
      <AppLegalFooter class="footer-legal" />
    </div>
  </div>
</template>

<script>
import AppLegalFooter from '@/components/AppLegalFooter'

export default {
  name: "Footer",
  components: { AppLegalFooter }
}
</script>

<style scoped>
.footer {
  padding: 28px 20px 30px;
  color: var(--pet-text-muted, #63716d);
  background: #fff;
  border-top: 1px solid var(--pet-border, #e2e8e5);
}
.footer-inner {
  max-width: 1120px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(220px, 340px) minmax(0, 1fr);
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}
.footer strong {
  color: var(--pet-text, #17211f);
}
.footer-legal {
  justify-self: end;
  text-align: right;
}
@media (max-width: 640px) {
  .footer-inner {
    display: grid;
    grid-template-columns: 1fr;
  }
  .footer-legal {
    justify-self: start;
    text-align: left;
  }
}
</style>
