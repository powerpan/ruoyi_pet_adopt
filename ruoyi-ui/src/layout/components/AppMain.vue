<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <section class="app-main">
    <div class="app-main-view">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="cachedViews">
          <router-view v-if="!$route.meta.link" :key="key" />
        </keep-alive>
      </transition>
      <iframe-toggle />
    </div>
    <!-- 管理端主框架统一展示项目版权：https://github.com/powerpan/ruoyi_pet_adopt.git -->
    <app-legal-footer class="admin-legal-footer" />
  </section>
</template>

<script>
import iframeToggle from "./IframeToggle/index"
import AppLegalFooter from "@/components/AppLegalFooter"

export default {
  name: 'AppMain',
  components: { iframeToggle, AppLegalFooter },
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  box-sizing: border-box;
  /* 主内容区固定为视口高，overflow 才能生效；仅 min-height 时区会随内容无限增高，底部被裁且无任何滚动条 */
  height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
}

.app-main-view {
  flex: 1 0 auto;
  min-width: 0;
}

.admin-legal-footer {
  flex: 0 0 auto;
  margin: 18px auto 0;
  padding: 18px 16px 20px;
  width: min(100%, 1180px);
  border-top: 1px solid #dfe7f1;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

/* hasTagsView 在 layout 的 main-container 上；84 = 顶栏 50 + 标签栏 34 */
.hasTagsView .fixed-header + .app-main {
  padding-top: 84px;
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}
</style>
