<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>{{ title }}</h2>
          <p>{{ description || defaultDescription }}</p>
        </div>
        <span class="meta">当前 {{ rows.length }} / 总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item v-if="searchField" label="关键词">
          <el-input v-model="query[searchField]" placeholder="请输入关键词" clearable @keyup.enter.native="load" />
        </el-form-item>
        <el-form-item v-if="showStatusFilter" label="状态">
          <el-select v-model="query[statusField]" clearable placeholder="全部">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-tabs v-if="usesAuditWorkflow" v-model="auditView" class="audit-tabs" @tab-click="switchAuditView">
        <el-tab-pane label="待审核" name="pending" />
        <el-tab-pane label="审核历史" name="history" />
      </el-tabs>
      <el-table v-loading="loading" :data="rows" class="pet-admin-table" :empty-text="emptyText">
        <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" :min-width="col.width || 120" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <el-tag v-if="col.statusMap" size="mini" :type="statusType(row[col.prop], col.statusMap)">{{ statusText(row[col.prop], col.statusMap) }}</el-tag>
            <span v-else>{{ row[col.prop] }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="openDetail(row)">详情</el-button>
            <el-button v-if="canAudit(row)" size="mini" type="text" @click="openAudit(row, auditPassStatus)">{{ auditPassText }}</el-button>
            <el-button v-if="canAudit(row)" size="mini" type="text" class="pet-admin-danger" @click="openAudit(row, auditRejectStatus)">{{ auditRejectText }}</el-button>
            <slot name="row-actions" :row="row" :reload="load"></slot>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>

    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="720px" append-to-body custom-class="pet-admin-dialog">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item v-for="col in columns" :key="col.prop" :label="col.label">
          <span v-if="col.statusMap">{{ statusText(activeRow[col.prop], col.statusMap) }}</span>
          <span v-else>{{ activeRow[col.prop] }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-form v-if="auditMode" label-width="86px" class="audit-form">
        <el-form-item label="审核结果">
          <el-tag :type="auditStatus === auditPassStatus ? 'success' : 'danger'">{{ auditStatus === auditPassStatus ? auditPassText : auditRejectText }}</el-tag>
        </el-form-item>
        <el-form-item label="审核原因">
          <el-input v-model="auditReason" type="textarea" :rows="4" maxlength="300" show-word-limit placeholder="可填写通过说明或拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="detailOpen = false">关闭</el-button>
        <el-button v-if="auditMode" type="primary" :loading="auditing" @click="submitAudit">确认审核</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AuditTable',
  props: {
    title: String,
    description: String,
    listFn: Function,
    auditFn: Function,
    columns: Array,
    searchField: String,
    statusField: String,
    targetIdField: { type: String, default: 'id' },
    pendingStatus: { type: Number, default: 0 },
    auditPassStatus: { type: Number, default: 1 },
    auditRejectStatus: { type: Number, default: 2 },
    auditPassText: { type: String, default: '通过' },
    auditRejectText: { type: String, default: '拒绝' },
    statusOptions: {
      type: Array,
      default: () => [
        { label: '待审核', value: 0 },
        { label: '通过', value: 1 },
        { label: '拒绝', value: 2 }
      ]
    }
  },
  data() {
    return {
      rows: [],
      total: 0,
      loading: false,
      auditing: false,
      query: { pageNum: 1, pageSize: 10 },
      auditView: 'pending',
      detailOpen: false,
      auditMode: false,
      auditStatus: 1,
      auditReason: '',
      activeRow: {}
    }
  },
  computed: {
    usesAuditWorkflow() {
      return Boolean(this.auditFn && this.statusField)
    },
    showStatusFilter() {
      return Boolean(this.statusField && !this.usesAuditWorkflow)
    },
    defaultDescription() {
      if (this.usesAuditWorkflow) return '待审核只处理新申请，已处理记录自动进入审核历史'
      return '查看业务数据和筛选记录'
    },
    emptyText() {
      if (!this.usesAuditWorkflow) return '暂无匹配数据'
      return this.auditView === 'pending' ? '暂无待审核记录' : '暂无审核历史'
    },
    detailTitle() {
      return this.auditMode ? '审核处理' : '详情'
    }
  },
  created() {
    this.applyAuditQuery()
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      this.listFn(this.query).then(res => {
        this.rows = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    resetQuery() {
      this.query = { pageNum: 1, pageSize: this.query.pageSize || 10 }
      this.applyAuditQuery()
      this.load()
    },
    switchAuditView() {
      this.query.pageNum = 1
      this.applyAuditQuery()
      this.load()
    },
    applyAuditQuery() {
      if (!this.usesAuditWorkflow) return
      if (this.auditView === 'pending') {
        this.$set(this.query, this.statusField, this.pendingStatus)
        this.$delete(this.query, 'statusScope')
      } else {
        this.$delete(this.query, this.statusField)
        this.$set(this.query, 'statusScope', 'history')
      }
    },
    canAudit(row) {
      if (!this.auditFn) return false
      if (!this.statusField) return true
      return Number(row[this.statusField]) === Number(this.pendingStatus)
    },
    openDetail(row) {
      this.activeRow = row
      this.auditMode = false
      this.detailOpen = true
    },
    openAudit(row, auditStatus) {
      if (!this.canAudit(row)) {
        this.$modal.msgWarning('已处理记录只能查看详情，不能重复审核')
        return
      }
      this.activeRow = row
      this.auditStatus = auditStatus
      this.auditReason = ''
      this.auditMode = true
      this.detailOpen = true
    },
    submitAudit() {
      this.auditing = true
      this.auditFn({
        targetId: this.activeRow[this.targetIdField],
        auditStatus: this.auditStatus,
        auditReason: this.auditReason
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.detailOpen = false
        this.auditView = 'history'
        this.applyAuditQuery()
        this.load()
      }).finally(() => {
        this.auditing = false
      })
    },
    statusText(value, map) {
      const item = map[value]
      return item ? item.label : value
    },
    statusType(value, map) {
      const item = map[value]
      return item ? item.type : ''
    }
  }
}
</script>

<style scoped>
.audit-form {
  margin-top: 18px;
}
.audit-tabs {
  margin: -2px 0 12px;
}
</style>
