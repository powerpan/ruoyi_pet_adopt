<template>
  <AuditTable title="健康提醒" :list-fn="api.listReminders" status-field="status" :status-options="statusOptions" :columns="columns" />
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default { name: 'PetReminder', components: { AuditTable }, data: () => ({ api, statusOptions: [
  { label: '待提醒', value: 0 }, { label: '已完成', value: 1 }, { label: '已过期', value: 2 }
], columns: [
  { prop: 'id', label: 'ID', width: 80 }, { prop: 'ownerName', label: '所属用户' }, { prop: 'petName', label: '宠物' },
  { prop: 'title', label: '标题' }, { prop: 'reminderType', label: '类型' }, { prop: 'dueTime', label: '到期时间', width: 160 },
  { prop: 'status', label: '状态', statusMap: { 0: { label: '待提醒', type: 'warning' }, 1: { label: '已完成', type: 'success' }, 2: { label: '已过期', type: 'danger' } } }
] }) }
</script>
