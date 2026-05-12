<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col v-for="item in cards" :key="item.key" :xs="12" :sm="8" :lg="6">
        <el-card shadow="never" class="stat-card" @click.native="go(item.path)">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ overview[item.key] || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" class="quick-card">
      <div slot="header">快捷入口</div>
      <el-button v-for="item in quick" :key="item.path" @click="go(item.path)">
        {{ item.label }}
      </el-button>
    </el-card>
  </div>
</template>

<script>
import { petApi } from '@/api/pet'

export default {
  name: 'Index',
  data() {
    return {
      overview: {},
      cards: [
        { key: 'profiles', label: '用户主页', path: '/pet/profile' },
        { key: 'pets', label: '宠物档案', path: '/pet/pet' },
        { key: 'posts', label: '帖子总数', path: '/pet/post' },
        { key: 'pendingPosts', label: '待审帖子', path: '/pet/post' },
        { key: 'merchants', label: '商家总数', path: '/pet/merchant' },
        { key: 'pendingMerchants', label: '待审商家', path: '/pet/merchant' },
        { key: 'services', label: '服务项目', path: '/pet/service' },
        { key: 'reminders', label: '到期提醒', path: '/pet/reminder' }
      ],
      quick: [
        { label: '帖子审核', path: '/pet/post' },
        { label: '商家审核', path: '/pet/merchant' },
        { label: '话题管理', path: '/pet/topic' },
        { label: '服务管理', path: '/pet/service' },
        { label: '数据统计', path: '/pet/statistics' }
      ]
    }
  },
  created() {
    petApi.overview().then(res => {
      this.overview = res.data || {}
    })
  },
  methods: {
    go(path) {
      this.$router.push(path).catch(() => {})
    }
  }
}
</script>

<style scoped>
.stat-card { margin-bottom: 16px; cursor: pointer; }
.label { color: #909399; }
.value { margin-top: 12px; font-size: 30px; font-weight: 700; color: #2f8f83; }
.quick-card { margin-top: 8px; }
.quick-card .el-button { margin: 0 10px 10px 0; }
</style>
