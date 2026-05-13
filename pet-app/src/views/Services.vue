<template>
  <div class="page">
    <div class="page-head pet-page-title">
      <div>
        <h1>附近服务</h1>
        <p>按距离、服务类型和关键词筛选宠物商家服务。</p>
      </div>
      <div class="head-actions">
        <el-button v-if="loggedIn" icon="el-icon-tickets" @click="loadMyOrders">我的预约单</el-button>
        <el-button type="primary" icon="el-icon-office-building" @click="openMerchantEntry">商家入驻</el-button>
      </div>
    </div>

    <div class="filters">
      <el-input v-model="query.keyword" class="filter-keyword" placeholder="搜索服务或商家" clearable @keyup.enter.native="load" />
      <el-select v-model="query.serviceType" class="filter-select" clearable placeholder="服务类型">
        <el-option v-for="type in serviceTypes" :key="type.value" :label="type.label" :value="type.value" />
      </el-select>
      <el-input v-model="query.latitude" class="filter-coord" placeholder="纬度" />
      <el-input v-model="query.longitude" class="filter-coord" placeholder="经度" />
      <el-input-number v-model="query.radiusKm" class="filter-radius" :min="1" :max="100" controls-position="right" />
      <el-button class="filter-action" icon="el-icon-location-information" @click="useBrowserLocation">定位</el-button>
      <el-button class="filter-action" type="primary" icon="el-icon-search" :loading="loading" @click="load">筛选</el-button>
    </div>

    <section v-if="loggedIn" class="orders-panel" v-loading="loadingOrders">
      <div class="section-head">
        <div>
          <h2>我的预约单</h2>
          <p>查看商家咨询进度，进入订单对话，并在商家完成订单后评价。</p>
        </div>
        <div class="order-filters">
          <el-select v-model="orderQuery.status" clearable size="small" placeholder="全部状态" @change="loadMyOrders">
            <el-option v-for="item in requestStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button size="small" @click="loadMyOrders">刷新</el-button>
        </div>
      </div>
      <el-table :data="orders" size="small" class="orders-table">
        <el-table-column prop="serviceName" label="服务" min-width="140" />
        <el-table-column prop="merchantName" label="商家" min-width="120" />
        <el-table-column prop="petName" label="宠物" width="90" />
        <el-table-column prop="serviceTime" label="预约时间" width="150" />
        <el-table-column label="咨询进度" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="requestTag(scope.row.status)">{{ requestStatus(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="openOrderChat(scope.row)">对话</el-button>
            <el-button size="mini" type="text" :disabled="!canReview(scope.row)" @click="openReviewDialog(scope.row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loadingOrders && orders.length === 0" description="暂无预约单，先从服务卡片提交预约/咨询" />
    </section>

    <div class="services" v-loading="loading">
      <article v-for="item in services" :key="item.id" class="service">
        <div class="cover" v-if="item.coverUrl" :style="{ backgroundImage: backgroundImage(item.coverUrl) }"></div>
        <div v-else class="cover cover-empty">{{ serviceLabel(item.serviceType) }}</div>
        <div class="service-body">
          <div class="topline">
            <el-tag size="mini">{{ serviceLabel(item.serviceType) }}</el-tag>
            <span v-if="item.distanceKm !== undefined && item.distanceKm !== null">{{ item.distanceKm }} km</span>
          </div>
          <h3>{{ item.serviceName || '未命名服务' }}</h3>
          <p>{{ item.description || '暂无服务说明' }}</p>
          <div class="merchant" v-if="item.merchantName || item.merchantAddress">
            <img v-if="item.merchantLogoUrl" class="merchant-logo merchant-logo-img" :src="mediaUrl(item.merchantLogoUrl)" alt="商家 Logo">
            <div v-else class="merchant-logo merchant-logo-empty">{{ (item.merchantName || '商').slice(0, 1) }}</div>
            <div class="merchant-info">
              <strong>{{ item.merchantName || '商家' }}</strong>
              <span>{{ item.merchantAddress || '地址未填写' }}</span>
            </div>
          </div>
          <div class="meta">
            <span>评分 {{ item.reviewScore || 0 }}</span>
            <span>评价 {{ item.reviewCount || 0 }}</span>
            <span v-if="item.priceMin || item.priceMax">{{ item.priceMin || 0 }}-{{ item.priceMax || 0 }} 元</span>
          </div>
          <div class="actions">
            <el-button size="small" type="primary" @click="openRequestDialog(item)">预约/咨询</el-button>
            <el-button size="small" @click="explainReviewRule">评价规则</el-button>
            <el-button size="small" type="text" @click="openReviews(item)">查看评价</el-button>
          </div>
        </div>
      </article>
      <el-empty v-if="!loading && services.length === 0" description="暂无服务，可先提交商家入驻或在后台新增服务" />
    </div>

    <el-dialog title="预约/咨询" :visible.sync="requestDialog" width="540px">
      <el-form label-width="92px">
        <el-form-item label="服务"><span>{{ activeService.serviceName || '-' }}</span></el-form-item>
        <el-form-item label="宠物">
          <el-select v-model="requestForm.petId" clearable placeholder="选择宠物，可选" class="full">
            <el-option v-for="pet in pets" :key="pet.id" :label="pet.name" :value="pet.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="requestForm.contactPhone" /></el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="requestForm.serviceTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择时间" class="full" />
        </el-form-item>
        <el-form-item label="留言">
          <el-input v-model="requestForm.message" type="textarea" :rows="4" maxlength="300" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="requestDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingRequest" @click="submitRequest">提交咨询</el-button>
      </div>
    </el-dialog>

    <el-dialog title="服务评价" :visible.sync="reviewDialog" width="540px">
      <el-form label-width="82px">
        <el-form-item label="服务"><span>{{ activeOrder.serviceName || '-' }}</span></el-form-item>
        <el-form-item label="订单"><span>#{{ activeOrder.id }} · {{ activeOrder.merchantName || '-' }}</span></el-form-item>
        <el-form-item label="评分"><el-rate v-model="reviewForm.rating" /></el-form-item>
        <el-form-item label="内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" maxlength="300" show-word-limit />
        </el-form-item>
        <el-form-item label="图片">
          <pet-upload v-model="reviewForm.imageUrls" :limit="6" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="reviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingReview" @click="submitReview">提交评价</el-button>
      </div>
    </el-dialog>

    <el-dialog title="服务评价列表" :visible.sync="reviewsDialog" width="680px">
      <div class="review-filter">
        <el-radio-group v-model="reviewQuery.ratingType" size="small" @change="loadReviews">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="good">好评</el-radio-button>
          <el-radio-button label="middle">中评</el-radio-button>
          <el-radio-button label="bad">差评</el-radio-button>
        </el-radio-group>
        <span>{{ reviewFilterHint }}</span>
      </div>
      <div v-loading="loadingReviews">
        <article v-for="item in reviews" :key="item.id" class="review-item">
          <div class="review-head">
            <div class="review-author">
              <strong>{{ item.userName || '匿名用户' }}</strong>
              <el-tag v-if="!reviewQuery.ratingType && item.topFlag === 1" size="mini" type="warning">商家置顶</el-tag>
              <el-rate :value="item.rating" disabled />
            </div>
            <time>{{ item.createTime || '' }}</time>
          </div>
          <p>{{ item.content }}</p>
          <div class="review-images" v-if="item.imageUrls">
            <img v-for="url in splitImages(item.imageUrls)" :key="url" :src="mediaUrl(url)" alt="评价图片">
          </div>
        </article>
        <el-empty v-if="!loadingReviews && reviews.length === 0" description="暂无公开评价" />
      </div>
    </el-dialog>

    <el-dialog :title="'订单对话 #' + (activeOrder.id || '')" :visible.sync="chatDialog" width="680px" @open="loadOrderMessages">
      <div ref="chatBox" class="chat-box" v-loading="loadingMessages">
        <div v-for="item in orderMessages" :key="item.id" class="chat-message" :class="{ self: item.senderRole === 'user' }">
          <div class="message-meta">
            <span>{{ messageSender(item) }}</span>
            <time>{{ item.createTime }}</time>
          </div>
          <div class="message-bubble">{{ item.content }}</div>
        </div>
        <el-empty v-if="!loadingMessages && orderMessages.length === 0" description="还没有对话消息" />
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
  createReview,
  createServiceRequest,
  listMyServiceRequests,
  listPets,
  listServiceRequestMessages,
  listServiceReviews,
  listServices,
  sendServiceRequestMessage
} from '@/api/pet'
import { getToken } from '@/utils/auth'
import { resolveCanvasAssetUrl, resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'Services',
  data() {
    return {
      query: { radiusKm: 5, keyword: '', serviceType: '', latitude: '', longitude: '' },
      serviceTypes: [
        { label: '洗护美容', value: 'grooming' },
        { label: '寄养托管', value: 'boarding' },
        { label: '医疗问诊', value: 'medical' },
        { label: '训练行为', value: 'training' },
        { label: '用品零售', value: 'retail' }
      ],
      services: [],
      pets: [],
      reviews: [],
      orders: [],
      orderMessages: [],
      loading: false,
      loadingReviews: false,
      loadingOrders: false,
      loadingMessages: false,
      requestDialog: false,
      reviewDialog: false,
      reviewsDialog: false,
      chatDialog: false,
      submittingRequest: false,
      submittingReview: false,
      sendingMessage: false,
      activeService: {},
      activeOrder: {},
      requestForm: this.emptyRequest(),
      reviewForm: this.emptyReview(),
      orderQuery: { status: '' },
      reviewQuery: { ratingType: '' },
      messageText: '',
      requestStatusOptions: [
        { label: '待处理', value: 0 },
        { label: '已接受', value: 1 },
        { label: '已完成', value: 2 },
        { label: '已取消', value: 3 }
      ]
    }
  },
  computed: {
    loggedIn() {
      return !!getToken()
    },
    reviewFilterHint() {
      return this.reviewQuery.ratingType ? '筛选后按时间排序，商家置顶不参与排序。' : '未筛选时优先展示商家置顶好评。'
    }
  },
  created() {
    this.load()
    if (this.loggedIn) {
      this.loadMyOrders()
    }
  },
  methods: {
    emptyRequest() {
      return { petId: '', contactPhone: '', serviceTime: '', message: '' }
    },
    emptyReview() {
      return { rating: 5, content: '', imageUrls: '' }
    },
    serviceLabel(value) {
      const found = this.serviceTypes.find(type => type.value === value)
      return found ? found.label : (value || '综合服务')
    },
    ensureLogin() {
      if (getToken()) return true
      this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
      return false
    },
    load() {
      this.loading = true
      const params = { ...this.query, pageNum: 1, pageSize: 50 }
      if (!params.latitude || !params.longitude) {
        delete params.latitude
        delete params.longitude
        delete params.radiusKm
      }
      listServices(params).then(res => {
        this.services = res.rows || []
      }).finally(() => {
        this.loading = false
      })
    },
    loadPets() {
      if (!getToken()) return
      listPets({ pageNum: 1, pageSize: 100 }).then(res => {
        this.pets = res.rows || []
      })
    },
    loadMyOrders() {
      if (!this.ensureLogin()) return
      this.loadingOrders = true
      const params = {
        pageNum: 1,
        pageSize: 50,
        status: this.orderQuery.status === '' ? undefined : this.orderQuery.status
      }
      listMyServiceRequests(params).then(res => {
        this.orders = res.rows || []
      }).finally(() => {
        this.loadingOrders = false
      })
    },
    useBrowserLocation() {
      if (!navigator.geolocation) {
        this.$message.warning('当前浏览器不支持定位，请手动填写经纬度')
        return
      }
      navigator.geolocation.getCurrentPosition(position => {
        this.query.latitude = position.coords.latitude.toFixed(6)
        this.query.longitude = position.coords.longitude.toFixed(6)
        this.load()
      }, () => {
        this.$message.warning('定位失败，请手动填写经纬度')
      })
    },
    openRequestDialog(item) {
      if (!this.ensureLogin()) return
      this.activeService = item
      this.requestForm = this.emptyRequest()
      this.loadPets()
      this.requestDialog = true
    },
    submitRequest() {
      if (!this.requestForm.contactPhone) {
        this.$message.warning('请填写联系电话')
        return
      }
      this.submittingRequest = true
      createServiceRequest({
        ...this.requestForm,
        petId: this.requestForm.petId || null,
        serviceTime: this.requestForm.serviceTime || null,
        serviceId: this.activeService.id,
        merchantId: this.activeService.merchantId
      }).then(res => {
        this.$message.success('已提交咨询意向')
        this.requestDialog = false
        this.loadMyOrders()
        const requestId = res && res.data
        if (requestId) {
          this.activeOrder = {
            id: requestId,
            serviceId: this.activeService.id,
            merchantId: this.activeService.merchantId,
            serviceName: this.activeService.serviceName,
            merchantName: this.activeService.merchantName,
            status: 0
          }
          this.openOrderChat(this.activeOrder)
        }
      }).finally(() => {
        this.submittingRequest = false
      })
    },
    explainReviewRule() {
      if (!this.ensureLogin()) return
      this.$message.info('评价入口在“我的预约单”里，只有商家完成订单后才能评价')
      this.loadMyOrders()
    },
    canReview(order) {
      return order && order.status === 2 && Number(order.reviewed || 0) === 0
    },
    openReviewDialog(order) {
      if (!this.canReview(order)) {
        this.$message.warning(order && Number(order.reviewed || 0) === 1 ? '该预约单已评价' : '商家完成订单后才能评价')
        return
      }
      this.activeOrder = order
      this.reviewForm = this.emptyReview()
      this.reviewDialog = true
    },
    submitReview() {
      if (!this.reviewForm.content) {
        this.$message.warning('请填写评价内容')
        return
      }
      this.submittingReview = true
      createReview({
        ...this.reviewForm,
        requestId: this.activeOrder.id,
        serviceId: this.activeOrder.serviceId,
        merchantId: this.activeOrder.merchantId
      }).then(() => {
        this.$message.success('评价已公开展示')
        this.reviewDialog = false
        this.loadMyOrders()
        this.load()
      }).finally(() => {
        this.submittingReview = false
      })
    },
    openReviews(item) {
      this.activeService = item
      this.reviewQuery = { ratingType: '' }
      this.reviewsDialog = true
      this.loadReviews()
    },
    loadReviews() {
      if (!this.activeService.id) return
      this.loadingReviews = true
      const params = {
        pageNum: 1,
        pageSize: 50,
        ratingType: this.reviewQuery.ratingType || undefined
      }
      listServiceReviews(this.activeService.id, params).then(res => {
        this.reviews = res.rows || []
      }).finally(() => {
        this.loadingReviews = false
      })
    },
    openMerchantEntry() {
      if (!this.ensureLogin()) return
      this.$router.push({ path: '/merchant', query: { entry: '1' } })
    },
    openOrderChat(order) {
      this.activeOrder = order
      this.messageText = ''
      this.chatDialog = true
      this.loadOrderMessages()
    },
    loadOrderMessages() {
      if (!this.activeOrder.id) return
      this.loadingMessages = true
      listServiceRequestMessages(this.activeOrder.id).then(res => {
        this.orderMessages = res.data || []
        this.scrollChatToBottom()
      }).finally(() => {
        this.loadingMessages = false
      })
    },
    sendOrderMessage() {
      if (!this.messageText.trim()) {
        this.$message.warning('请输入消息内容')
        return
      }
      this.sendingMessage = true
      sendServiceRequestMessage(this.activeOrder.id, { content: this.messageText.trim() }).then(() => {
        this.messageText = ''
        this.loadOrderMessages()
      }).finally(() => {
        this.sendingMessage = false
      })
    },
    requestStatus(status) {
      return ({ 0: '待处理', 1: '已接受', 2: '已完成', 3: '已取消' })[status] || '待处理'
    },
    requestTag(status) {
      return ({ 1: 'primary', 2: 'success', 3: 'info' })[status] || 'warning'
    },
    messageSender(item) {
      if (!item) return '用户'
      if (item.senderRole === 'user') return item.senderName || '我'
      return item.senderName || this.activeOrder.merchantName || '商家'
    },
    scrollChatToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.chatBox
        if (el) {
          el.scrollTop = el.scrollHeight
        }
      })
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    backgroundImage(url) {
      return resolveCanvasBackgroundImage(url)
    },
    splitImages(value) {
      return String(value || '').split(',').map(item => item.trim()).filter(Boolean)
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
.order-filters,
.chat-input {
  display: flex;
  align-items: center;
  gap: 10px;
}
.head-actions {
  justify-content: flex-end;
  flex-wrap: wrap;
}
.filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.filter-keyword {
  flex: 2 1 220px;
}
.filter-select {
  flex: 1 1 150px;
}
.filter-coord {
  flex: 1 1 116px;
}
.filter-radius {
  flex: 0 0 126px;
}
.filter-action {
  flex: 0 0 auto;
}
.orders-panel {
  margin-bottom: 20px;
  padding: 18px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.section-head {
  justify-content: space-between;
  margin-bottom: 12px;
}
.section-head h2 {
  margin: 0 0 4px;
  color: var(--pet-text);
  font-size: 20px;
}
.section-head p {
  margin: 0;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.order-filters {
  justify-content: flex-end;
  flex-wrap: wrap;
}
.orders-table {
  width: 100%;
}
.services {
  min-height: 220px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.service {
  overflow: hidden;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--pet-border);
  box-shadow: var(--pet-shadow-soft);
  transition: transform .18s ease, box-shadow .18s ease;
}
.service:hover {
  transform: translateY(-2px);
  box-shadow: var(--pet-shadow);
}
.cover {
  height: 154px;
  background-size: cover;
  background-position: center;
}
.cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--pet-primary-soft);
  color: var(--pet-primary);
  font-weight: 700;
}
.service-body {
  padding: 16px;
}
.topline,
.meta,
.actions,
.review-head {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.topline {
  justify-content: space-between;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.service h3 {
  margin: 12px 0 8px;
  color: var(--pet-text);
  font-size: 18px;
}
.service p {
  min-height: 44px;
  margin: 0 0 12px;
  color: #606266;
  line-height: 1.6;
}
.merchant {
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr);
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  background: var(--pet-surface-soft);
  color: var(--pet-text-muted);
  font-size: 13px;
}
.merchant-logo {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background-color: #fff;
  border: 1px solid var(--pet-border);
}
.merchant-logo-img {
  display: block;
  padding: 2px;
  object-fit: contain;
}
.merchant-logo-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--pet-primary);
  background: #fff;
  font-weight: 800;
}
.merchant-info {
  min-width: 0;
  display: grid;
  gap: 4px;
}
.merchant strong {
  color: var(--pet-text);
}
.merchant span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  margin: 12px 0;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.full {
  width: 100%;
}
.review-filter {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
  color: var(--pet-text-muted);
  font-size: 12px;
}
.review-item {
  padding: 14px 0;
  border-bottom: 1px solid var(--pet-border);
}
.review-head {
  justify-content: space-between;
  color: var(--pet-text-muted);
}
.review-author {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.review-author strong {
  color: var(--pet-text);
  font-size: 14px;
}
.review-item p {
  color: #303133;
  line-height: 1.7;
}
.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.review-images img {
  width: 78px;
  height: 78px;
  border-radius: 8px;
  object-fit: cover;
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
@media (max-width: 900px) {
  .page-head {
    grid-template-columns: 1fr;
    display: grid;
  }
}
@media (max-width: 640px) {
  .filter-keyword,
  .filter-select,
  .filter-coord,
  .filter-radius,
  .filter-action {
    flex-basis: 100%;
    width: 100%;
  }
  .section-head,
  .head-actions,
  .chat-input {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
