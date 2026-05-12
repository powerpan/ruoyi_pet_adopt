<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="page adoption-manage-page">
    <div class="page-head pet-page-title">
      <div>
        <h1>我的领养</h1>
        <p>管理待领养发布、申请处理、领养进度和后续回访。</p>
      </div>
      <el-button icon="el-icon-back" @click="$router.push('/adoptions')">领养大厅</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-click="loadAll">
      <el-tab-pane label="发布领养" name="publish">
        <section class="panel form-panel">
          <div class="panel-head">
            <h2>{{ form.id ? '编辑待领养宠物' : '发布待领养宠物' }}</h2>
            <el-button v-if="form.id" size="mini" icon="el-icon-plus" @click="form = emptyPet()">新增模式</el-button>
          </div>
          <el-form label-width="92px" class="pet-form">
            <el-form-item label="名称"><el-input v-model="form.name" maxlength="64" /></el-form-item>
            <el-form-item label="封面"><pet-upload v-model="form.coverUrl" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" /></el-form-item>
            <el-form-item label="相册"><pet-upload v-model="form.imageUrls" :limit="6" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" /></el-form-item>
            <el-form-item label="物种">
              <el-select v-model="form.species" placeholder="选择物种" class="full">
                <el-option label="猫" value="猫" />
                <el-option label="狗" value="狗" />
                <el-option label="兔" value="兔" />
                <el-option label="鸟" value="鸟" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="品种"><el-input v-model="form.breed" maxlength="64" /></el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender" clearable class="full">
                <el-option label="公" value="公" />
                <el-option label="母" value="母" />
                <el-option label="未知" value="未知" />
              </el-select>
            </el-form-item>
            <el-form-item label="月龄"><el-input-number v-model="form.ageMonths" :min="0" :max="360" class="full" /></el-form-item>
            <el-form-item label="城市"><el-input v-model="form.city" maxlength="64" /></el-form-item>
            <el-form-item label="区县"><el-input v-model="form.district" maxlength="64" /></el-form-item>
            <el-form-item label="健康"><el-input v-model="form.healthStatus" maxlength="255" /></el-form-item>
            <el-form-item label="疫苗"><el-input v-model="form.vaccineStatus" maxlength="255" /></el-form-item>
            <el-form-item label="绝育"><el-switch v-model="form.neutered" :active-value="1" :inactive-value="0" /></el-form-item>
            <el-form-item label="性格"><el-input v-model="form.personality" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
            <el-form-item label="来源"><el-input v-model="form.sourceDesc" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
            <el-form-item label="要求"><el-input v-model="form.adoptionRequirements" type="textarea" :rows="4" maxlength="1000" show-word-limit /></el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingPet" @click="savePet">{{ form.id ? '保存并重新提交审核' : '提交审核' }}</el-button>
              <el-button @click="form = emptyPet()">清空</el-button>
            </el-form-item>
          </el-form>
        </section>

        <section class="panel">
          <div class="panel-head">
            <h2>我的发布</h2>
            <el-button size="small" icon="el-icon-refresh" @click="loadPublished">刷新</el-button>
          </div>
          <el-table :data="published" v-loading="loading.published">
            <el-table-column prop="name" label="宠物" min-width="120" />
            <el-table-column prop="species" label="物种" width="90" />
            <el-table-column label="状态" width="110">
              <template slot-scope="scope"><el-tag size="mini" :type="adoptionStatusTag(scope.row.status)">{{ adoptionStatus(scope.row.status) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="applicationCount" label="申请数" width="90" />
            <el-table-column prop="auditReason" label="审核意见" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="220">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="$router.push('/adoptions/' + scope.row.id)">详情</el-button>
                <el-button size="mini" type="text" :disabled="!canEditPet(scope.row)" @click="editPet(scope.row)">编辑</el-button>
                <el-button size="mini" type="text" class="danger-text" :disabled="!canDeletePet(scope.row)" @click="removePet(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </el-tab-pane>

      <el-tab-pane label="我的申请" name="mine">
        <section class="panel">
          <div class="panel-head">
            <h2>我的申请</h2>
            <el-button size="small" icon="el-icon-refresh" @click="loadMine">刷新</el-button>
          </div>
          <el-table :data="mine" v-loading="loading.mine">
            <el-table-column prop="petName" label="宠物" min-width="130" />
            <el-table-column prop="publisherName" label="发布方" min-width="120" />
            <el-table-column label="状态" width="120">
              <template slot-scope="scope"><el-tag size="mini" :type="applicationStatusTag(scope.row.status)">{{ applicationStatus(scope.row.status) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="reviewReason" label="处理意见" min-width="180" show-overflow-tooltip />
            <el-table-column prop="visitTime" label="看宠时间" width="160" />
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="$router.push('/adoptions/' + scope.row.adoptionPetId)">查看宠物</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </el-tab-pane>

      <el-tab-pane label="收到的申请" name="received">
        <section class="panel">
          <div class="panel-head">
            <h2>收到的申请</h2>
            <el-button size="small" icon="el-icon-refresh" @click="loadReceived">刷新</el-button>
          </div>
          <el-table :data="received" v-loading="loading.received">
            <el-table-column prop="petName" label="宠物" min-width="120" />
            <el-table-column prop="applicantName" label="申请人" min-width="120" />
            <el-table-column prop="phone" label="电话" width="130" />
            <el-table-column label="状态" width="120">
              <template slot-scope="scope"><el-tag size="mini" :type="applicationStatusTag(scope.row.status)">{{ applicationStatus(scope.row.status) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="applyReason" label="申请理由" min-width="180" show-overflow-tooltip />
            <el-table-column label="操作" width="280" fixed="right">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="openApplication(scope.row)">详情/处理</el-button>
                <el-button size="mini" type="text" :disabled="scope.row.status !== 1 && scope.row.status !== 5" @click="confirmAdoption(scope.row)">确认领养</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </el-tab-pane>

      <el-tab-pane label="回访记录" name="followups">
        <section class="panel">
          <div class="panel-head">
            <h2>回访记录</h2>
            <el-button size="small" icon="el-icon-refresh" @click="loadFollowups">刷新</el-button>
          </div>
          <el-table :data="followups" v-loading="loading.followups">
            <el-table-column prop="petName" label="宠物" min-width="120" />
            <el-table-column prop="followupRound" label="轮次" width="80" />
            <el-table-column prop="planTime" label="计划时间" width="160" />
            <el-table-column label="状态" width="120">
              <template slot-scope="scope"><el-tag size="mini" :type="followupStatusTag(scope.row.status)">{{ followupStatus(scope.row.status) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="abnormalReason" label="异常说明" min-width="170" show-overflow-tooltip />
            <el-table-column label="操作" width="130">
              <template slot-scope="scope">
                <el-button size="mini" type="text" :disabled="scope.row.status === 2 || scope.row.status === 4" @click="openFollowup(scope.row)">提交回访</el-button>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="处理领养申请" :visible.sync="applicationDialog" width="720px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="宠物">{{ activeApplication.petName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ activeApplication.applicantName || activeApplication.realName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ activeApplication.phone }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ activeApplication.city }}</el-descriptions-item>
        <el-descriptions-item label="居住情况">{{ activeApplication.housingType }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验">{{ activeApplication.petExperience }}</el-descriptions-item>
        <el-descriptions-item label="申请理由">{{ activeApplication.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="承诺">{{ activeApplication.commitment }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="92px" class="dialog-form">
        <el-form-item label="处理状态">
          <el-select v-model="applicationForm.status" class="full">
            <el-option label="初审通过" :value="1" />
            <el-option label="待补充" :value="2" />
            <el-option label="拒绝" :value="3" />
            <el-option label="已预约" :value="5" />
            <el-option label="关闭" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="看宠时间"><el-date-picker v-model="applicationForm.visitTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" class="full" /></el-form-item>
        <el-form-item label="看宠地点"><el-input v-model="applicationForm.visitAddress" maxlength="255" /></el-form-item>
        <el-form-item label="处理意见"><el-input v-model="applicationForm.reviewReason" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="applicationDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingApplication" @click="saveApplicationStatus">保存</el-button>
      </div>
    </el-dialog>

    <el-dialog title="提交回访" :visible.sync="followupDialog" width="620px">
      <el-form label-width="92px">
        <el-form-item label="健康情况"><el-input v-model="followupForm.healthStatus" maxlength="255" /></el-form-item>
        <el-form-item label="生活情况"><el-input v-model="followupForm.livingStatus" type="textarea" :rows="4" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item label="回访图片"><pet-upload v-model="followupForm.imageUrls" :limit="6" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" /></el-form-item>
        <el-form-item label="异常说明"><el-input v-model="followupForm.abnormalReason" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="无异常可留空" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="followupDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingFollowup" @click="saveFollowup">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  confirmAdoptionApplication,
  createAdoption,
  deleteAdoptions,
  listAdoptionFollowups,
  listMyAdoptionApplications,
  listMyAdoptions,
  listReceivedAdoptionApplications,
  submitAdoptionFollowup,
  updateAdoption,
  updateAdoptionApplicationStatus
} from '@/api/pet'

export default {
  name: 'AdoptionManage',
  data() {
    return {
      activeTab: this.$route.query.tab || 'publish',
      form: this.emptyPet(),
      published: [],
      mine: [],
      received: [],
      followups: [],
      loading: { published: false, mine: false, received: false, followups: false },
      savingPet: false,
      savingApplication: false,
      savingFollowup: false,
      applicationDialog: false,
      followupDialog: false,
      activeApplication: {},
      activeFollowup: {},
      applicationForm: this.emptyApplicationForm(),
      followupForm: this.emptyFollowupForm()
    }
  },
  created() {
    this.loadAll()
  },
  methods: {
    emptyPet() {
      return {
        name: '', species: '', breed: '', gender: '', ageMonths: undefined, city: '', district: '',
        coverUrl: '', imageUrls: '', healthStatus: '', vaccineStatus: '', neutered: 0,
        personality: '', sourceDesc: '', adoptionRequirements: '', publisherType: 'personal'
      }
    },
    emptyApplicationForm() {
      return { id: null, status: 1, visitTime: '', visitAddress: '', reviewReason: '' }
    },
    emptyFollowupForm() {
      return { healthStatus: '', livingStatus: '', imageUrls: '', abnormalReason: '' }
    },
    loadAll() {
      this.loadPublished()
      this.loadMine()
      this.loadReceived()
      this.loadFollowups()
    },
    loadPublished() {
      this.loading.published = true
      listMyAdoptions({ pageNum: 1, pageSize: 100 }).then(res => {
        this.published = res.rows || []
      }).finally(() => {
        this.loading.published = false
      })
    },
    loadMine() {
      this.loading.mine = true
      listMyAdoptionApplications({ pageNum: 1, pageSize: 100 }).then(res => {
        this.mine = res.rows || []
      }).finally(() => {
        this.loading.mine = false
      })
    },
    loadReceived() {
      this.loading.received = true
      listReceivedAdoptionApplications({ pageNum: 1, pageSize: 100 }).then(res => {
        this.received = res.rows || []
      }).finally(() => {
        this.loading.received = false
      })
    },
    loadFollowups() {
      this.loading.followups = true
      listAdoptionFollowups({ pageNum: 1, pageSize: 100 }).then(res => {
        this.followups = res.rows || []
      }).finally(() => {
        this.loading.followups = false
      })
    },
    savePet() {
      if (!this.form.name || !this.form.species) {
        this.$message.warning('请填写宠物名称和物种')
        return
      }
      this.savingPet = true
      const action = this.form.id ? updateAdoption : createAdoption
      action(this.form).then(() => {
        this.$message.success('已提交审核')
        this.form = this.emptyPet()
        this.loadPublished()
      }).finally(() => {
        this.savingPet = false
      })
    },
    editPet(row) {
      this.form = { ...this.emptyPet(), ...row }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    removePet(row) {
      this.$confirm(`确认删除待领养宠物「${row.name || row.id}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return deleteAdoptions(row.id)
      }).then(() => {
        this.$message.success('已删除')
        this.loadPublished()
      }).catch(() => {})
    },
    canEditPet(row) {
      return [0, 1, 5, 6].indexOf(Number(row.status)) !== -1
    },
    canDeletePet(row) {
      return [0, 5, 6].indexOf(Number(row.status)) !== -1
    },
    openApplication(row) {
      this.activeApplication = row
      this.applicationForm = {
        id: row.id,
        status: row.status === 0 ? 1 : row.status,
        visitTime: row.visitTime || '',
        visitAddress: row.visitAddress || '',
        reviewReason: row.reviewReason || ''
      }
      this.applicationDialog = true
    },
    saveApplicationStatus() {
      this.savingApplication = true
      updateAdoptionApplicationStatus(this.applicationForm).then(() => {
        this.$message.success('申请状态已更新')
        this.applicationDialog = false
        this.loadReceived()
      }).finally(() => {
        this.savingApplication = false
      })
    },
    confirmAdoption(row) {
      this.$confirm(`确认将「${row.petName}」交由 ${row.applicantName || row.realName} 领养吗？`, '确认领养', { type: 'warning' }).then(() => {
        return confirmAdoptionApplication(row.id)
      }).then(() => {
        this.$message.success('领养已确认')
        this.loadAll()
      }).catch(() => {})
    },
    openFollowup(row) {
      this.activeFollowup = row
      this.followupForm = this.emptyFollowupForm()
      this.followupDialog = true
    },
    saveFollowup() {
      this.savingFollowup = true
      submitAdoptionFollowup(this.activeFollowup.id, this.followupForm).then(() => {
        this.$message.success('回访已提交')
        this.followupDialog = false
        this.loadFollowups()
      }).finally(() => {
        this.savingFollowup = false
      })
    },
    adoptionStatus(status) {
      return ({ 0: '草稿', 1: '待审核', 2: '已发布', 3: '已预约', 4: '已领养', 5: '已下架', 6: '拒绝' })[status] || '未知'
    },
    adoptionStatusTag(status) {
      return ({ 1: 'warning', 2: 'success', 3: 'warning', 4: 'info', 5: 'info', 6: 'danger' })[status] || ''
    },
    applicationStatus(status) {
      return ({ 0: '已提交', 1: '初审通过', 2: '待补充', 3: '拒绝', 4: '已撤回', 5: '已预约', 6: '已确认领养', 7: '已关闭' })[status] || '未知'
    },
    applicationStatusTag(status) {
      return ({ 0: 'warning', 1: 'success', 2: 'warning', 3: 'danger', 4: 'info', 5: 'success', 6: 'success', 7: 'info' })[status] || ''
    },
    followupStatus(status) {
      return ({ 0: '待回访', 1: '已提交', 2: '正常归档', 3: '异常待处理', 4: '已关闭' })[status] || '未知'
    },
    followupStatusTag(status) {
      return ({ 0: 'warning', 1: '', 2: 'success', 3: 'danger', 4: 'info' })[status] || ''
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 30px 20px 56px;
}
.page-head,
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.page-head {
  margin-bottom: 18px;
}
.page-head h1,
.panel-head h2 {
  margin: 0;
}
.page-head p {
  margin: 8px 0 0;
  color: var(--pet-text-muted);
}
.panel {
  margin-bottom: 18px;
  padding: 18px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow);
}
.form-panel {
  margin-top: 6px;
}
.pet-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
  margin-top: 16px;
}
.pet-form .el-form-item:nth-last-child(-n + 4),
.pet-form .el-form-item:nth-child(2),
.pet-form .el-form-item:nth-child(3) {
  grid-column: 1 / -1;
}
.full {
  width: 100%;
}
.dialog-form {
  margin-top: 18px;
}
.danger-text {
  color: #f56c6c;
}
@media (max-width: 760px) {
  .page-head,
  .panel-head {
    align-items: flex-start;
    flex-direction: column;
  }
  .pet-form {
    grid-template-columns: 1fr;
  }
  .pet-form .el-form-item {
    grid-column: 1 / -1;
  }
}
</style>
