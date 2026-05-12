<template>
  <div class="app-container pet-admin-page">
    <el-card shadow="never" class="pet-admin-card">
      <div slot="header" class="pet-admin-title">
        <div>
          <h2>话题管理</h2>
          <p>维护用户端话题广场和首页筛选入口</p>
        </div>
        <span class="meta">总计 {{ total }}</span>
      </div>
      <el-form :inline="true" class="pet-admin-filter">
        <el-form-item label="名称"><el-input v-model="query.name" placeholder="话题名称" clearable /></el-form-item>
        <el-form-item><el-button type="primary" icon="el-icon-search" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="primary" icon="el-icon-plus" @click="openForm()">新增话题</el-button></el-form-item>
      </el-form>
      <el-table :data="rows" v-loading="loading" class="pet-admin-table" empty-text="暂无话题">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="话题" />
        <el-table-column prop="description" label="说明" show-overflow-tooltip />
        <el-table-column prop="coverUrl" label="封面" show-overflow-tooltip />
        <el-table-column prop="postCount" label="帖子数" width="100" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === 0 ? 'success' : 'info'">{{ scope.row.status === 0 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="128">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="openForm(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" class="pet-admin-danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
    </el-card>

    <el-dialog :title="form.id ? '编辑话题' : '新增话题'" :visible.sync="open" width="560px" append-to-body custom-class="pet-admin-dialog">
      <el-form label-width="80px">
        <el-form-item label="话题名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="封面"><ImageUpload v-model="form.coverUrl" :limit="1" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
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
  name: 'PetTopic',
  data() {
    return {
      rows: [],
      total: 0,
      loading: false,
      open: false,
      query: { pageNum: 1, pageSize: 10, name: '' },
      form: this.emptyForm()
    }
  },
  created() {
    this.load()
  },
  methods: {
    emptyForm() {
      return { name: '', description: '', coverUrl: '', status: 0 }
    },
    load() {
      this.loading = true
      petApi.listTopics(this.query).then(res => {
        this.rows = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    openForm(row) {
      this.form = row ? { ...row } : this.emptyForm()
      this.open = true
    },
    save() {
      const request = this.form.id ? petApi.updateTopic(this.form) : petApi.addTopic(this.form)
      request.then(() => {
        this.$modal.msgSuccess('保存成功')
        this.open = false
        this.load()
      })
    },
    remove(row) {
      this.$modal.confirm(`确认删除话题「${row.name}」吗？`).then(() => {
        return petApi.deleteTopic(row.id)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.load()
      }).catch(() => {})
    }
  }
}
</script>
<style scoped>
</style>
