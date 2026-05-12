<template>
  <AuditTable title="健康档案" :list-fn="api.listHealthRecords" :columns="columns" />
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default { name: 'PetHealth', components: { AuditTable }, data: () => ({ api, columns: [
  { prop: 'id', label: 'ID', width: 80 }, { prop: 'ownerName', label: '所属用户' }, { prop: 'petName', label: '宠物' },
  { prop: 'title', label: '标题' }, { prop: 'recordType', label: '类型' }, { prop: 'recordDate', label: '记录日期' }, { prop: 'nextDueDate', label: '到期日期' }
] }) }
</script>
