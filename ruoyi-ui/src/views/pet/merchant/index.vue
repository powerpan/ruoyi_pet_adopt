<template>
  <div>
    <AuditTable
      title="商家资质审核"
      :list-fn="api.listMerchants"
      :audit-fn="api.auditMerchant"
      search-field="name"
      status-field="qualificationStatus"
      :columns="columns"
    >
      <template v-slot:row-actions="{ row }">
        <el-button size="mini" type="text" @click="openQualifications(row)">资质</el-button>
      </template>
    </AuditTable>

    <el-dialog title="商家资质材料" :visible.sync="open" width="720px" append-to-body custom-class="pet-admin-dialog">
      <el-table :data="qualifications" v-loading="loading" class="pet-admin-table" empty-text="暂无资质材料">
        <el-table-column prop="materialType" label="材料类型" width="140" />
        <el-table-column prop="materialUrl" label="材料文件" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-link :href="mediaUrl(scope.row.materialUrl)" target="_blank" type="primary">{{ scope.row.materialUrl }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="110">
          <template slot-scope="scope">
            <el-tag size="mini" :type="statusType(scope.row.auditStatus)">{{ statusLabel(scope.row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
      </el-table>
    </el-dialog>
  </div>
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
import { resolveAdminMediaUrl } from '@/utils/canvasAssetUrl'

export default {
  name: 'PetMerchant',
  components: { AuditTable },
  data() {
    return {
      api,
      open: false,
      loading: false,
      qualifications: [],
      columns: [
        { prop: 'id', label: 'ID', width: 80 },
        { prop: 'name', label: '商家名称' },
        { prop: 'ownerName', label: '提交用户' },
        { prop: 'contactName', label: '联系人' },
        { prop: 'phone', label: '电话' },
        { prop: 'address', label: '地址', width: 220 },
        { prop: 'qualificationStatus', label: '资质状态', statusMap: { 0: { label: '待审核', type: 'warning' }, 1: { label: '通过', type: 'success' }, 2: { label: '拒绝', type: 'danger' }, 3: { label: '拒绝', type: 'danger' } } }
      ]
    }
  },
  methods: {
    openQualifications(row) {
      this.open = true
      this.loading = true
      api.listMerchantQualifications(row.id, { pageNum: 1, pageSize: 50 }).then(res => {
        this.qualifications = res.rows || []
      }).finally(() => {
        this.loading = false
      })
    },
    mediaUrl(url) {
      return resolveAdminMediaUrl(url)
    },
    statusLabel(status) {
      return ({ 0: '待审核', 1: '通过', 2: '拒绝', 3: '拒绝' })[status] || '待审核'
    },
    statusType(status) {
      return ({ 1: 'success', 2: 'danger', 3: 'danger' })[status] || 'warning'
    }
  }
}
</script>
