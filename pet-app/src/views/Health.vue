<template>
  <div class="page">
    <div class="page-head pet-page-title">
      <div>
        <h1>健康档案与到期提醒</h1>
        <p>管理疫苗、驱虫、体检、用药、复诊等记录，并建立提醒。</p>
      </div>
      <div class="head-actions">
        <el-select v-model="selectedPetId" clearable placeholder="全部宠物" @change="loadData">
          <el-option v-for="pet in pets" :key="pet.id" :label="petLabel(pet)" :value="pet.id" />
        </el-select>
        <el-button icon="el-icon-document" @click="$router.push('/pets')">宠物档案</el-button>
      </div>
    </div>

    <el-alert v-if="pets.length === 0" title="还没有宠物档案，建议先在“宠物”页面绑定宠物，再维护健康记录。" type="info" show-icon :closable="false" class="hint" />

    <el-tabs v-model="tab" class="health-tabs">
      <el-tab-pane label="健康档案" name="records">
        <el-card shadow="never" class="panel form-panel">
          <div slot="header" class="panel-head">
            <span>{{ record.id ? '编辑健康记录' : '新增健康记录' }}</span>
            <el-button type="text" @click="$router.push('/pets')">宠物档案</el-button>
          </div>
          <el-form label-width="88px" class="form-grid">
            <el-form-item label="宠物">
              <el-select v-model="record.petId" clearable placeholder="选择宠物">
                <el-option v-for="pet in pets" :key="pet.id" :label="petLabel(pet)" :value="pet.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="record.recordType" placeholder="选择类型">
                <el-option v-for="type in healthTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题"><el-input v-model="record.title" placeholder="例如：年度疫苗接种" /></el-form-item>
            <el-form-item label="记录日期"><el-date-picker v-model="record.recordDate" value-format="yyyy-MM-dd" placeholder="选择日期" /></el-form-item>
            <el-form-item label="下次到期"><el-date-picker v-model="record.nextDueDate" value-format="yyyy-MM-dd" placeholder="可选" /></el-form-item>
            <el-form-item label="附件" class="wide">
              <pet-upload v-model="record.attachmentUrls" :limit="6" :file-type="['jpg','jpeg','png','gif','pdf']" :file-size="20" />
            </el-form-item>
            <el-form-item label="说明" class="wide">
              <el-input v-model="record.description" type="textarea" :rows="3" placeholder="症状、用量、医生建议等" />
            </el-form-item>
            <el-form-item class="wide">
              <el-button type="primary" :loading="savingRecord" @click="saveRecord">{{ record.id ? '保存修改' : '保存记录' }}</el-button>
              <el-button @click="record = emptyRecord()">清空</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="panel table-panel">
          <div slot="header" class="panel-head">
            <span>记录列表</span>
            <el-button type="text" @click="loadData">刷新</el-button>
          </div>
          <el-table :data="records" v-loading="loading.records">
            <el-table-column prop="title" label="标题" min-width="140" />
            <el-table-column prop="recordType" label="类型" width="90" />
            <el-table-column label="宠物" width="120">
              <template slot-scope="scope">{{ petName(scope.row.petId) }}</template>
            </el-table-column>
            <el-table-column prop="recordDate" label="日期" width="110" />
            <el-table-column prop="nextDueDate" label="下次到期" width="110" />
            <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="128">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="editRecord(scope.row)">编辑</el-button>
                <el-button size="mini" type="text" class="danger-text" @click="removeRecord(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="到期提醒" name="reminders">
        <el-card shadow="never" class="panel form-panel">
          <div slot="header" class="panel-head">
            <span>{{ reminder.id ? '编辑提醒' : '新增提醒' }}</span>
            <span class="panel-sub">第一版先在提醒列表内展示，不接短信或推送。</span>
          </div>
          <el-form label-width="88px" class="form-grid">
            <el-form-item label="宠物">
              <el-select v-model="reminder.petId" clearable placeholder="选择宠物">
                <el-option v-for="pet in pets" :key="pet.id" :label="petLabel(pet)" :value="pet.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="reminder.reminderType" placeholder="选择类型">
                <el-option v-for="type in healthTypes" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题"><el-input v-model="reminder.title" placeholder="例如：下次驱虫提醒" /></el-form-item>
            <el-form-item label="提醒时间"><el-date-picker v-model="reminder.dueTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择时间" /></el-form-item>
            <el-form-item class="wide">
              <el-button type="primary" :loading="savingReminder" @click="saveReminder">{{ reminder.id ? '保存修改' : '保存提醒' }}</el-button>
              <el-button @click="reminder = emptyReminder()">清空</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="panel table-panel">
          <div slot="header" class="panel-head">
            <span>提醒列表</span>
            <el-button type="text" @click="loadData">刷新</el-button>
          </div>
          <el-table :data="reminders" v-loading="loading.reminders">
            <el-table-column prop="title" label="标题" min-width="140" />
            <el-table-column prop="reminderType" label="类型" width="90" />
            <el-table-column label="宠物" width="120">
              <template slot-scope="scope">{{ petName(scope.row.petId) }}</template>
            </el-table-column>
            <el-table-column prop="dueTime" label="到期时间" width="150" />
            <el-table-column label="状态" width="90">
              <template slot-scope="scope">
                <el-tag size="mini" :type="reminderTag(scope.row)">{{ reminderLabel(scope.row) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="editReminder(scope.row)">编辑</el-button>
                <el-button size="mini" type="text" @click="completeReminder(scope.row)" v-if="scope.row.status !== 1">完成</el-button>
                <el-button size="mini" type="text" @click="delayReminder(scope.row)" v-if="scope.row.status !== 1">延期7天</el-button>
                <el-button size="mini" type="text" class="danger-text" @click="removeReminder(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {
  addHealthRecord,
  addReminder,
  deleteHealthRecords,
  deleteReminders,
  listHealthRecords,
  listPets,
  listReminders,
  updateHealthRecord,
  updateReminder
} from '@/api/pet'

export default {
  name: 'Health',
  data() {
    return {
      tab: 'records',
      selectedPetId: '',
      healthTypes: ['疫苗', '驱虫', '体检', '用药', '复诊'],
      pets: [],
      records: [],
      reminders: [],
      record: this.emptyRecord(),
      reminder: this.emptyReminder(),
      loading: { records: false, reminders: false },
      savingRecord: false,
      savingReminder: false
    }
  },
  created() {
    if (this.$route.query.tab === 'boarding' || this.$route.query.tab === 'merchantBoarding') {
      this.$router.replace('/pets')
      return
    }
    if (this.$route.query.tab === 'reminders') {
      this.tab = 'reminders'
    }
    if (this.$route.query.petId) {
      this.selectedPetId = Number(this.$route.query.petId)
    }
    this.loadPets()
    this.loadData()
  },
  methods: {
    emptyRecord() {
      return { petId: '', title: '', recordType: '', recordDate: '', nextDueDate: '', attachmentUrls: '', description: '' }
    },
    emptyReminder() {
      return { petId: '', title: '', reminderType: '', dueTime: '', status: 0 }
    },
    loadPets() {
      listPets({ pageNum: 1, pageSize: 100 }).then(res => {
        this.pets = res.rows || []
      })
    },
    loadData() {
      const params = this.selectedPetId ? { petId: this.selectedPetId, pageNum: 1, pageSize: 50 } : { pageNum: 1, pageSize: 50 }
      this.loading.records = true
      this.loading.reminders = true
      listHealthRecords(params).then(res => {
        this.records = res.rows || []
      }).finally(() => {
        this.loading.records = false
      })
      listReminders(params).then(res => {
        this.reminders = res.rows || []
      }).finally(() => {
        this.loading.reminders = false
      })
    },
    petLabel(pet) {
      if (!pet) return ''
      if (pet.boardingFlag && pet.boardingRole === 'owner') {
        return `${pet.name}（已寄养）`
      }
      if (pet.boardingFlag && pet.boardingRole === 'merchant') {
        return `${pet.name}（寄养档案）`
      }
      return pet.name
    },
    petName(id) {
      const pet = this.pets.find(item => Number(item.id) === Number(id))
      return pet ? this.petLabel(pet) : (id ? `宠物 ${id}` : '-')
    },
    saveRecord() {
      if (!this.record.title || !this.record.recordType || !this.record.recordDate) {
        this.$message.warning('请填写标题、类型和记录日期')
        return
      }
      this.savingRecord = true
      const payload = {
        ...this.record,
        petId: this.record.petId || null,
        recordDate: this.record.recordDate || null,
        nextDueDate: this.record.nextDueDate || null
      }
      const request = payload.id ? updateHealthRecord(payload) : addHealthRecord(payload)
      request.then(() => {
        this.$message.success('健康记录已保存')
        this.record = this.emptyRecord()
        this.loadData()
      }).finally(() => {
        this.savingRecord = false
      })
    },
    editRecord(row) {
      this.record = { ...this.emptyRecord(), ...row }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    removeRecord(row) {
      this.$confirm(`确认删除健康记录「${row.title}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return deleteHealthRecords(row.id)
      }).then(() => {
        this.$message.success('已删除')
        this.loadData()
      }).catch(() => {})
    },
    saveReminder() {
      if (!this.reminder.title || !this.reminder.reminderType || !this.reminder.dueTime) {
        this.$message.warning('请填写标题、类型和提醒时间')
        return
      }
      this.savingReminder = true
      const payload = {
        ...this.reminder,
        petId: this.reminder.petId || null,
        dueTime: this.reminder.dueTime || null
      }
      const request = payload.id ? updateReminder(payload) : addReminder(payload)
      request.then(() => {
        this.$message.success('提醒已保存')
        this.reminder = this.emptyReminder()
        this.loadData()
      }).finally(() => {
        this.savingReminder = false
      })
    },
    editReminder(row) {
      this.reminder = { ...this.emptyReminder(), ...row }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    completeReminder(row) {
      updateReminder({ ...row, status: 1 }).then(() => {
        this.$message.success('提醒已完成')
        this.loadData()
      })
    },
    delayReminder(row) {
      const base = row.dueTime ? new Date(row.dueTime.replace(/-/g, '/')) : new Date()
      base.setDate(base.getDate() + 7)
      updateReminder({ ...row, dueTime: this.formatDateTime(base), status: 0, noticeSent: 0 }).then(() => {
        this.$message.success('已延期7天')
        this.loadData()
      })
    },
    removeReminder(row) {
      this.$confirm(`确认删除提醒「${row.title}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return deleteReminders(row.id)
      }).then(() => {
        this.$message.success('已删除')
        this.loadData()
      }).catch(() => {})
    },
    reminderLabel(row) {
      if (row.status === 1) return '已完成'
      if (row.status === 2) return '已过期'
      if (row.dueTime && new Date(row.dueTime.replace(/-/g, '/')).getTime() < Date.now()) return '已过期'
      return '待提醒'
    },
    reminderTag(row) {
      if (row.status === 1) return 'success'
      if (row.status === 2) return 'danger'
      if (row.dueTime && new Date(row.dueTime.replace(/-/g, '/')).getTime() < Date.now()) return 'danger'
      return 'warning'
    },
    formatDateTime(date) {
      const pad = value => String(value).padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 32px 20px 52px;
}
.head-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.hint {
  margin-bottom: 16px;
}
.health-tabs {
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--pet-border);
  box-shadow: var(--pet-shadow-soft);
}
.panel {
  border-radius: 8px;
  border-color: var(--pet-border);
  margin-bottom: 16px;
}
.table-panel {
  margin-bottom: 0;
}
.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}
.panel-sub {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 16px;
}
.form-grid .wide {
  grid-column: 1 / -1;
}
.form-grid .el-select,
.form-grid .el-date-editor {
  width: 100%;
}
.danger-text {
  color: var(--pet-danger);
}
@media (max-width: 760px) {
  .page-head,
  .form-grid {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
