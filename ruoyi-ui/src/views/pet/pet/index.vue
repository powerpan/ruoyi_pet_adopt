<template>
  <AuditTable title="宠物档案" :list-fn="api.listPets" search-field="name" :columns="columns" />
</template>
<script>
import AuditTable from '../components/AuditTable.vue'
import { petApi as api } from '@/api/pet'
export default { name: 'PetPet', components: { AuditTable }, data: () => ({ api, columns: [
  { prop: 'id', label: 'ID', width: 80 }, { prop: 'ownerName', label: '所属用户' }, { prop: 'name', label: '宠物名' },
  { prop: 'species', label: '物种' }, { prop: 'breed', label: '品种' }, { prop: 'healthStatus', label: '健康状态' }
] }) }
</script>
