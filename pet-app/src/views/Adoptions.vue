<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="page adoption-page">
    <div class="page-head pet-page-title">
      <div>
        <h1>领养大厅</h1>
        <p>查看已审核的待领养宠物，按城市、物种和关键词快速筛选。</p>
      </div>
      <div class="head-actions">
        <el-button icon="el-icon-s-management" @click="$router.push('/adoption-manage')">我的领养</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="$router.push({ path: '/adoption-manage', query: { tab: 'publish' } })">发布领养</el-button>
      </div>
    </div>

    <section class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索名称、品种或领养要求" clearable @keyup.enter.native="load" />
      <el-select v-model="query.species" clearable placeholder="物种" class="filter-select">
        <el-option label="猫" value="猫" />
        <el-option label="狗" value="狗" />
        <el-option label="兔" value="兔" />
        <el-option label="鸟" value="鸟" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-input v-model="query.city" placeholder="城市" clearable class="city-input" @keyup.enter.native="load" />
      <el-select v-model="query.status" clearable placeholder="状态" class="filter-select">
        <el-option label="可申请" :value="2" />
        <el-option label="已预约" :value="3" />
        <el-option label="已领养" :value="4" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" :loading="loading" @click="load">搜索</el-button>
      <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
    </section>

    <section class="adoption-grid" v-loading="loading">
      <article v-for="item in rows" :key="item.id" class="adoption-card" @click="$router.push('/adoptions/' + item.id)">
        <div v-if="item.coverUrl" class="cover" :style="{ backgroundImage: backgroundImage(item.coverUrl) }"></div>
        <div v-else class="cover empty-cover">{{ (item.name || '?').slice(0, 1) }}</div>
        <div class="card-body">
          <div class="card-title">
            <h3>{{ item.name || '未命名宠物' }}</h3>
            <el-tag size="mini" :type="statusTag(item.status)">{{ statusText(item.status) }}</el-tag>
          </div>
          <p>{{ [item.species, item.breed, ageText(item.ageMonths)].filter(Boolean).join(' · ') || '基础信息待完善' }}</p>
          <div class="meta">
            <span><i class="el-icon-location-outline"></i>{{ [item.city, item.district].filter(Boolean).join(' ') || '未填写地区' }}</span>
            <span><i class="el-icon-user"></i>{{ item.publisherName || '发布方' }}</span>
          </div>
          <div class="desc">{{ item.adoptionRequirements || item.personality || '暂无领养要求说明' }}</div>
        </div>
      </article>
      <el-empty v-if="!loading && rows.length === 0" description="暂无符合条件的待领养宠物" />
    </section>
  </div>
</template>

<script>
import { listAdoptions } from '@/api/pet'
import { resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'Adoptions',
  data() {
    return {
      loading: false,
      rows: [],
      query: {
        keyword: '',
        species: '',
        city: '',
        status: '',
        pageNum: 1,
        pageSize: 24
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      const params = { ...this.query, status: this.query.status || undefined }
      listAdoptions(params).then(res => {
        this.rows = res.rows || []
      }).finally(() => {
        this.loading = false
      })
    },
    reset() {
      this.query = { keyword: '', species: '', city: '', status: '', pageNum: 1, pageSize: 24 }
      this.load()
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
      return ({ 2: '可申请', 3: '已预约', 4: '已领养' })[status] || '待处理'
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
.page-head,
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}
.page-head {
  margin-bottom: 18px;
}
.page-head h1 {
  margin: 0 0 8px;
}
.page-head p {
  margin: 0;
  color: var(--pet-text-muted);
}
.head-actions,
.toolbar {
  flex-wrap: wrap;
}
.toolbar {
  justify-content: flex-start;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow);
}
.toolbar .el-input {
  width: 280px;
}
.filter-select {
  width: 130px;
}
.city-input {
  width: 150px !important;
}
.adoption-grid {
  min-height: 240px;
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}
.adoption-card {
  overflow: hidden;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow);
  cursor: pointer;
  transition: transform .18s ease, box-shadow .18s ease;
}
.adoption-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 34px rgba(23, 33, 31, .12);
}
.cover {
  height: 176px;
  background-size: cover;
  background-position: center;
}
.empty-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  font-size: 44px;
  font-weight: 800;
}
.card-body {
  padding: 16px;
}
.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}
.card-title h3 {
  margin: 0;
  color: var(--pet-text);
  font-size: 18px;
}
.card-body p,
.desc {
  color: var(--pet-text-muted);
  line-height: 1.6;
}
.card-body p {
  margin: 8px 0;
}
.meta {
  display: grid;
  gap: 6px;
  margin: 10px 0;
  color: #5d6b68;
  font-size: 13px;
}
.meta span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.desc {
  display: -webkit-box;
  min-height: 44px;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
@media (max-width: 760px) {
  .page-head {
    align-items: flex-start;
    flex-direction: column;
  }
  .toolbar .el-input,
  .filter-select,
  .city-input {
    width: 100% !important;
  }
}
</style>
