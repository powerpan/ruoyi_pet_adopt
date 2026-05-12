<template>
  <div class="word-preview-page">
    <div v-if="!src" class="preview-empty">未传入可预览文件地址</div>
    <iframe
      v-else-if="isPdf"
      class="pdf-preview-frame"
      :src="pdfSrc"
      frameborder="0"
    ></iframe>
    <vue-office-docx v-else :src="src" @rendered="rendered" />
  </div>
</template>

<script>
import VueOfficeDocx from "@vue-office/docx";
import "@vue-office/docx/lib/index.css";

export default {
  components: {
    VueOfficeDocx,
  },
  computed: {
    src() {
      const raw = this.$route.query && this.$route.query.src;
      if (!raw) return "";
      return decodeURIComponent(String(raw));
    },
    isPdf() {
      return /\.pdf(\?|$)/i.test(this.src);
    },
    pdfSrc() {
      // 使用浏览器原生 PDF 预览能力，兼容当前依赖版本
      return this.src;
    },
  },
  methods: {
    rendered() {
      // 保留空方法，避免组件事件未处理报警
    },
  },
};
</script>

<style scoped>
.word-preview-page {
  min-height: 100vh;
  background: #f8fafc;
}

.pdf-preview-frame {
  width: 100%;
  height: 100vh;
  display: block;
}

.preview-empty {
  padding: 32px;
  color: #64748b;
  font-size: 16px;
  text-align: center;
}
</style>