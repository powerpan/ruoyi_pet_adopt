<template>
  <div class="page">
    <div class="page-head pet-page-title">
      <div>
        <h1>商家工作台</h1>
        <p>管理入驻资料、资质材料、服务项目和预约订单。</p>
      </div>
      <div class="head-actions">
        <el-button icon="el-icon-refresh" :loading="loading.merchants" @click="loadMerchants">刷新</el-button>
        <el-button v-if="!currentMerchant" type="primary" icon="el-icon-plus" @click="openMerchantCreate">商家入驻</el-button>
        <el-button v-else type="primary" icon="el-icon-edit" @click="openMerchantEdit">编辑资料</el-button>
      </div>
    </div>

    <section v-if="!loading.merchants && !currentMerchant" class="empty-workbench">
      <div>
        <h2>还没有商家主体</h2>
        <p>一个账号只绑定一个商家。提交商家资料和资质后，这里会成为独立的服务经营入口。</p>
      </div>
      <el-button type="primary" icon="el-icon-office-building" @click="openMerchantCreate">提交商家入驻</el-button>
    </section>

    <section v-else class="merchant-workbench" v-loading="loading.merchants">
      <article class="merchant-card">
        <img v-if="currentMerchant.logoUrl" class="merchant-logo merchant-logo-img" :src="mediaUrl(currentMerchant.logoUrl)" alt="商家 Logo">
        <div v-else class="merchant-logo merchant-logo-empty">{{ (currentMerchant.name || '商').slice(0, 1) }}</div>
        <div class="merchant-main">
          <div class="merchant-title">
            <h2>{{ currentMerchant.name || '未命名商家' }}</h2>
            <el-tag size="small" :type="merchantTag(currentMerchant.qualificationStatus)">{{ merchantStatus(currentMerchant.qualificationStatus) }}</el-tag>
          </div>
          <p>{{ currentMerchant.description || '暂无商家简介' }}</p>
          <div class="merchant-meta">
            <span><i class="el-icon-phone"></i>{{ currentMerchant.phone || '电话未填' }}</span>
            <span><i class="el-icon-location-outline"></i>{{ currentMerchant.address || '地址未填' }}</span>
            <span><i class="el-icon-star-off"></i>{{ currentMerchant.score || 0 }} / {{ currentMerchant.reviewCount || 0 }} 条评价</span>
          </div>
          <el-alert
            v-if="currentMerchant.qualificationStatus !== 1"
            class="merchant-status-tip"
            :title="currentMerchant.qualificationStatus === 0 ? '商家资料正在审核，通过后才能发布服务和处理订单。' : '商家认证未通过，修改资料和资质后会重新进入审核。'"
            :type="currentMerchant.qualificationStatus === 0 ? 'warning' : 'error'"
            :closable="false"
          />
          <div class="merchant-actions">
            <el-button size="small" @click="openMerchantEdit">编辑资料</el-button>
            <el-button size="small" @click="openQualification(currentMerchant)">资质材料</el-button>
            <el-button size="small" :disabled="!merchantCanServe(currentMerchant)" @click="openServiceManage(currentMerchant)">服务管理</el-button>
            <el-button size="small" type="primary" :disabled="!merchantCanServe(currentMerchant)" @click="openRequestManage(currentMerchant)">预约订单</el-button>
            <el-button size="small" :disabled="!merchantCanServe(currentMerchant)" @click="openReviewManage(currentMerchant)">收到评价</el-button>
          </div>
        </div>
      </article>
    </section>

    <el-dialog :title="merchantForm.id ? '编辑商家资料' : '商家入驻'" :visible.sync="merchantDialog" width="660px">
      <el-form label-width="92px">
        <el-form-item label="商家名称"><el-input v-model="merchantForm.name" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="merchantForm.contactName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="merchantForm.phone" /></el-form-item>
        <el-form-item label="城市/区域">
          <div class="inline-row">
            <el-input v-model="merchantForm.city" placeholder="城市" />
            <el-input v-model="merchantForm.district" placeholder="区域" />
          </div>
        </el-form-item>
        <el-form-item label="地址"><el-input v-model="merchantForm.address" /></el-form-item>
        <el-form-item label="经纬度">
          <div class="inline-row">
            <el-input v-model="merchantForm.longitude" placeholder="经度" />
            <el-input v-model="merchantForm.latitude" placeholder="纬度" />
          </div>
        </el-form-item>
        <el-form-item label="商家 Logo">
          <pet-upload v-model="merchantForm.logoUrl" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
        </el-form-item>
        <el-form-item label="简介"><el-input v-model="merchantForm.description" type="textarea" :rows="4" maxlength="500" show-word-limit /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="merchantDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingMerchant" @click="submitMerchant">{{ merchantForm.id ? '保存资料' : '提交入驻' }}</el-button>
      </div>
    </el-dialog>

    <el-dialog title="资质材料" :visible.sync="qualificationDialog" width="680px">
      <el-alert title="营业执照、门店照片、从业证明等可上传为图片或 PDF。提交后等待后台审核。" type="info" :closable="false" class="dialog-tip" />
      <el-form label-width="92px">
        <el-form-item label="材料类型">
          <el-input v-model="qualificationForm.materialType" placeholder="例如：营业执照、门店照片" />
        </el-form-item>
        <el-form-item label="材料文件">
          <pet-upload v-model="qualificationForm.materialUrl" :limit="1" :file-type="['jpg','jpeg','png','gif','pdf']" :file-size="20" />
        </el-form-item>
      </el-form>
      <el-table :data="qualifications" size="small" class="qualification-table">
        <el-table-column prop="materialType" label="类型" width="140" />
        <el-table-column prop="materialUrl" label="文件" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template slot-scope="scope">
            <el-tag size="mini" :type="merchantTag(scope.row.auditStatus)">{{ merchantStatus(scope.row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer">
        <el-button @click="qualificationDialog = false">关闭</el-button>
        <el-button type="primary" :loading="submittingQualification" @click="submitQualification">提交材料</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="(activeMerchant.name || '商家') + '服务管理'" :visible.sync="serviceDialog" width="920px">
      <el-form label-width="92px" class="service-form">
        <el-form-item label="服务名称"><el-input v-model="serviceForm.serviceName" placeholder="例如：猫咪洗护套餐" /></el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="serviceForm.serviceType" placeholder="选择类型" class="full">
            <el-option v-for="type in serviceTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <div class="inline-row">
            <el-input v-model="serviceForm.priceMin" placeholder="最低价" />
            <el-input v-model="serviceForm.priceMax" placeholder="最高价" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="serviceForm.status">
            <el-radio :label="0">上架</el-radio>
            <el-radio :label="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面">
          <pet-upload v-model="serviceForm.coverUrl" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
        </el-form-item>
        <el-form-item label="说明" class="wide">
          <el-input v-model="serviceForm.description" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item class="wide">
          <el-button type="primary" :loading="savingService" @click="saveMerchantService">{{ serviceForm.id ? '保存修改' : '发布服务' }}</el-button>
          <el-button @click="serviceForm = emptyService()">清空</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="merchantServices" size="small" v-loading="loading.services" class="merchant-table">
        <el-table-column prop="serviceName" label="服务" min-width="150" />
        <el-table-column label="类型" width="100">
          <template slot-scope="scope">{{ serviceLabel(scope.row.serviceType) }}</template>
        </el-table-column>
        <el-table-column label="价格" width="120">
          <template slot-scope="scope">{{ scope.row.priceMin || 0 }}-{{ scope.row.priceMax || 0 }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.status === 0 ? 'success' : 'info'">{{ scope.row.status === 0 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评价" width="110">
          <template slot-scope="scope">{{ scope.row.reviewScore || 0 }} / {{ scope.row.reviewCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editService(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" class="danger-text" @click="removeService(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="(activeMerchant.name || '商家') + '预约订单'" :visible.sync="requestDialog" width="980px">
      <div class="dialog-toolbar">
        <el-select v-model="requestQuery.status" clearable size="small" placeholder="全部状态" @change="loadMerchantRequests">
          <el-option v-for="item in requestStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button size="small" @click="loadMerchantRequests">刷新</el-button>
      </div>
      <el-table :data="serviceRequests" size="small" v-loading="loading.requests">
        <el-table-column prop="serviceName" label="服务" min-width="130" />
        <el-table-column prop="userName" label="预约用户" width="110" />
        <el-table-column prop="petName" label="宠物" width="90" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="serviceTime" label="预约时间" width="150" />
        <el-table-column prop="message" label="留言" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="96">
          <template slot-scope="scope">
            <el-tag size="mini" :type="requestTag(scope.row.status)">{{ requestStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="openOrderChat(scope.row)">对话</el-button>
            <el-button size="mini" type="text" :disabled="scope.row.status !== 0" @click="setRequestStatus(scope.row, 1)">接受预约</el-button>
            <el-button size="mini" type="text" :disabled="scope.row.status !== 1" @click="setRequestStatus(scope.row, 2)">完成</el-button>
            <el-button size="mini" type="text" :disabled="scope.row.status === 2 || scope.row.status === 3" @click="setRequestStatus(scope.row, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :title="(activeMerchant.name || '商家') + '收到的评价'" :visible.sync="reviewDialog" width="980px">
      <div class="dialog-toolbar review-toolbar">
        <el-radio-group v-model="reviewQuery.ratingType" size="small" @change="loadMerchantReviews">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="good">好评</el-radio-button>
          <el-radio-button label="middle">中评</el-radio-button>
          <el-radio-button label="bad">差评</el-radio-button>
        </el-radio-group>
        <el-select v-model="reviewQuery.hideStatus" clearable size="small" placeholder="全部屏蔽状态" @change="loadMerchantReviews">
          <el-option label="未申请" :value="0" />
          <el-option label="申请审核中" :value="1" />
          <el-option label="已屏蔽" :value="2" />
          <el-option label="已驳回" :value="3" />
        </el-select>
        <el-button size="small" @click="loadMerchantReviews">刷新</el-button>
      </div>
      <el-table :data="merchantReviews" size="small" v-loading="loading.reviews" empty-text="暂无收到的评价">
        <el-table-column prop="serviceName" label="服务" min-width="140" />
        <el-table-column prop="userName" label="评价用户" width="110" />
        <el-table-column label="评分" width="120">
          <template slot-scope="scope"><el-rate :value="scope.row.rating" disabled /></template>
        </el-table-column>
        <el-table-column label="展示" width="90">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.topFlag === 1" size="mini" type="warning">置顶</el-tag>
            <span v-else class="muted-text">普通</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="操作" width="170">
          <template slot-scope="scope">
            <el-button size="mini" type="text" :disabled="!canTopReview(scope.row)" @click="toggleTopReview(scope.row)">{{ scope.row.topFlag === 1 ? '取消置顶' : '置顶' }}</el-button>
            <el-button size="mini" type="text" :disabled="!canRequestHide(scope.row)" @click="openHideRequest(scope.row)">申请屏蔽</el-button>
          </template>
        </el-table-column>
        <el-table-column label="屏蔽状态" width="120">
          <template slot-scope="scope">
            <el-tag size="mini" :type="hideStatusTag(scope.row.hideStatus)">{{ hideStatusLabel(scope.row.hideStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hideReason" label="申请理由" min-width="180" show-overflow-tooltip />
        <el-table-column prop="hideAuditReason" label="审核意见" min-width="180" show-overflow-tooltip />
      </el-table>
    </el-dialog>

    <el-dialog title="申请屏蔽评价" :visible.sync="hideDialog" width="560px">
      <el-alert title="评价默认公开。只有涉及恶意攻击、虚假内容、隐私泄露等情况建议申请屏蔽，管理员会按理由审核。" type="warning" :closable="false" class="dialog-tip" />
      <div class="review-content">
        <strong>{{ activeReview.userName || '评价用户' }}</strong>
        <el-rate :value="activeReview.rating" disabled />
        <p>{{ activeReview.content || '暂无评价内容' }}</p>
      </div>
      <el-form label-width="92px">
        <el-form-item label="屏蔽理由">
          <el-input v-model="hideForm.hideReason" type="textarea" :rows="5" maxlength="500" show-word-limit placeholder="请写清楚申请屏蔽的具体原因" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="hideDialog = false">取消</el-button>
        <el-button type="primary" :loading="requestingHide" @click="submitHideRequest">提交申请</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="'订单对话 #' + (activeRequest.id || '')" :visible.sync="chatDialog" width="680px" @open="loadOrderMessages">
      <div ref="chatBox" class="chat-box" v-loading="loading.messages">
        <div v-for="item in orderMessages" :key="item.id" class="chat-message" :class="{ self: item.senderRole === 'merchant' }">
          <div class="message-meta">
            <span>{{ messageSender(item) }}</span>
            <time>{{ item.createTime }}</time>
          </div>
          <div class="message-bubble">{{ item.content }}</div>
        </div>
        <el-empty v-if="!loading.messages && orderMessages.length === 0" description="还没有对话消息" />
      </div>
      <div class="chat-input">
        <el-input v-model="messageText" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="输入订单沟通内容" />
        <el-button type="primary" :loading="sendingMessage" @click="sendOrderMessage">发送</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  createMerchant,
  createMerchantQualification,
  createMerchantService,
  deleteMerchantServices,
  listMerchantQualifications,
  listMerchantReviews,
  listMerchantServiceRequests,
  listMyMerchantServices,
  listMyMerchants,
  listServiceRequestMessages,
  requestHideReview,
  sendServiceRequestMessage,
  topMerchantReview,
  updateMerchant,
  updateMerchantService,
  updateMerchantServiceRequestStatus
} from '@/api/pet'
import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

export default {
  name: 'Merchant',
  data() {
    return {
      merchants: [],
      merchantServices: [],
      serviceRequests: [],
      merchantReviews: [],
      qualifications: [],
      orderMessages: [],
      activeMerchant: {},
      activeRequest: {},
      activeReview: {},
      merchantForm: this.emptyMerchant(),
      qualificationForm: this.emptyQualification(),
      serviceForm: this.emptyService(),
      requestQuery: { status: '' },
      reviewQuery: { hideStatus: '', ratingType: '' },
      hideForm: { hideReason: '' },
      messageText: '',
      merchantDialog: false,
      qualificationDialog: false,
      serviceDialog: false,
      requestDialog: false,
      reviewDialog: false,
      hideDialog: false,
      chatDialog: false,
      loading: { merchants: false, services: false, requests: false, reviews: false, messages: false },
      submittingMerchant: false,
      submittingQualification: false,
      savingService: false,
      requestingHide: false,
      sendingMessage: false,
      serviceTypes: [
        { label: '洗护美容', value: 'grooming' },
        { label: '寄养托管', value: 'boarding' },
        { label: '医疗问诊', value: 'medical' },
        { label: '训练行为', value: 'training' },
        { label: '用品零售', value: 'retail' }
      ],
      requestStatusOptions: [
        { label: '待处理', value: 0 },
        { label: '已接受', value: 1 },
        { label: '已完成', value: 2 },
        { label: '已取消', value: 3 }
      ]
    }
  },
  created() {
    this.loadMerchants()
  },
  computed: {
    currentMerchant() {
      return this.merchants.length > 0 ? this.merchants[0] : null
    }
  },
  methods: {
    emptyMerchant() {
      return { name: '', contactName: '', phone: '', city: '', district: '', address: '', longitude: '', latitude: '', logoUrl: '', description: '' }
    },
    emptyQualification() {
      return { materialType: '', materialUrl: '' }
    },
    emptyService() {
      return { serviceName: '', serviceType: '', priceMin: '', priceMax: '', coverUrl: '', description: '', status: 0 }
    },
    loadMerchants() {
      this.loading.merchants = true
      listMyMerchants({ pageNum: 1, pageSize: 50 }).then(res => {
        this.merchants = res.rows || []
        this.activeMerchant = this.currentMerchant || {}
        if (this.$route.query.entry && !this.currentMerchant) {
          this.openMerchantCreate()
        }
        window.dispatchEvent(new Event('pet-merchant-updated'))
      }).finally(() => {
        this.loading.merchants = false
      })
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    openMerchantCreate() {
      if (this.currentMerchant) {
        this.$message.info('一个账号只绑定一个商家，可在这里编辑当前商家资料')
        this.openMerchantEdit()
        return
      }
      this.merchantForm = this.emptyMerchant()
      this.merchantDialog = true
    },
    openMerchantEdit() {
      if (!this.currentMerchant) return
      this.merchantForm = { ...this.emptyMerchant(), ...this.currentMerchant }
      this.merchantDialog = true
    },
    submitMerchant() {
      if (!this.merchantForm.name || !this.merchantForm.phone) {
        this.$message.warning('请填写商家名称和电话')
        return
      }
      this.submittingMerchant = true
      const payload = {
        ...this.merchantForm,
        longitude: this.merchantForm.longitude === '' ? null : this.merchantForm.longitude,
        latitude: this.merchantForm.latitude === '' ? null : this.merchantForm.latitude
      }
      const request = payload.id ? updateMerchant(payload) : createMerchant(payload)
      request.then(res => {
        this.$message.success(payload.id ? '商家资料已保存' : '入驻申请已提交，可继续上传资质材料')
        this.merchantDialog = false
        const id = payload.id || (res && res.data)
        this.merchantForm = this.emptyMerchant()
        this.loadMerchants()
        if (!payload.id && id) {
          this.openQualification({ id, name: payload.name, qualificationStatus: 0 })
        }
      }).finally(() => {
        this.submittingMerchant = false
      })
    },
    openQualification(row) {
      this.activeMerchant = row
      this.qualificationForm = this.emptyQualification()
      this.qualificationDialog = true
      listMerchantQualifications(row.id, { pageNum: 1, pageSize: 50 }).then(res => {
        this.qualifications = res.rows || []
      })
    },
    submitQualification() {
      if (!this.qualificationForm.materialType || !this.qualificationForm.materialUrl) {
        this.$message.warning('请填写材料类型并上传文件')
        return
      }
      this.submittingQualification = true
      createMerchantQualification(this.activeMerchant.id, this.qualificationForm).then(() => {
        this.$message.success('资质材料已提交')
        this.openQualification(this.activeMerchant)
      }).finally(() => {
        this.submittingQualification = false
      })
    },
    merchantCanServe(row) {
      return row && row.qualificationStatus === 1 && row.status === 0
    },
    openServiceManage(row) {
      if (!this.merchantCanServe(row)) {
        this.$message.warning('商家审核通过后才能发布服务')
        return
      }
      this.activeMerchant = row
      this.serviceForm = this.emptyService()
      this.serviceDialog = true
      this.loadMerchantServices()
    },
    loadMerchantServices() {
      if (!this.activeMerchant.id) return
      this.loading.services = true
      listMyMerchantServices(this.activeMerchant.id, { pageNum: 1, pageSize: 50 }).then(res => {
        this.merchantServices = res.rows || []
      }).finally(() => {
        this.loading.services = false
      })
    },
    saveMerchantService() {
      if (!this.serviceForm.serviceName || !this.serviceForm.serviceType) {
        this.$message.warning('请填写服务名称和类型')
        return
      }
      this.savingService = true
      const payload = {
        ...this.serviceForm,
        priceMin: this.serviceForm.priceMin === '' ? null : this.serviceForm.priceMin,
        priceMax: this.serviceForm.priceMax === '' ? null : this.serviceForm.priceMax
      }
      const request = payload.id ? updateMerchantService(payload) : createMerchantService(this.activeMerchant.id, payload)
      request.then(() => {
        this.$message.success(payload.id ? '服务已更新' : '服务已发布')
        this.serviceForm = this.emptyService()
        this.loadMerchantServices()
      }).finally(() => {
        this.savingService = false
      })
    },
    editService(row) {
      this.serviceForm = { ...this.emptyService(), ...row }
    },
    removeService(row) {
      this.$confirm(`确认删除服务「${row.serviceName}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return deleteMerchantServices(row.id)
      }).then(() => {
        this.$message.success('服务已删除')
        this.loadMerchantServices()
      }).catch(() => {})
    },
    openRequestManage(row) {
      if (!this.merchantCanServe(row)) {
        this.$message.warning('商家审核通过后才能处理预约')
        return
      }
      this.activeMerchant = row
      this.requestQuery = { status: '' }
      this.requestDialog = true
      this.loadMerchantRequests()
    },
    loadMerchantRequests() {
      if (!this.activeMerchant.id) return
      this.loading.requests = true
      const params = {
        merchantId: this.activeMerchant.id,
        pageNum: 1,
        pageSize: 50,
        status: this.requestQuery.status === '' ? undefined : this.requestQuery.status
      }
      listMerchantServiceRequests(params).then(res => {
        this.serviceRequests = res.rows || []
      }).finally(() => {
        this.loading.requests = false
      })
    },
    openReviewManage(row) {
      this.activeMerchant = row
      this.reviewQuery = { hideStatus: '', ratingType: '' }
      this.reviewDialog = true
      this.loadMerchantReviews()
    },
    loadMerchantReviews() {
      if (!this.activeMerchant.id) return
      this.loading.reviews = true
      const params = {
        merchantId: this.activeMerchant.id,
        pageNum: 1,
        pageSize: 50,
        hideStatus: this.reviewQuery.hideStatus === '' ? undefined : this.reviewQuery.hideStatus,
        ratingType: this.reviewQuery.ratingType || undefined
      }
      listMerchantReviews(params).then(res => {
        this.merchantReviews = res.rows || []
      }).finally(() => {
        this.loading.reviews = false
      })
    },
    canRequestHide(row) {
      return row && Number(row.status) === 1 && ![1, 2].includes(Number(row.hideStatus || 0))
    },
    canTopReview(row) {
      return row && Number(row.status) === 1 && ![1, 2].includes(Number(row.hideStatus || 0)) && Number(row.rating || 0) >= 4
    },
    toggleTopReview(row) {
      if (!this.canTopReview(row)) {
        this.$message.warning('只有公开好评可以置顶')
        return
      }
      const topFlag = row.topFlag === 1 ? 0 : 1
      topMerchantReview(row.id, { topFlag }).then(() => {
        this.$message.success(topFlag === 1 ? '评价已置顶' : '已取消置顶')
        this.loadMerchantReviews()
      })
    },
    openHideRequest(row) {
      this.activeReview = row
      this.hideForm = { hideReason: '' }
      this.hideDialog = true
    },
    submitHideRequest() {
      if (!this.hideForm.hideReason.trim()) {
        this.$message.warning('请填写申请屏蔽理由')
        return
      }
      this.requestingHide = true
      requestHideReview(this.activeReview.id, { hideReason: this.hideForm.hideReason.trim() }).then(() => {
        this.$message.success('屏蔽申请已提交，等待管理员审核')
        this.hideDialog = false
        this.loadMerchantReviews()
      }).finally(() => {
        this.requestingHide = false
      })
    },
    setRequestStatus(row, status) {
      const statusText = this.requestStatus(status)
      this.$confirm(`确认将预约单更新为「${statusText}」吗？`, '更新订单进度', { type: 'warning' }).then(() => {
        return updateMerchantServiceRequestStatus({ id: row.id, status })
      }).then(() => {
        this.$message.success('订单进度已更新')
        this.loadMerchantRequests()
      }).catch(() => {})
    },
    openOrderChat(row) {
      this.activeRequest = row
      this.messageText = ''
      this.chatDialog = true
      this.loadOrderMessages()
    },
    loadOrderMessages() {
      if (!this.activeRequest.id) return
      this.loading.messages = true
      listServiceRequestMessages(this.activeRequest.id).then(res => {
        this.orderMessages = res.data || []
        this.scrollChatToBottom()
      }).finally(() => {
        this.loading.messages = false
      })
    },
    sendOrderMessage() {
      if (!this.messageText.trim()) {
        this.$message.warning('请输入消息内容')
        return
      }
      this.sendingMessage = true
      sendServiceRequestMessage(this.activeRequest.id, { content: this.messageText.trim() }).then(() => {
        this.messageText = ''
        this.loadOrderMessages()
      }).finally(() => {
        this.sendingMessage = false
      })
    },
    serviceLabel(value) {
      const item = this.serviceTypes.find(type => type.value === value)
      return item ? item.label : (value || '综合服务')
    },
    requestStatus(status) {
      return ({ 0: '待处理', 1: '已接受', 2: '已完成', 3: '已取消' })[status] || '待处理'
    },
    requestTag(status) {
      return ({ 1: 'primary', 2: 'success', 3: 'info' })[status] || 'warning'
    },
    hideStatusLabel(status) {
      return ({ 0: '未申请', 1: '申请审核中', 2: '已屏蔽', 3: '已驳回' })[status] || '未申请'
    },
    hideStatusTag(status) {
      return ({ 1: 'warning', 2: 'info', 3: 'danger' })[status] || ''
    },
    messageSender(item) {
      if (!item) return '用户'
      if (item.senderRole === 'merchant') return item.senderName || this.activeMerchant.name || '商家'
      return item.senderName || this.activeRequest.userName || '用户'
    },
    scrollChatToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.chatBox
        if (el) {
          el.scrollTop = el.scrollHeight
        }
      })
    },
    merchantStatus(status) {
      return ({ 0: '待审核', 1: '已通过', 2: '未通过', 3: '未通过' })[status] || '待审核'
    },
    merchantTag(status) {
      return ({ 1: 'success', 2: 'danger', 3: 'danger' })[status] || 'warning'
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
.inline-row,
.merchant-title,
.merchant-meta,
.merchant-actions,
.dialog-toolbar,
.chat-input {
  display: flex;
  align-items: center;
  gap: 10px;
}
.head-actions {
  justify-content: flex-end;
  flex-wrap: wrap;
}
.empty-workbench,
.merchant-card {
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.empty-workbench {
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 28px;
}
.empty-workbench h2 {
  margin: 0 0 10px;
  color: var(--pet-text);
}
.empty-workbench p {
  margin: 0;
  color: var(--pet-text-muted);
  line-height: 1.8;
}
.merchant-workbench {
  display: grid;
  gap: 16px;
}
.merchant-card {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 18px;
  padding: 18px;
}
.merchant-logo {
  width: 96px;
  height: 96px;
  border-radius: 8px;
  background-color: #fff;
  border: 1px solid var(--pet-border);
}
.merchant-logo-img {
  display: block;
  padding: 4px;
  object-fit: contain;
}
.merchant-logo-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  font-size: 28px;
  font-weight: 800;
}
.merchant-title {
  justify-content: space-between;
}
.merchant-title h2 {
  margin: 0;
  color: var(--pet-text);
  font-size: 20px;
}
.merchant-main p {
  margin: 10px 0;
  color: var(--pet-text-muted);
  line-height: 1.7;
}
.merchant-meta {
  flex-wrap: wrap;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.merchant-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.merchant-status-tip {
  margin-top: 12px;
}
.merchant-actions {
  flex-wrap: wrap;
  margin-top: 14px;
}
.full {
  width: 100%;
}
.inline-row {
  align-items: stretch;
}
.dialog-tip,
.qualification-table,
.merchant-table {
  margin-bottom: 14px;
}
.service-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.service-form .wide {
  grid-column: 1 / -1;
}
.dialog-toolbar {
  justify-content: flex-end;
  margin-bottom: 12px;
}
.review-toolbar {
  justify-content: space-between;
  flex-wrap: wrap;
}
.muted-text {
  color: var(--pet-text-muted);
  font-size: 12px;
}
.review-content {
  display: grid;
  gap: 8px;
  margin: 0 0 14px;
  padding: 12px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: var(--pet-surface-soft);
}
.review-content strong {
  color: var(--pet-text);
}
.review-content p {
  margin: 0;
  color: var(--pet-text);
  line-height: 1.7;
  white-space: pre-wrap;
}
.danger-text {
  color: var(--pet-danger);
}
.chat-box {
  max-height: 420px;
  min-height: 220px;
  overflow-y: auto;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: var(--pet-surface-soft);
}
.chat-message {
  max-width: 82%;
  margin-bottom: 12px;
}
.chat-message.self {
  margin-left: auto;
}
.message-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
  color: var(--pet-text-muted);
  font-size: 12px;
}
.chat-message.self .message-meta {
  justify-content: flex-end;
}
.message-bubble {
  padding: 10px 12px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--pet-border);
  color: var(--pet-text);
  line-height: 1.7;
  white-space: pre-wrap;
}
.chat-message.self .message-bubble {
  color: #fff;
  border-color: var(--pet-primary);
  background: var(--pet-primary);
}
.chat-input {
  align-items: flex-end;
  margin-top: 12px;
}
.chat-input .el-textarea {
  flex: 1;
}
@media (max-width: 760px) {
  .page-head,
  .empty-workbench,
  .merchant-card,
  .service-form {
    grid-template-columns: 1fr;
  }
  .empty-workbench {
    align-items: flex-start;
    flex-direction: column;
  }
  .merchant-logo {
    width: 72px;
    height: 72px;
  }
}
</style>
