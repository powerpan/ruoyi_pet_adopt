<template>
  <div class="pet-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :multiple="limit > 1"
      :limit="limit"
      :file-list="fileList"
      :list-type="listType"
      :accept="accept"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-remove="handleRemove"
      :on-preview="handlePreview"
      :on-exceed="handleExceed"
      :on-error="handleError"
    >
      <i v-if="listType === 'picture-card'" class="el-icon-plus"></i>
      <el-button v-else size="small" type="primary" icon="el-icon-upload2">上传文件</el-button>
      <div slot="tip" class="el-upload__tip" v-if="showTip">
        支持 {{ fileType.join('/') }}，单个文件不超过 {{ fileSize }}MB
      </div>
    </el-upload>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

export default {
  name: 'PetUpload',
  props: {
    value: { type: [String, Array], default: '' },
    limit: { type: Number, default: 5 },
    fileSize: { type: Number, default: 20 },
    fileType: { type: Array, default: () => ['jpg', 'jpeg', 'png', 'gif', 'mp4', 'mov', 'webm', 'pdf'] },
    listType: { type: String, default: 'text' },
    showTip: { type: Boolean, default: true }
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      headers: { Authorization: 'Bearer ' + getToken() },
      fileList: []
    }
  },
  computed: {
    accept() {
      return this.fileType.map(type => '.' + type).join(',')
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(value) {
        const list = Array.isArray(value) ? value : String(value || '').split(',')
        this.fileList = list.filter(Boolean).map((url, index) => ({
          name: this.fileName(url),
          url: resolveCanvasAssetUrl(url),
          responsePath: url,
          uid: index + '-' + url
        }))
      }
    }
  },
  methods: {
    fileName(url) {
      return String(url || '').split('/').pop() || 'file'
    },
    beforeUpload(file) {
      const ext = file.name.includes('.') ? file.name.split('.').pop().toLowerCase() : ''
      if (this.fileType.length && this.fileType.indexOf(ext) === -1) {
        this.$message.error('文件格式不正确')
        return false
      }
      if (this.fileSize && file.size / 1024 / 1024 > this.fileSize) {
        this.$message.error(`文件大小不能超过 ${this.fileSize}MB`)
        return false
      }
      this.headers.Authorization = 'Bearer ' + getToken()
      return true
    },
    handleSuccess(res, file, fileList) {
      if (!res || res.code !== 200) {
        this.$message.error((res && res.msg) || '上传失败')
        return
      }
      file.responsePath = res.fileName
      file.url = resolveCanvasAssetUrl(res.fileName)
      this.syncValue(fileList)
    },
    handleRemove(file, fileList) {
      this.syncValue(fileList)
    },
    handlePreview(file) {
      window.open(file.url || resolveCanvasAssetUrl(file.responsePath), '_blank')
    },
    handleExceed() {
      this.$message.warning(`最多上传 ${this.limit} 个文件`)
    },
    handleError() {
      this.$message.error('上传失败，请检查后端服务')
    },
    syncValue(fileList) {
      const paths = (fileList || [])
        .map(file => file.responsePath || (file.response && file.response.fileName) || file.url)
        .filter(Boolean)
      this.$emit('input', paths.join(','))
      this.$emit('change', paths)
    }
  }
}
</script>

<style scoped>
.pet-upload {
  width: 100%;
}
::v-deep .el-upload--picture-card,
::v-deep .el-upload-list--picture-card .el-upload-list__item {
  border-radius: 8px;
  border-color: var(--pet-border);
}
::v-deep .el-upload--picture-card {
  background: var(--pet-surface-soft);
}
::v-deep .el-upload--picture-card:hover {
  border-color: var(--pet-primary);
  color: var(--pet-primary);
}
::v-deep .el-upload__tip {
  color: var(--pet-text-muted);
  line-height: 1.6;
}
</style>
