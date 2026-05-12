<template>
  <AuditTable
    title="博主认证审核"
    description="待审核只展示用户主动提交的博主认证申请，通过或拒绝后进入审核历史"
    :list-fn="api.listProfiles"
    :audit-fn="api.auditBlogger"
    search-field="nickname"
    status-field="bloggerStatus"
    :pending-status="2"
    :audit-pass-status="1"
    :audit-reject-status="3"
    :columns="columns"
  />
</template>

<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'

export default {
  name: 'PetBlogger',
  components: { AuditTable },
  data() {
    return {
      api,
      columns: [
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'nickname', label: '昵称' },
        { prop: 'bio', label: '认证说明', width: 280 },
        {
          prop: 'bloggerStatus',
          label: '认证状态',
          statusMap: {
            1: { label: '已通过', type: 'success' },
            2: { label: '待审核', type: 'warning' },
            3: { label: '已拒绝', type: 'danger' }
          }
        },
        { prop: 'postCount', label: '帖子数' },
        { prop: 'createTime', label: '提交时间', width: 160 }
      ]
    }
  }
}
</script>
