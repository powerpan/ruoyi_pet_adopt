<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="page">
    <div class="page-head pet-page-title">
      <div>
        <h1>宠物档案</h1>
        <p>集中管理名下宠物、寄养授权确认和商家寄养档案请求。</p>
      </div>
      <div class="head-actions">
        <el-button icon="el-icon-first-aid-kit" @click="$router.push('/health')">健康记录</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="scrollToPetForm">新增宠物</el-button>
      </div>
    </div>

    <section class="section pet-list" v-loading="loading.pets">
      <div class="section-head">
        <div>
          <h2>现有宠物档案</h2>
          <p>寄养中的档案会显示特殊标注，进行中的寄养关系不能直接删除。</p>
        </div>
        <el-button size="small" icon="el-icon-refresh" @click="loadPets">刷新</el-button>
      </div>
      <div class="pet-cards" v-if="pets.length">
        <article v-for="item in pets" :key="item.id" class="pet-card">
          <img v-if="item.avatar" :src="mediaUrl(item.avatar)" alt="宠物头像" class="pet-avatar">
          <div v-else class="pet-avatar pet-avatar-empty">{{ (item.name || '?').slice(0, 1) }}</div>
          <div class="pet-info">
            <h3>{{ item.name || '未命名宠物' }}</h3>
            <p>{{ [item.species, item.breed].filter(Boolean).join(' · ') || '未填写品类' }}</p>
            <div class="pet-meta">
              <el-tag v-if="item.boardingFlag && item.boardingRole === 'owner'" size="mini" type="warning">已寄养</el-tag>
              <el-tag v-if="item.boardingFlag && item.boardingRole === 'merchant'" size="mini" type="success">寄养档案</el-tag>
              <el-tag size="mini">{{ item.healthStatus || '健康状态未填' }}</el-tag>
              <span v-if="item.weightKg">{{ item.weightKg }} kg</span>
              <span v-if="item.birthday">{{ item.birthday }}</span>
            </div>
          </div>
          <div class="card-actions">
            <el-button size="mini" @click="editPet(item)">编辑</el-button>
            <el-button size="mini" @click="$router.push({ path: '/health', query: { petId: item.id } })">健康记录</el-button>
            <el-button size="mini" type="danger" :disabled="item.boardingFlag" @click="removePet(item)">删除</el-button>
          </div>
        </article>
      </div>
      <el-empty v-else description="还没有宠物档案，先在本页底部新增一只宠物" />
    </section>

    <section class="section boarding-section">
      <div class="section-head">
        <div>
          <h2>寄养确认</h2>
          <p>用户在这里处理商家寄养授权；商家账号也在这里发起寄养档案请求并查看关系状态。</p>
        </div>
        <el-button size="small" icon="el-icon-refresh" :loading="loading.ownerBoarding || loading.boarding" @click="loadBoardingData">刷新</el-button>
      </div>

      <el-card v-if="!currentMerchant" shadow="never" class="panel table-panel">
        <div slot="header" class="panel-head">
          <span><i class="el-icon-s-check"></i>我的寄养授权</span>
          <el-select v-model="ownerBoardingQuery.status" clearable size="mini" placeholder="全部状态" @change="loadOwnerBoardings">
            <el-option v-for="item in boardingStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <el-table :data="ownerBoardings" v-loading="loading.ownerBoarding">
          <el-table-column prop="merchantName" label="商家" min-width="150" />
          <el-table-column prop="petName" label="宠物" width="120" />
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag size="mini" :type="boardingTag(scope.row.status)">{{ boardingStatus(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestTime" label="申请时间" width="160" />
          <el-table-column prop="confirmTime" label="确认时间" width="160" />
          <el-table-column prop="requestNote" label="备注" min-width="160" show-overflow-tooltip />
          <el-table-column label="操作" width="180">
            <template slot-scope="scope">
              <template v-if="scope.row.status === 0">
                <el-button size="mini" type="text" @click="approveBoarding(scope.row)">同意</el-button>
                <el-button size="mini" type="text" class="danger-text" @click="rejectBoarding(scope.row)">拒绝</el-button>
              </template>
              <el-button v-else-if="scope.row.status === 1" size="mini" type="text" class="danger-text" @click="cancelBoarding(scope.row)">取消寄养</el-button>
              <span v-else class="muted-text">-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <template v-if="currentMerchant">
        <el-card shadow="never" class="panel table-panel merchant-relation-panel">
          <div slot="header" class="panel-head">
            <span><i class="el-icon-document-copy"></i>商家寄养关系</span>
            <el-select v-model="merchantBoardingQuery.status" clearable size="mini" placeholder="全部状态" @change="loadMerchantBoardings">
              <el-option v-for="item in boardingStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
          <el-table :data="merchantBoardings" v-loading="loading.boarding">
            <el-table-column label="客户" min-width="150">
              <template slot-scope="scope">{{ userDisplay(scope.row) }}</template>
            </el-table-column>
            <el-table-column prop="petName" label="宠物" width="130" />
            <el-table-column label="状态" width="110">
              <template slot-scope="scope">
                <el-tag size="mini" :type="boardingTag(scope.row.status)">{{ boardingStatus(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="requestTime" label="申请时间" width="170" />
            <el-table-column prop="confirmTime" label="确认时间" width="170" />
            <el-table-column prop="requestNote" label="备注" min-width="220" show-overflow-tooltip />
            <el-table-column label="操作" width="120" fixed="right">
              <template slot-scope="scope">
                <el-button v-if="scope.row.status === 1" size="mini" type="text" class="danger-text" @click="cancelBoarding(scope.row)">取消寄养</el-button>
                <span v-else class="muted-text">-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <div class="merchant-secondary-grid">
        <el-card shadow="never" class="panel form-panel">
          <div slot="header" class="panel-head">
            <span><i class="el-icon-s-shop"></i>发起寄养授权</span>
            <el-tag v-if="!merchantCanBoard" size="mini" type="warning">待商家审核通过</el-tag>
          </div>
          <el-alert
            v-if="!merchantCanBoard"
            title="商家审核通过后才能发起寄养档案授权请求。"
            type="warning"
            show-icon
            :closable="false"
            class="hint"
          />
          <el-form label-width="96px" class="boarding-form">
            <el-form-item label="客户搜索">
              <div class="inline-row">
                <el-input v-model="boardingSearch.keyword" placeholder="用户名或用户ID" :disabled="!merchantCanBoard" @keyup.enter.native="searchUsers" />
                <el-button type="primary" :loading="loading.users" :disabled="!merchantCanBoard" @click="searchUsers">搜索</el-button>
              </div>
            </el-form-item>
            <el-form-item label="选择客户">
              <div class="candidate-list" v-if="boardingUsers.length">
                <button
                  v-for="user in boardingUsers"
                  :key="user.userId"
                  type="button"
                  class="candidate"
                  :class="{ active: Number(boardingForm.ownerUserId) === Number(user.userId) }"
                  @click="selectBoardingUser(user)"
                >
                  <strong>{{ user.nickName || user.userName }}</strong>
                  <span>ID {{ user.userId }} · {{ user.userName }}</span>
                </button>
              </div>
              <el-empty v-else :image-size="80" description="暂无搜索结果" />
            </el-form-item>
            <el-form-item label="准确宠物名">
              <el-input v-model="boardingForm.petName" maxlength="64" placeholder="必须与客户名下宠物名称完全一致" :disabled="!merchantCanBoard" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="boardingForm.requestNote" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="寄养时间、护理说明等" :disabled="!merchantCanBoard" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submittingBoarding" :disabled="!merchantCanBoard" @click="submitBoardingRequest">发送授权请求</el-button>
              <el-button @click="resetBoardingForm">清空</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="panel table-panel">
          <div slot="header" class="panel-head">
            <span><i class="el-icon-s-check"></i>我的寄养授权</span>
            <el-select v-model="ownerBoardingQuery.status" clearable size="mini" placeholder="全部状态" @change="loadOwnerBoardings">
              <el-option v-for="item in boardingStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </div>
          <el-table :data="ownerBoardings" v-loading="loading.ownerBoarding">
            <el-table-column prop="merchantName" label="商家" min-width="140" />
            <el-table-column prop="petName" label="宠物" width="120" />
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag size="mini" :type="boardingTag(scope.row.status)">{{ boardingStatus(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="requestTime" label="申请时间" width="160" />
            <el-table-column prop="confirmTime" label="确认时间" width="160" />
            <el-table-column prop="requestNote" label="备注" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="180">
              <template slot-scope="scope">
                <template v-if="scope.row.status === 0">
                  <el-button size="mini" type="text" @click="approveBoarding(scope.row)">同意</el-button>
                  <el-button size="mini" type="text" class="danger-text" @click="rejectBoarding(scope.row)">拒绝</el-button>
                </template>
                <el-button v-else-if="scope.row.status === 1" size="mini" type="text" class="danger-text" @click="cancelBoarding(scope.row)">取消寄养</el-button>
                <span v-else class="muted-text">-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      </template>
    </section>

    <section ref="petForm" class="section pet-form-section">
      <el-card shadow="never" class="panel form-panel">
        <div slot="header" class="panel-head">
          <span><i class="el-icon-document"></i>{{ pet.id ? '编辑宠物档案' : '新增宠物档案' }}</span>
          <el-button v-if="pet.id" size="mini" icon="el-icon-plus" @click="pet = emptyPet()">新增模式</el-button>
        </div>
        <el-form label-width="78px" class="pet-form">
          <el-form-item label="名称"><el-input v-model="pet.name" placeholder="宠物名称" /></el-form-item>
          <el-form-item label="头像">
            <pet-upload v-model="pet.avatar" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
          </el-form-item>
          <el-form-item label="物种">
            <el-select v-model="pet.species" placeholder="选择物种" class="full">
              <el-option label="猫" value="猫" />
              <el-option label="狗" value="狗" />
              <el-option label="兔" value="兔" />
              <el-option label="鸟" value="鸟" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="品种"><el-input v-model="pet.breed" placeholder="例如：英短、柯基" /></el-form-item>
          <el-form-item label="生日"><el-date-picker v-model="pet.birthday" value-format="yyyy-MM-dd" placeholder="选择日期" class="full" /></el-form-item>
          <el-form-item label="体重"><el-input v-model="pet.weightKg" placeholder="kg" /></el-form-item>
          <el-form-item label="状态"><el-input v-model="pet.healthStatus" placeholder="例如：健康、恢复中" /></el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="addingPet" @click="savePet">{{ pet.id ? '保存修改' : '保存宠物' }}</el-button>
            <el-button @click="pet = emptyPet()">清空</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </section>
  </div>
</template>

<script>
import {
  addPet,
  approveBoardingRelation,
  cancelBoardingRelation,
  createBoardingRequest,
  deletePets,
  listMerchantBoardingRelations,
  listMyMerchants,
  listOwnerBoardingRelations,
  listPets,
  rejectBoardingRelation,
  searchBoardingUsers,
  updatePet
} from '@/api/pet'
import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

export default {
  name: 'Pets',
  data() {
    return {
      pets: [],
      pet: this.emptyPet(),
      currentMerchant: null,
      boardingUsers: [],
      ownerBoardings: [],
      merchantBoardings: [],
      boardingSearch: { keyword: '' },
      boardingForm: this.emptyBoardingForm(),
      ownerBoardingQuery: { status: '' },
      merchantBoardingQuery: { status: '' },
      boardingStatusOptions: [
        { label: '待确认', value: 0 },
        { label: '寄养中', value: 1 },
        { label: '已拒绝', value: 2 },
        { label: '客户取消', value: 3 },
        { label: '商家取消', value: 4 }
      ],
      loading: { pets: false, merchants: false, users: false, boarding: false, ownerBoarding: false },
      addingPet: false,
      submittingBoarding: false
    }
  },
  computed: {
    merchantCanBoard() {
      return this.currentMerchant && this.currentMerchant.qualificationStatus === 1 && this.currentMerchant.status === 0
    }
  },
  created() {
    this.loadPets()
    this.loadMerchantStatus()
    this.loadBoardingData()
  },
  methods: {
    emptyPet() {
      return { name: '', species: '', breed: '', birthday: '', weightKg: '', avatar: '', healthStatus: '健康', status: 0 }
    },
    emptyBoardingForm() {
      return { ownerUserId: '', petName: '', requestNote: '' }
    },
    loadPets() {
      this.loading.pets = true
      listPets({ pageNum: 1, pageSize: 100 }, { noErrorMessage: true }).then(res => {
        this.pets = res.rows || []
      }).catch(err => {
        this.$message.warning((err && err.message) || '宠物档案加载失败')
      }).finally(() => {
        this.loading.pets = false
      })
    },
    loadMerchantStatus() {
      this.loading.merchants = true
      listMyMerchants({ pageNum: 1, pageSize: 1 }, { noErrorMessage: true }).then(res => {
        this.currentMerchant = (res.rows || [])[0] || null
        if (this.currentMerchant) {
          this.loadMerchantBoardings()
        }
      }).catch(() => {
        this.currentMerchant = null
      }).finally(() => {
        this.loading.merchants = false
      })
    },
    loadBoardingData() {
      this.loadOwnerBoardings()
      if (this.currentMerchant) {
        this.loadMerchantBoardings()
      }
    },
    loadOwnerBoardings() {
      this.loading.ownerBoarding = true
      const params = {
        pageNum: 1,
        pageSize: 50,
        status: this.ownerBoardingQuery.status === '' ? undefined : this.ownerBoardingQuery.status
      }
      listOwnerBoardingRelations(params).then(res => {
        this.ownerBoardings = res.rows || []
      }).finally(() => {
        this.loading.ownerBoarding = false
      })
    },
    loadMerchantBoardings() {
      if (!this.currentMerchant) return
      this.loading.boarding = true
      const params = {
        merchantId: this.currentMerchant.id,
        pageNum: 1,
        pageSize: 50,
        status: this.merchantBoardingQuery.status === '' ? undefined : this.merchantBoardingQuery.status
      }
      listMerchantBoardingRelations(params).then(res => {
        this.merchantBoardings = res.rows || []
      }).finally(() => {
        this.loading.boarding = false
      })
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    scrollToPetForm() {
      if (this.$refs.petForm) {
        this.$refs.petForm.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    },
    savePet() {
      if (!this.pet.name || !this.pet.species) {
        this.$message.warning('请填写宠物名称和物种')
        return
      }
      this.addingPet = true
      const payload = {
        ...this.pet,
        birthday: this.pet.birthday || null,
        weightKg: this.pet.weightKg === '' ? null : this.pet.weightKg
      }
      const request = payload.id ? updatePet(payload) : addPet(payload)
      request.then(() => {
        this.$message.success('宠物档案已保存')
        this.pet = this.emptyPet()
        this.loadPets()
      }).finally(() => {
        this.addingPet = false
      })
    },
    editPet(item) {
      this.pet = { ...this.emptyPet(), ...item }
      this.$nextTick(this.scrollToPetForm)
    },
    removePet(item) {
      if (item.boardingFlag) {
        this.$message.warning('寄养关系处理中，不能删除该宠物档案')
        return
      }
      this.$confirm(`确认删除宠物「${item.name || item.id}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return deletePets(item.id)
      }).then(() => {
        this.$message.success('已删除')
        this.loadPets()
      }).catch(() => {})
    },
    searchUsers() {
      if (!this.merchantCanBoard) {
        this.$message.warning('商家审核通过后才能发起寄养档案请求')
        return
      }
      if (!this.boardingSearch.keyword || this.boardingSearch.keyword.trim().length < 2) {
        this.$message.warning('请输入至少2个字符的用户名或完整用户ID')
        return
      }
      this.loading.users = true
      searchBoardingUsers({ keyword: this.boardingSearch.keyword.trim() }).then(res => {
        this.boardingUsers = res.data || []
      }).finally(() => {
        this.loading.users = false
      })
    },
    selectBoardingUser(user) {
      this.boardingForm.ownerUserId = user.userId
    },
    resetBoardingForm() {
      this.boardingSearch = { keyword: '' }
      this.boardingUsers = []
      this.boardingForm = this.emptyBoardingForm()
    },
    submitBoardingRequest() {
      if (!this.boardingForm.ownerUserId || !this.boardingForm.petName.trim()) {
        this.$message.warning('请选择客户并填写准确宠物名')
        return
      }
      const user = this.boardingUsers.find(item => Number(item.userId) === Number(this.boardingForm.ownerUserId))
      const target = user ? `${user.nickName || user.userName}（ID ${user.userId}）` : `用户 ${this.boardingForm.ownerUserId}`
      this.$confirm(`确认向 ${target} 发起宠物「${this.boardingForm.petName}」的寄养档案授权请求吗？`, '寄养授权确认', { type: 'warning' }).then(() => {
        this.submittingBoarding = true
        return createBoardingRequest({
          ownerUserId: this.boardingForm.ownerUserId,
          petName: this.boardingForm.petName.trim(),
          requestNote: this.boardingForm.requestNote
        })
      }).then(() => {
        this.$message.success('寄养授权请求已发送')
        this.resetBoardingForm()
        this.loadBoardingData()
      }).finally(() => {
        this.submittingBoarding = false
      }).catch(() => {
        this.submittingBoarding = false
      })
    },
    approveBoarding(row) {
      this.$confirm(`同意后会将宠物「${row.petName}」的档案和健康记录复制给商家，确认同意？`, '同意寄养授权', { type: 'warning' }).then(() => {
        return approveBoardingRelation(row.id)
      }).then(() => {
        this.$message.success('已同意寄养授权')
        this.refreshAfterBoardingChange()
      }).catch(() => {})
    },
    rejectBoarding(row) {
      this.$confirm(`确认拒绝商家「${row.merchantName || '-'}」的寄养档案授权请求吗？`, '拒绝寄养授权', { type: 'warning' }).then(() => {
        return rejectBoardingRelation(row.id)
      }).then(() => {
        this.$message.success('已拒绝')
        this.refreshAfterBoardingChange()
      }).catch(() => {})
    },
    cancelBoarding(row) {
      this.$confirm(`取消后会把商家侧最新档案回流到客户名下，并收回商家侧临时档案。确认取消宠物「${row.petName}」的寄养关系吗？`, '取消寄养关系', { type: 'warning' }).then(() => {
        return cancelBoardingRelation(row.id)
      }).then(() => {
        this.$message.success('寄养关系已取消，档案已回流')
        this.refreshAfterBoardingChange()
      }).catch(() => {})
    },
    refreshAfterBoardingChange() {
      this.loadPets()
      this.loadBoardingData()
      window.dispatchEvent(new Event('pet-notification-updated'))
    },
    boardingStatus(status) {
      return ({ 0: '待确认', 1: '寄养中', 2: '已拒绝', 3: '客户取消', 4: '商家取消' })[status] || '未知'
    },
    boardingTag(status) {
      return ({ 0: 'warning', 1: 'success', 2: 'danger', 3: 'info', 4: 'info' })[status] || 'info'
    },
    userDisplay(row) {
      return row.ownerNickName || row.ownerUserName || (row.ownerUserId ? `用户 ${row.ownerUserId}` : '-')
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
.head-actions,
.section-head,
.panel-head,
.inline-row,
.card-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.head-actions,
.section-head,
.panel-head {
  justify-content: space-between;
}
.head-actions,
.section-head {
  flex-wrap: wrap;
}
.section {
  margin-top: 18px;
  padding: 22px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.section-head {
  margin-bottom: 16px;
}
.section-head h2 {
  margin: 0 0 6px;
  font-size: 20px;
}
.section-head p {
  margin: 0;
  color: var(--pet-text-muted);
  line-height: 1.6;
}
.pet-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}
.pet-card {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr);
  gap: 12px;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: var(--pet-surface-soft);
}
.pet-avatar {
  width: 62px;
  height: 62px;
  object-fit: cover;
  border-radius: 8px;
}
.pet-avatar-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  color: #fff;
  font-weight: 700;
}
.pet-info h3 {
  margin: 0 0 6px;
}
.pet-info p {
  margin: 0 0 8px;
  color: var(--pet-text-muted);
}
.pet-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.card-actions {
  grid-column: 1 / -1;
  justify-content: flex-end;
  flex-wrap: wrap;
}
.panel {
  border-radius: 8px;
  border-color: var(--pet-border);
  margin-bottom: 16px;
}
.table-panel {
  margin-bottom: 0;
}
.merchant-relation-panel {
  margin-bottom: 16px;
}
.merchant-secondary-grid {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 16px;
}
::v-deep .panel .el-card__header {
  padding: 0;
  border-bottom: 1px solid var(--pet-border);
  background: linear-gradient(180deg, #ffffff 0%, #f8fbfa 100%);
}
.panel-head {
  min-height: 56px;
  padding: 0 18px;
}
.panel-head span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--pet-text);
  font-size: 16px;
  font-weight: 800;
}
.panel-head span i {
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: var(--pet-primary-soft);
  color: var(--pet-primary);
}
.hint {
  margin-bottom: 16px;
}
.boarding-form {
  display: block;
}
.inline-row {
  align-items: stretch;
}
.inline-row .el-input {
  flex: 1;
}
.candidate-list {
  display: grid;
  gap: 8px;
}
.candidate {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  color: var(--pet-text);
  cursor: pointer;
  text-align: left;
  transition: border-color .18s ease, background .18s ease, box-shadow .18s ease;
}
.candidate strong,
.candidate span {
  display: block;
}
.candidate strong {
  font-size: 14px;
}
.candidate span {
  margin-top: 3px;
  color: var(--pet-text-muted);
  font-size: 12px;
}
.candidate.active {
  border-color: var(--pet-primary);
  background: var(--pet-primary-soft);
  box-shadow: 0 8px 18px rgba(35, 130, 118, .12);
}
.pet-form {
  max-width: 640px;
}
.full {
  width: 100%;
}
.danger-text {
  color: var(--pet-danger);
}
.muted-text {
  color: var(--pet-text-muted);
  font-size: 12px;
}
@media (max-width: 760px) {
  .merchant-secondary-grid {
    grid-template-columns: 1fr;
  }
  .inline-row {
    flex-direction: column;
  }
}
</style>
