<template>
  <AuditTable
    title="用户主页管理"
    description="查看用户主页资料、宠物领养平台身份和内容数据，不承载认证审核操作"
    :list-fn="api.listProfiles"
    search-field="nickname"
    :columns="columns"
  />
</template>

<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'

export default {
  name: 'PetProfile',
  components: { AuditTable },
  data() {
    return {
      api,
      columns: [
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'nickname', label: '昵称' },
        { prop: 'bio', label: '简介', width: 240 },
        {
          prop: 'bloggerStatus',
          label: '博主状态',
          statusMap: {
            0: { label: '普通用户', type: 'info' },
            1: { label: '已认证', type: 'success' },
            2: { label: '认证审核中', type: 'warning' },
            3: { label: '认证未通过', type: 'danger' }
          }
        },
        { prop: 'postCount', label: '帖子数' },
        { prop: 'followerCount', label: '粉丝数' }
      ]
    }
  }
}
</script>
