<template>
  <div class="upload-file">
    <div v-if="uploadUi.active" class="upload-progress-wrap">
      <el-progress
        :percentage="Math.min(100, Math.round(uploadUi.percent))"
        :stroke-width="10"
        :color="progressColors"
      />
      <span class="upload-progress-tip">{{ uploadUi.useRealProgress ? "正在上传（真实进度）" : "正在上传…" }}</span>
    </div>
    <div class="upload-file__actions">
      <el-upload
        v-if="fileList.length === 0"
        :multiple="multiple"
        :action="uploadFileUrl"
        :before-upload="handleBeforeUpload"
        :file-list="fileList"
        :limit="limit"
        :on-error="handleUploadError"
        :on-exceed="handleExceed"
        :on-success="handleUploadSuccess"
        :on-progress="handleProgress"
        :show-file-list="false"
        :headers="headers"
        class="upload-file-uploader"
        ref="fileUpload"
      >
        <el-button size="mini" type="primary" :disabled="uploadUi.active">上传文件</el-button>
      </el-upload>
      <el-button
        v-else
        size="mini"
        type="primary"
        :disabled="uploadUi.active"
        @click="handlePreview"
      >预览文件</el-button>
      <el-button
        size="mini"
        type="danger"
        :disabled="uploadUi.active"
        @click="handleDelete(0)"
      >删除</el-button>
    </div>
    <el-dialog
      :visible.sync="dialogVisible"
      title="文件预览"
      width="780px"
      top="6vh"
      :before-close="handleClose"
      append-to-body
      custom-class="file-preview-dialog"
    >
      <div class="file-preview-dialog__body">
        <word-preview :src="fileUrl" :compact="true" />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";
import { resolveAdminMediaUrl } from "@/utils/canvasAssetUrl";

/** 小于该字节数视为「小文件」：用约 1s 的模拟进度；大于等于则用 XMLHttpRequest 真实进度 */
const SMALL_FILE_BYTES = 1024 * 1024;
const SMALL_FILE_MIN_MS = 1000;
const FAKE_PROGRESS_MS = 900;

