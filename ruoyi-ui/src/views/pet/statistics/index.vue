<template>
  <div class="app-container pet-admin-page">
    <div class="pet-admin-title statistics-title">
      <div>
        <h2>数据统计</h2>
        <p>总览用户、内容、商家、服务和提醒的核心运营数量</p>
      </div>
      <el-button size="small" icon="el-icon-refresh" @click="load">刷新</el-button>
    </div>
    <div class="pet-stat-grid">
      <div v-for="item in cards" :key="item.key" class="pet-stat-card">
        <div class="label">{{ item.label }}</div>
        <div class="value">{{ overview[item.key] || 0 }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import { petApi } from '@/api/pet'
export default {
  name: 'PetStatistics',
  data() {
    return {
      overview: {},
      cards: [
        { key: 'profiles', label: '用户主页' }, { key: 'pets', label: '宠物档案' },
        { key: 'adoptions', label: '领养发布' }, { key: 'pendingAdoptions', label: '待审领养' },
        { key: 'adoptionApplications', label: '领养申请' }, { key: 'successfulAdoptions', label: '成功领养' },
        { key: 'pendingAdoptionFollowups', label: '待回访' }, { key: 'abnormalAdoptionFollowups', label: '异常回访' },
        { key: 'posts', label: '帖子总数' }, { key: 'pendingPosts', label: '待审帖子' },
        { key: 'merchants', label: '商家总数' }, { key: 'pendingMerchants', label: '待审商家' },
        { key: 'services', label: '服务项目' }, { key: 'reminders', label: '提醒数量' }
      ]
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      petApi.overview().then(res => { this.overview = res.data || {} })
    }
  }
}
</script>
<style scoped>
.statistics-title {
  margin-bottom: 16px;
}
</style>
