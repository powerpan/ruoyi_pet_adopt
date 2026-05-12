<template>
  <AuditTable
    title="评价屏蔽审核"
    description="服务评价默认公开；这里只处理商家提交的屏蔽申请，通过后评价从用户端隐藏，拒绝后继续公开"
    :list-fn="api.listReviews"
    :audit-fn="api.auditReview"
    status-field="hideStatus"
    :pending-status="1"
    :audit-pass-status="2"
    :audit-reject-status="3"
    audit-pass-text="同意屏蔽"
    audit-reject-text="驳回申请"
    :status-options="statusOptions"
    :columns="columns"
  />
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default { name: 'PetReview', components: { AuditTable }, data: () => ({ api, statusOptions: [
  { label: '未申请', value: 0 }, { label: '申请审核中', value: 1 }, { label: '已屏蔽', value: 2 }, { label: '已驳回', value: 3 }
], columns: [
  { prop: 'id', label: 'ID', width: 80 }, { prop: 'serviceName', label: '服务' }, { prop: 'merchantName', label: '商家' },
  { prop: 'userName', label: '评价用户' }, { prop: 'rating', label: '评分' }, { prop: 'content', label: '评价内容', width: 260 },
  { prop: 'hideReason', label: '屏蔽理由', width: 220 }, { prop: 'hideAuditReason', label: '审核意见', width: 220 },
  { prop: 'hideStatus', label: '屏蔽状态', statusMap: { 0: { label: '未申请', type: 'info' }, 1: { label: '申请审核中', type: 'warning' }, 2: { label: '已屏蔽', type: 'info' }, 3: { label: '已驳回', type: 'danger' } } }
] }) }
</script>