export default {
  name: "FileUpload",
  props: {
    value: [String, Object, Array],
    limit: {
      type: Number,
      default: 5,
    },
    fileSize: {
      type: Number,
      default: 5,
    },
    fileType: {
      type: Array,
      default: () => ["doc", "xls", "ppt", "txt", "pdf"],
    },
    isShowTip: {
      type: Boolean,
      default: true,
    },
    /** 文档库场景建议 false，避免多文件并发导致进度与合并逻辑错乱 */
    multiple: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      dialogVisible: false,
      fileUrl: "",
      number: 0,
      uploadList: [],
      baseUrl: process.env.VUE_APP_BASE_API,
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/common/upload/word",
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      fileList: [],
      uploadUi: {
        active: false,
        percent: 0,
        useRealProgress: false,
        startedAt: 0,
        rafId: null,
      },
      progressColors: [
        { color: "#2d6bb3", percentage: 40 },
        { color: "#1f4e8f", percentage: 100 },
      ],
    };
  },
  watch: {
    value: {
      handler(val) {
        if (val) {
          let temp = 1;
          const list = Array.isArray(val) ? val : String(val).split(",");
          this.fileList = list.map((item) => {
            if (typeof item === "string") {
              item = { name: item, url: item };
            }
            item.uid = item.uid || new Date().getTime() + temp++;
            return item;
          });
          this.fileUrl = this.listToString(this.fileList);
        } else {
          this.fileList = [];
          this.fileUrl = "";
          this.dialogVisible = false;
        }
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    showTip() {
      return this.isShowTip && (this.fileType || this.fileSize);
    },
  },
  beforeDestroy() {
    this.clearProgressAnimation();
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    handlePreview() {
      if (this.fileList.length > 0) {
        this.dialogVisible = true;
        this.fileUrl = resolveAdminMediaUrl(this.fileList[0].url);
      }
    },
    clearProgressAnimation() {
      if (this.uploadUi.rafId != null) {
        cancelAnimationFrame(this.uploadUi.rafId);
        this.uploadUi.rafId = null;
      }
    },
    resetUploadUi() {
      this.clearProgressAnimation();
      this.uploadUi.active = false;
      this.uploadUi.percent = 0;
      this.uploadUi.useRealProgress = false;
      this.uploadUi.startedAt = 0;
    },
    startFakeProgress() {
      const start = Date.now();
      const step = () => {
        if (!this.uploadUi.active || this.uploadUi.useRealProgress) {
          return;
        }
        const t = Math.min(1, (Date.now() - start) / FAKE_PROGRESS_MS);
        const eased = 1 - (1 - t) * (1 - t);
        this.uploadUi.percent = Math.min(88, eased * 88);
        if (t < 1) {
          this.uploadUi.rafId = requestAnimationFrame(step);
        }
      };
      this.uploadUi.rafId = requestAnimationFrame(step);
    },
    handleBeforeUpload(file) {
      if (this.fileType) {
        const fileName = file.name.split(".");
        const fileExt = fileName[fileName.length - 1];
        const isTypeOk = this.fileType.indexOf(fileExt) >= 0;
        if (!isTypeOk) {
          this.$modal.msgError(`文件格式不正确, 请上传${this.fileType.join("/")}格式文件!`);
          return false;
        }
      }
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$modal.msgError(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      this.resetUploadUi();
      this.uploadUi.active = true;
      this.uploadUi.startedAt = Date.now();
      this.uploadUi.useRealProgress = file.size >= SMALL_FILE_BYTES;
      this.uploadUi.percent = 0;
      if (!this.uploadUi.useRealProgress) {
        this.startFakeProgress();
      }
      this.number++;
      return true;
    },
    handleExceed() {
      this.$modal.msgError(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    handleProgress(event) {
      if (!this.uploadUi.active || !this.uploadUi.useRealProgress) {
        return;
      }
      const p = event && typeof event.percent === "number" ? event.percent : 0;
      this.uploadUi.percent = Math.min(99, Math.max(this.uploadUi.percent, p));
    },
    handleUploadError() {
      this.clearProgressAnimation();
      this.resetUploadUi();
      this.$modal.msgError("上传文件失败，请重试");
      if (this.number > 0) {
        this.number--;
      }
    },
    afterProgressFinish(callback) {
      const elapsed = Date.now() - this.uploadUi.startedAt;
      const wait =
        !this.uploadUi.useRealProgress ? Math.max(0, SMALL_FILE_MIN_MS - elapsed) : 0;
      this.clearProgressAnimation();
      setTimeout(() => {
        this.uploadUi.percent = 100;
        setTimeout(() => {
          this.uploadUi.active = false;
          this.uploadUi.percent = 0;
          callback();
        }, 220);
      }, wait);
    },
    handleUploadSuccess(res, file) {
      if (res.code === 200) {
        this.uploadList.push({ name: res.fileName, url: res.fileName });
        this.afterProgressFinish(() => {
          this.uploadedSuccessfully();
          const displayName =
            res.originalFilename || (file && file.name) || this.getFileName(res.fileName) || "";
          this.$emit("handle-upload", {
            url: this.listToString(this.fileList),
            name: displayName,
          });
          this.$modal.msgSuccess("上传成功");
        });
      } else {
        this.clearProgressAnimation();
        this.resetUploadUi();
        this.number--;
        this.$modal.msgError(res.msg);
        if (this.$refs.fileUpload) {
          this.$refs.fileUpload.handleRemove(file);
        }
        this.uploadedSuccessfully();
      }
    },
    handleDelete(index) {
      this.fileList.splice(index, 1);
      this.fileUrl = "";
      this.$emit("input", this.listToString(this.fileList));
      this.$emit("handle-upload", { name: "", url: "" });
    },
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.concat(this.uploadList);
        this.fileUrl = this.listToString(this.fileList);
        this.uploadList = [];
        this.number = 0;
        this.$emit("input", this.listToString(this.fileList));
      }
    },
    getFileName(name) {
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1);
      }
      return name;
    },
    listToString(list, separator) {
      let strs = "";
      separator = separator || ",";
      for (const i in list) {
        strs += list[i].url + separator;
      }
      return strs !== "" ? strs.substr(0, strs.length - 1) : "";
    },
  },
};
</script>

<style scoped lang="scss">
.upload-file {
  width: 100%;
}

.upload-progress-wrap {
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(247, 249, 252, 0.95);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.upload-progress-tip {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.55);
}

.upload-file__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}
</style>
