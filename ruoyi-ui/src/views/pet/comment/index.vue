<template>
  <AuditTable title="评论审核" :list-fn="api.listComments" :audit-fn="api.auditComment" status-field="auditStatus" :columns="columns" />
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default { name: 'PetComment', components: { AuditTable }, data: () => ({ api, columns: [
  { prop: 'id', label: 'ID', width: 80 }, { prop: 'postTitle', label: '帖子' }, { prop: 'userName', label: '评论用户' },
  { prop: 'content', label: '评论内容', width: 260 }, { prop: 'auditStatus', label: '审核状态', statusMap: { 0: { label: '待审核', type: 'warning' }, 1: { label: '通过', type: 'success' }, 2: { label: '拒绝', type: 'danger' } } }, { prop: 'createTime', label: '时间', width: 160 }
] }) }
</script>
