<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="page adoption-detail-page" v-loading="loading">
    <div class="detail-head">
      <el-button icon="el-icon-back" @click="$router.push('/adoptions')">返回领养大厅</el-button>
      <el-button icon="el-icon-s-management" @click="$router.push('/adoption-manage')">我的领养</el-button>
    </div>

    <section v-if="pet.id" class="detail-main">
      <div class="gallery">
        <div v-if="pet.coverUrl" class="cover" :style="{ backgroundImage: backgroundImage(pet.coverUrl) }"></div>
        <div v-else class="cover empty-cover">{{ (pet.name || '?').slice(0, 1) }}</div>
        <div class="thumbs" v-if="imageList.length">
          <img v-for="url in imageList" :key="url" :src="mediaUrl(url)" alt="领养宠物图片">
        </div>
      </div>
      <div class="info">
        <div class="title-row">
          <h1>{{ pet.name || '未命名宠物' }}</h1>
          <el-tag :type="statusTag(pet.status)">{{ statusText(pet.status) }}</el-tag>
        </div>
        <div class="facts">
          <span>{{ pet.species || '未填写物种' }}</span>
          <span>{{ pet.breed || '未填写品种' }}</span>
          <span>{{ pet.gender || '未填写性别' }}</span>
          <span>{{ ageText(pet.ageMonths) || '年龄未知' }}</span>
          <span>{{ [pet.city, pet.district].filter(Boolean).join(' ') || '地区未填写' }}</span>
        </div>
        <el-descriptions :column="1" border size="small" class="desc-table">
          <el-descriptions-item label="健康情况">{{ pet.healthStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="疫苗情况">{{ pet.vaccineStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="绝育情况">{{ pet.neutered ? '已绝育' : '未绝育或未填写' }}</el-descriptions-item>
          <el-descriptions-item label="性格特点">{{ pet.personality || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源说明">{{ pet.sourceDesc || '-' }}</el-descriptions-item>
          <el-descriptions-item label="领养要求">{{ pet.adoptionRequirements || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="actions">
          <el-button type="primary" icon="el-icon-document-checked" :disabled="pet.status !== 2" @click="openApply">申请领养</el-button>
          <el-button icon="el-icon-location-outline" @click="$router.push('/services')">查看附近服务</el-button>
        </div>
      </div>
    </section>

    <section class="service-section" v-if="services.length">
      <div class="section-head">
        <h2>领养后推荐服务</h2>
        <span>体检、疫苗、绝育、训练和寄养可在领养后继续衔接</span>
      </div>
      <div class="service-list">
        <article v-for="item in services" :key="item.id" @click="$router.push('/services')">
          <strong>{{ item.serviceName }}</strong>
          <span>{{ item.merchantName || '本地商家' }}</span>
          <small>{{ item.serviceType || '综合服务' }}</small>
        </article>
      </div>
    </section>

    <el-dialog title="提交领养申请" :visible.sync="applyDialog" width="620px">
      <el-form label-width="92px" class="apply-form">
        <el-form-item label="真实姓名"><el-input v-model="form.realName" maxlength="64" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" maxlength="32" /></el-form-item>
        <el-form-item label="所在城市"><el-input v-model="form.city" maxlength="64" /></el-form-item>
        <el-form-item label="居住情况"><el-input v-model="form.housingType" maxlength="64" placeholder="自有住房、租房、合住等" /></el-form-item>
        <el-form-item label="养宠经验"><el-input v-model="form.petExperience" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item label="申请理由"><el-input v-model="form.applyReason" type="textarea" :rows="3" maxlength="1000" show-word-limit /></el-form-item>
        <el-form-item label="领养承诺"><el-input v-model="form.commitment" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="例如：不弃养、接受回访、按时免疫" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="applyDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApply">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { applyAdoption, getAdoption, listServices } from '@/api/pet'
import { getToken } from '@/utils/auth'
import { resolveCanvasAssetUrl, resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'AdoptionDetail',
  data() {
    return {
      loading: false,
      submitting: false,
      applyDialog: false,
      pet: {},
      services: [],
      form: this.emptyForm()
    }
  },
  computed: {
    imageList() {
      return String(this.pet.imageUrls || '').split(',').filter(Boolean)
    }
  },
  created() {
    this.load()
    this.loadServices()
  },
  methods: {
    emptyForm() {
      return { realName: '', phone: '', city: '', housingType: '', petExperience: '', applyReason: '', commitment: '' }
    },
    load() {
      this.loading = true
      getAdoption(this.$route.params.id).then(res => {
        this.pet = res.data || {}
      }).finally(() => {
        this.loading = false
      })
    },
    loadServices() {
      listServices({ pageNum: 1, pageSize: 4, keyword: '体检' }).then(res => {
        this.services = res.rows || []
      }).catch(() => {
        this.services = []
      })
    },
    openApply() {
      if (!getToken()) {
        this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
        return
      }
      this.form = this.emptyForm()
      this.applyDialog = true
    },
    submitApply() {
      if (!this.form.realName || !this.form.phone) {
        this.$message.warning('请填写真实姓名和联系电话')
        return
      }
      this.submitting = true
      applyAdoption(this.pet.id, this.form).then(() => {
        this.$message.success('领养申请已提交')
        this.applyDialog = false
        this.$router.push('/adoption-manage')
      }).finally(() => {
        this.submitting = false
      })
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    backgroundImage(url) {
      return resolveCanvasBackgroundImage(url)
    },
    ageText(months) {
      if (!months) return ''
      if (months < 12) return months + '个月'
      const year = Math.floor(months / 12)
      const month = months % 12
      return month ? `${year}岁${month}个月` : `${year}岁`
    },
    statusText(status) {
      return ({ 2: '可申请', 3: '已预约', 4: '已领养' })[status] || '待审核'
    },
    statusTag(status) {
      return ({ 2: 'success', 3: 'warning', 4: 'info' })[status] || ''
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
.detail-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}
.detail-main {
  display: grid;
  grid-template-columns: 430px minmax(0, 1fr);
  gap: 22px;
}
.gallery,
.info,
.service-section {
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow);
}
.gallery {
  overflow: hidden;
}
.cover {
  min-height: 340px;
  background-size: cover;
  background-position: center;
}
.empty-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  font-size: 56px;
  font-weight: 800;
}
.thumbs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 12px;
}
.thumbs img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 8px;
}
.info {
  padding: 24px;
}
.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.title-row h1 {
  margin: 0;
  color: var(--pet-text);
}
.facts {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 16px 0;
}
.facts span {
  padding: 7px 10px;
  border-radius: 8px;
  background: var(--pet-primary-soft);
  color: var(--pet-primary-dark);
}
.desc-table {
  margin-top: 14px;
}
.actions {
  display: flex;
  gap: 12px;
  margin-top: 18px;
  flex-wrap: wrap;
}
.service-section {
  margin-top: 18px;
  padding: 18px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}
.section-head h2 {
  margin: 0;
}
.section-head span,
.service-list span,
.service-list small {
  color: var(--pet-text-muted);
}
.service-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}
.service-list article {
  display: grid;
  gap: 6px;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  cursor: pointer;
}
.apply-form {
  padding-right: 10px;
}
@media (max-width: 860px) {
  .detail-main {
    grid-template-columns: 1fr;
  }
  .detail-head,
  .section-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
