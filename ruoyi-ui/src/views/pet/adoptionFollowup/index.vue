<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>领养回访</h2>
          <p>跟踪领养后的照护回访和异常处理</p>
        </div>
        <span class="meta">当前 {{ rows.length }} / 总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="宠物、领养人或发布方" @keyup.enter.native="load" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="rows" class="pet-admin-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="petName" label="宠物" min-width="120" />
        <el-table-column prop="adopterName" label="领养人" min-width="120" />
        <el-table-column prop="publisherName" label="发布方" min-width="120" />
        <el-table-column prop="followupRound" label="轮次" width="80" />
        <el-table-column prop="planTime" label="计划时间" width="160" />
        <el-table-column prop="actualTime" label="提交时间" width="160" />
        <el-table-column label="状态" width="120">
          <template slot-scope="{ row }">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalReason" label="异常说明" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="open(row)">详情/处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>

    <el-dialog title="回访处理" :visible.sync="dialog" width="720px" append-to-body custom-class="pet-admin-dialog">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="宠物">{{ active.petName }}</el-descriptions-item>
        <el-descriptions-item label="轮次">{{ active.followupRound }}</el-descriptions-item>
        <el-descriptions-item label="计划时间">{{ active.planTime }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ active.actualTime }}</el-descriptions-item>
        <el-descriptions-item label="健康情况">{{ active.healthStatus }}</el-descriptions-item>
        <el-descriptions-item label="生活情况">{{ active.livingStatus }}</el-descriptions-item>
        <el-descriptions-item label="异常说明">{{ active.abnormalReason }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ active.handleResult }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="86px" class="dialog-form">
        <el-form-item label="处理状态">
          <el-select v-model="form.status" class="full">
            <el-option label="正常归档" :value="2" />
            <el-option label="异常待处理" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果"><el-input v-model="form.handleResult" type="textarea" :rows="4" maxlength="500" show-word-limit /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialog = false">关闭</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { petApi } from '@/api/pet'

export default {
  name: 'PetAdoptionFollowup',
  data() {
    return {
      rows: [],
      total: 0,
      loading: false,
      saving: false,
      dialog: false,
      active: {},
      query: { pageNum: 1, pageSize: 10, keyword: '', status: undefined },
      form: { id: null, status: 4, handleResult: '' },
      statusOptions: [
        { label: '待回访', value: 0 },
        { label: '已提交', value: 1 },
        { label: '正常归档', value: 2 },
        { label: '异常待处理', value: 3 },
        { label: '已关闭', value: 4 }
      ]
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      petApi.listAdoptionFollowups(this.query).then(res => {
        this.rows = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    reset() {
      this.query = { pageNum: 1, pageSize: 10, keyword: '', status: undefined }
      this.load()
    },
    open(row) {
      this.active = row
      this.form = { id: row.id, status: row.status === 3 ? 4 : row.status, handleResult: row.handleResult || '' }
      this.dialog = true
    },
    save() {
      this.saving = true
      petApi.handleAdoptionFollowup(this.form).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.dialog = false
        this.load()
      }).finally(() => {
        this.saving = false
      })
    },
    statusText(value) {
      const found = this.statusOptions.find(item => item.value === value)
      return found ? found.label : value
    },
    statusType(value) {
      return ({ 0: 'warning', 1: '', 2: 'success', 3: 'danger', 4: 'info' })[value] || ''
    }
  }
}
</script>

<style scoped>
.dialog-form {
  margin-top: 18px;
}
.full {
  width: 100%;
}
</style>
