<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>领养申请</h2>
          <p>查看和处理用户提交的领养申请</p>
        </div>
        <span class="meta">当前 {{ rows.length }} / 总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="宠物、申请人或真实姓名" @keyup.enter.native="load" />
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
        <el-table-column prop="applicantName" label="申请人" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="状态" width="120">
          <template slot-scope="{ row }">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyReason" label="申请理由" min-width="220" show-overflow-tooltip />
        <el-table-column prop="reviewReason" label="处理意见" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="open(row)">详情/处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>

    <el-dialog title="领养申请处理" :visible.sync="dialog" width="760px" append-to-body custom-class="pet-admin-dialog">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="宠物">{{ active.petName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ active.applicantName || active.realName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ active.phone }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ active.city }}</el-descriptions-item>
        <el-descriptions-item label="居住情况">{{ active.housingType }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验">{{ active.petExperience }}</el-descriptions-item>
        <el-descriptions-item label="申请理由">{{ active.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="领养承诺">{{ active.commitment }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="86px" class="dialog-form">
        <el-form-item label="状态">
          <el-select v-model="form.status" class="full">
            <el-option label="初审通过" :value="1" />
            <el-option label="待补充" :value="2" />
            <el-option label="拒绝" :value="3" />
            <el-option label="已预约" :value="5" />
            <el-option label="关闭" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="看宠时间"><el-date-picker v-model="form.visitTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" class="full" /></el-form-item>
        <el-form-item label="看宠地点"><el-input v-model="form.visitAddress" maxlength="255" /></el-form-item>
        <el-form-item label="处理意见"><el-input v-model="form.reviewReason" type="textarea" :rows="4" maxlength="500" show-word-limit /></el-form-item>
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
  name: 'PetAdoptionApplication',
  data() {
    return {
      rows: [],
      total: 0,
      loading: false,
      saving: false,
      dialog: false,
      active: {},
      query: { pageNum: 1, pageSize: 10, keyword: '', status: undefined },
      form: this.emptyForm(),
      statusOptions: [
        { label: '已提交', value: 0 },
        { label: '初审通过', value: 1 },
        { label: '待补充', value: 2 },
        { label: '拒绝', value: 3 },
        { label: '已撤回', value: 4 },
        { label: '已预约', value: 5 },
        { label: '已确认领养', value: 6 },
        { label: '已关闭', value: 7 }
      ]
    }
  },
  created() {
    this.load()
  },
  methods: {
    emptyForm() {
      return { id: null, status: 1, visitTime: '', visitAddress: '', reviewReason: '' }
    },
    load() {
      this.loading = true
      petApi.listAdoptionApplications(this.query).then(res => {
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
      this.form = {
        id: row.id,
        status: row.status === 0 ? 1 : row.status,
        visitTime: row.visitTime || '',
        visitAddress: row.visitAddress || '',
        reviewReason: row.reviewReason || ''
      }
      this.dialog = true
    },
    save() {
      this.saving = true
      petApi.updateAdoptionApplicationStatus(this.form).then(() => {
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
      return ({ 0: 'warning', 1: 'success', 2: 'warning', 3: 'danger', 4: 'info', 5: 'success', 6: 'success', 7: 'info' })[value] || ''
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
