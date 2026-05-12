<template>
  <div class="word-preview-root" :class="{ 'word-preview-root--compact': compact }">
    <div
      v-if="isDocx"
      ref="docxFit"
      class="word-docx-fit"
      :style="innerStyle"
    >
      <div class="word-docx-zoom-shell" :style="docxShellStyle">
        <vue-office-docx
          class="word-preview-inner word-preview-inner--docx"
          :src="src"
          @rendered="onDocxRendered"
        />
      </div>
    </div>
    <vue-office-pdf
      v-else-if="isPdf"
      class="word-preview-inner"
      :style="innerStyle"
      :src="src"
      @rendered="onRendered"
    />
    <div
      v-else-if="isExcel"
      class="word-excel-shell"
      :style="innerStyle"
    >
      <vue-office-excel
        class="word-preview-inner word-preview-inner--excel"
        :src="src"
        style="width: 100%; height: 100%; box-sizing: border-box"
        @rendered="onRendered"
        @error="onExcelError"
      />
    </div>
    <div v-else class="word-preview-unsupported">
      暂不支持该格式文档预览
    </div>
  </div>
</template>

<script>
import VueOfficeDocx from "@vue-office/docx";
import VueOfficePdf from "@vue-office/pdf";
import VueOfficeExcel from "@vue-office/excel";
import "@vue-office/docx/lib/index.css";
import "@vue-office/excel/lib/index.css";

/** 逻辑版面宽度（A4 @96dpi），整页在此宽度下排版后再 zoom 缩放到容器宽，与 PDF「适应宽度」一致 */
const DOCX_BASE_WIDTH = 816;

export default {
  name: "WordPreview",
  components: {
    VueOfficeDocx,
    VueOfficePdf,
    VueOfficeExcel,
  },
  props: {
    src: {
      type: String,
      default: "",
    },
    compact: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      docxZoom: 1,
      _docxResizeObserver: null,
    };
  },
  computed: {
    ext() {
      return this.resolveExt(this.src);
    },
    isDocx() {
      return this.ext === "docx";
    },
    isPdf() {
      return this.ext === "pdf";
    },
    isExcel() {
      return this.ext === "xlsx" || this.ext === "xls";
    },
    innerStyle() {
      if (this.compact) {
        return {
          width: "100%",
          height: "54vh",
          maxHeight: "560px",
          minHeight: "300px",
        };
      }
      return {
        width: "100%",
        height: "82vh",
        maxHeight: "880px",
      };
    },
    docxShellStyle() {
      return {
        width: `${DOCX_BASE_WIDTH}px`,
        zoom: this.docxZoom,
        margin: "0 auto",
      };
    },
  },
  watch: {
    src() {
      this.docxZoom = 1;
      this.$nextTick(() => {
        this.measureDocxZoom();
        this.bindDocxResizeObserver();
      });
    },
    isDocx: {
      immediate: true,
      handler(val) {
        this.$nextTick(() => {
          if (val) {
            this.measureDocxZoom();
            this.bindDocxResizeObserver();
          } else {
            this.unbindDocxResizeObserver();
          }
        });
      },
    },
  },
  mounted() {
    window.addEventListener("resize", this.measureDocxZoom);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.measureDocxZoom);
    this.unbindDocxResizeObserver();
  },
  methods: {
    resolveExt(url) {
      if (!url) {
        return "";
      }
      const path = String(url).split("?")[0].split("#")[0];
      const parts = path.split(".");
      return (parts[parts.length - 1] || "").toLowerCase().trim();
    },
    measureDocxZoom() {
      this.$nextTick(() => {
        const el = this.$refs.docxFit;
        if (!el || !this.isDocx) {
          return;
        }
        const cw = el.clientWidth;
        if (cw <= 0) {
          return;
        }
        const s = Math.min(1, cw / DOCX_BASE_WIDTH);
        if (Math.abs(s - this.docxZoom) > 0.0005) {
          this.docxZoom = s;
        }
      });
    },
    onDocxRendered() {
      this.bindDocxResizeObserver();
      this.measureDocxZoom();
      this.$nextTick(() => {
        requestAnimationFrame(() => this.measureDocxZoom());
      });
    },
    onRendered() {},
    onExcelError(err) {
      if (process.env.NODE_ENV === "development" && err) {
        // eslint-disable-next-line no-console
        console.warn("[WordPreview] excel render error", err);
      }
    },
    bindDocxResizeObserver() {
      if (typeof ResizeObserver === "undefined") {
        return;
      }
      this.unbindDocxResizeObserver();
      const el = this.$refs.docxFit;
      if (!el) {
        return;
      }
      this._docxResizeObserver = new ResizeObserver(() => this.measureDocxZoom());
      this._docxResizeObserver.observe(el);
    },
    unbindDocxResizeObserver() {
      if (this._docxResizeObserver) {
        this._docxResizeObserver.disconnect();
        this._docxResizeObserver = null;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.word-preview-root {
  width: 100%;
  box-sizing: border-box;
  overflow-x: hidden;
}

.word-preview-root--compact {
  border-radius: 12px;
  overflow-x: hidden;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.word-docx-fit {
  width: 100%;
  box-sizing: border-box;
  overflow-x: hidden;
  overflow-y: auto;
  background: #525659;
  padding: 12px 0 16px;
}

.word-docx-zoom-shell {
  box-sizing: border-box;
}

.word-preview-inner {
  box-sizing: border-box;
}

.word-excel-shell {
  width: 100%;
  box-sizing: border-box;
  overflow: auto;
  background: #eef1f6;
  border-radius: 8px;
}

.word-preview-unsupported {
  padding: 32px 20px;
  text-align: center;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.55);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px dashed rgba(15, 23, 42, 0.12);
}
</style>

<style lang="scss">
.file-preview-dialog {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.14);

  .el-dialog__header {
    padding: 14px 20px 12px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(247, 249, 252, 0.96) 100%);
    border-bottom: 1px solid rgba(15, 23, 42, 0.06);
    position: relative;

    &::after {
      content: "";
      position: absolute;
      left: 20px;
      right: 20px;
      bottom: 0;
      height: 2px;
      border-radius: 2px;
      background: linear-gradient(
        90deg,
        rgba(212, 175, 55, 0.9) 0%,
        rgba(86, 163, 255, 0.45) 55%,
        transparent 100%
      );
    }
  }

  .el-dialog__title {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
  }

  .el-dialog__headerbtn .el-dialog__close {
    color: rgba(15, 23, 42, 0.45);
    &:hover {
      color: #1f4e8f;
    }
  }

  .el-dialog__body {
    padding: 14px 18px 18px;
    background: linear-gradient(180deg, #f7f9fc 0%, #f2f6fb 100%);
    overflow-x: hidden;
  }
}

.file-preview-dialog__body {
  max-height: min(64vh, 640px);
  overflow: auto;
  overflow-x: hidden;
}

/* Word：页与页之间留白与阴影（版面宽度由外层 816px + zoom 控制，不再强制 section 定宽导致横向滚动） */
.word-docx-fit .vue-office-docx .docx-wrapper > section.docx {
  margin-bottom: 12px !important;
  background: #fff !important;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.22);
}

.word-docx-fit .vue-office-docx {
  box-sizing: border-box;
}

.word-docx-fit .vue-office-docx .docx-wrapper {
  padding: 0 8px 8px;
  box-sizing: border-box;
}

/* PDF：紧凑模式按容器宽度缩放 */
.word-preview-root--compact .word-preview-inner canvas {
  max-width: 100% !important;
  height: auto !important;
}

.word-excel-shell .vue-office-excel {
  min-height: 240px;
}
</style>
