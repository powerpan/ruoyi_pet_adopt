<template>
  <AuditTable title="帖子审核" :list-fn="api.listPosts" :audit-fn="api.auditPost" search-field="title" status-field="auditStatus" :columns="columns">
    <template slot="row-actions" slot-scope="{ row, reload }">
      <el-button size="mini" type="text" class="pet-admin-danger" @click="remove(row, reload)">删除</el-button>
    </template>
  </AuditTable>
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default {
  name: 'PetPost',
  components: { AuditTable },
  data: () => ({
    api,
    columns: [
      { prop: 'id', label: 'ID', width: 80 }, { prop: 'title', label: '标题' }, { prop: 'postType', label: '类型' },
      { prop: 'authorName', label: '作者' }, { prop: 'petName', label: '关联宠物' }, { prop: 'auditStatus', label: '审核状态', statusMap: { 0: { label: '待审核', type: 'warning' }, 1: { label: '通过', type: 'success' }, 2: { label: '拒绝', type: 'danger' } } }, { prop: 'content', label: '正文', width: 260 }, { prop: 'createTime', label: '发布时间', width: 160 }
    ]
  }),
  methods: {
    remove(row, reload) {
      this.$modal.confirm(`确认删除动态「${row.title || row.id}」吗？删除后评论、点赞和收藏记录也会一并清理。`).then(() => {
        return api.deletePost(row.id)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        reload()
      }).catch(() => {})
    }
  }
}
</script>
