<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>服务管理</h2>
          <p>维护商家服务、价格、封面和上下架状态</p>
        </div>
        <span class="meta">总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item label="服务"><el-input v-model="query.serviceName" placeholder="服务名称" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.serviceType" clearable placeholder="全部">
            <el-option v-for="type in serviceTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" icon="el-icon-search" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="primary" icon="el-icon-plus" @click="openForm()">新增服务</el-button></el-form-item>
      </el-form>
      <el-table :data="rows" v-loading="loading" class="pet-admin-table" empty-text="暂无服务">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="serviceName" label="服务" min-width="132" />
        <el-table-column label="类型" width="92">
          <template slot-scope="scope">{{ serviceLabel(scope.row.serviceType) }}</template>
        </el-table-column>
        <el-table-column prop="merchantName" label="商家" min-width="120" />
        <el-table-column label="价格" width="100">
          <template slot-scope="scope">{{ scope.row.priceMin || 0 }}-{{ scope.row.priceMax || 0 }}</template>
        </el-table-column>
        <el-table-column prop="reviewScore" label="评分" width="70" />
        <el-table-column prop="reviewCount" label="评价数" width="70" />
        <el-table-column label="状态" width="70">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === 0 ? 'success' : 'info'">{{ scope.row.status === 0 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="118">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="openForm(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" class="pet-admin-danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>

    <el-dialog :title="form.id ? '编辑服务' : '新增服务'" :visible.sync="open" width="680px" append-to-body custom-class="pet-admin-dialog">
      <el-form label-width="92px">
        <el-form-item label="商家">
          <el-select v-model="form.merchantId" filterable placeholder="选择已审核商家" class="full">
            <el-option v-for="merchant in merchants" :key="merchant.id" :label="`${merchant.name}（${merchant.id}）`" :value="merchant.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务名称"><el-input v-model="form.serviceName" /></el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType" placeholder="选择类型" class="full">
            <el-option v-for="type in serviceTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <div class="inline-row">
            <el-input v-model="form.priceMin" placeholder="最低价" />
            <el-input v-model="form.priceMax" placeholder="最高价" />
          </div>
        </el-form-item>
        <el-form-item label="封面"><ImageUpload v-model="form.coverUrl" :limit="1" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">上架</el-radio>
            <el-radio :label="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { petApi } from '@/api/pet'

export default {
  name: 'PetService',
  data() {
    return {
      rows: [],
      merchants: [],
      total: 0,
      loading: false,
      open: false,
      query: { pageNum: 1, pageSize: 10, serviceName: '', serviceType: '' },
      form: this.emptyForm(),
      serviceTypes: [
        { label: '洗护美容', value: 'grooming' },
        { label: '寄养托管', value: 'boarding' },
        { label: '医疗问诊', value: 'medical' },
        { label: '训练行为', value: 'training' },
        { label: '用品零售', value: 'retail' }
      ]
    }
  },
  created() {
    this.load()
    this.loadMerchants()
  },
  methods: {
    emptyForm() {
      return { merchantId: '', serviceName: '', serviceType: '', priceMin: '', priceMax: '', coverUrl: '', description: '', status: 0 }
    },
    load() {
      this.loading = true
      petApi.listServices(this.query).then(res => {
        this.rows = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    loadMerchants() {
      petApi.listMerchants({ pageNum: 1, pageSize: 200, qualificationStatus: 1 }).then(res => {
        this.merchants = res.rows || []
      })
    },
    openForm(row) {
      this.form = row ? { ...row } : this.emptyForm()
      this.open = true
    },
    save() {
      if (!this.form.merchantId || !this.form.serviceName) {
        this.$modal.msgWarning('请选择商家并填写服务名称')
        return
      }
      const payload = {
        ...this.form,
        priceMin: this.form.priceMin === '' ? null : this.form.priceMin,
        priceMax: this.form.priceMax === '' ? null : this.form.priceMax
      }
      const request = payload.id ? petApi.updateService(payload) : petApi.addService(payload)
      request.then(() => {
        this.$modal.msgSuccess('保存成功')
        this.open = false
        this.load()
      })
    },
    remove(row) {
      this.$modal.confirm(`确认删除服务「${row.serviceName}」吗？`).then(() => {
        return petApi.deleteService(row.id)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.load()
      }).catch(() => {})
    },
    serviceLabel(value) {
      const item = this.serviceTypes.find(type => type.value === value)
      return item ? item.label : value
    }
  }
}
</script>
<style scoped>
.full {
  width: 100%;
}
.inline-row {
  display: flex;
  gap: 10px;
}
</style>
