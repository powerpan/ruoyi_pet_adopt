<template>
  <AuditTable
    title="领养审核"
    description="审核用户发布的待领养宠物，通过后进入领养大厅"
    :list-fn="api.listAdoptions"
    :audit-fn="api.auditAdoption"
    search-field="keyword"
    status-field="status"
    :pending-status="1"
    :audit-pass-status="2"
    :audit-reject-status="6"
    audit-pass-text="通过"
    audit-reject-text="拒绝"
    :status-options="statusOptions"
    :columns="columns"
  >
    <template slot="row-actions" slot-scope="{ row, reload }">
      <el-button v-if="row.status === 2 || row.status === 3" size="mini" type="text" class="pet-admin-danger" @click="changeStatus(row, 5, reload)">下架</el-button>
      <el-button v-if="row.status === 5" size="mini" type="text" @click="changeStatus(row, 2, reload)">恢复</el-button>
    </template>
  </AuditTable>
</template>

<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'

export default {
  name: 'PetAdoption',
  components: { AuditTable },
  data() {
    return {
      api,
      statusOptions: [
        { label: '待审核', value: 1 },
        { label: '已发布', value: 2 },
        { label: '已预约', value: 3 },
        { label: '已领养', value: 4 },
        { label: '已下架', value: 5 },
        { label: '拒绝', value: 6 }
      ],
      columns: [
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'name', label: '宠物' },
        { prop: 'species', label: '物种', width: 90 },
        { prop: 'breed', label: '品种' },
        { prop: 'city', label: '城市' },
        { prop: 'publisherName', label: '发布方' },
        { prop: 'applicationCount', label: '申请数', width: 90 },
        {
          prop: 'status',
          label: '状态',
          statusMap: {
            1: { label: '待审核', type: 'warning' },
            2: { label: '已发布', type: 'success' },
            3: { label: '已预约', type: 'warning' },
            4: { label: '已领养', type: 'info' },
            5: { label: '已下架', type: 'info' },
            6: { label: '拒绝', type: 'danger' }
          }
        },
        { prop: 'adoptionRequirements', label: '领养要求', width: 260 },
        { prop: 'createTime', label: '发布时间', width: 160 }
      ]
    }
  },
  methods: {
    changeStatus(row, status, reload) {
      const text = status === 5 ? '下架' : '恢复展示'
      this.$prompt(`请输入${text}原因`, '状态处理', {
        inputType: 'textarea',
        inputPlaceholder: '可填写原因',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        return api.updateAdoptionStatus({ id: row.id, status, auditReason: value || '' })
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        reload()
      }).catch(() => {})
    }
  }
}
</script>
