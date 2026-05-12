<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>服务咨询/预约</h2>
          <p>处理用户提交的服务意向和联系状态</p>
        </div>
        <span class="meta">当前 {{ rows.length }} / 总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="load">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="rows" v-loading="loading" class="pet-admin-table" empty-text="暂无服务咨询">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="预约用户" width="110" />
        <el-table-column prop="serviceName" label="服务" min-width="130" />
        <el-table-column prop="merchantName" label="商家" min-width="120" />
        <el-table-column prop="petName" label="宠物" width="90" />
        <el-table-column prop="contactPhone" label="电话" width="130" />
        <el-table-column prop="serviceTime" label="预约时间" width="170" />
        <el-table-column prop="message" label="留言" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template slot-scope="scope">
            <el-tag size="mini" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="168">
          <template slot-scope="scope">
            <el-button size="mini" type="text" :disabled="scope.row.status !== 0" @click="updateStatus(scope.row, 1)">接受预约</el-button>
            <el-button size="mini" type="text" :disabled="scope.row.status !== 1" @click="updateStatus(scope.row, 2)">已完成</el-button>
            <el-button size="mini" type="text" class="pet-admin-danger" :disabled="scope.row.status === 2 || scope.row.status === 3" @click="updateStatus(scope.row, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>
  </div>
</template>

<script>
import { petApi } from '@/api/pet'

export default {
  name: 'PetServiceRequest',
  data() {
    return {
      rows: [],
      total: 0,
      loading: false,
      query: { pageNum: 1, pageSize: 10, status: '' },
      statusOptions: [
        { label: '待处理', value: 0 },
        { label: '接受预约', value: 1 },
        { label: '已完成', value: 2 },
        { label: '已取消', value: 3 }
      ]
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      const params = { ...this.query, status: this.query.status === '' ? undefined : this.query.status }
      petApi.listServiceRequests(params).then(res => {
        this.rows = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    resetQuery() {
      this.query = { pageNum: 1, pageSize: this.query.pageSize || 10, status: '' }
      this.load()
    },
    updateStatus(row, status) {
      petApi.updateServiceRequestStatus({ id: row.id, status }).then(() => {
        this.$modal.msgSuccess('状态已更新')
        this.load()
      })
    },
    statusLabel(status) {
      return ({ 0: '待处理', 1: '接受预约', 2: '已完成', 3: '已取消' })[status] || '待处理'
    },
    statusType(status) {
      return ({ 1: 'primary', 2: 'success', 3: 'info' })[status] || 'warning'
    }
  }
}
</script>

<style scoped>
</style>
