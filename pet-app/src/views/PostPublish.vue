<template>
  <div class="page publish-page">
    <div class="publish-hero">
      <div>
        <h1>{{ form.id ? '编辑动态' : '发布动态' }}</h1>
        <p>{{ form.id ? '修改后会重新进入审核流程。' : '记录养宠经验，关联宠物和话题后提交审核。' }}</p>
      </div>
      <div class="hero-actions">
        <el-tag size="medium" effect="plain">{{ form.id ? '编辑中' : '待填写' }}</el-tag>
        <el-button icon="el-icon-back" @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>

    <section class="publish-layout">
      <el-card shadow="never" class="publish-card form-panel">
        <div slot="header" class="card-head">
          <span>{{ form.id ? '编辑宠物动态' : '发布宠物动态' }}</span>
          <el-tag size="small" type="warning">审核后展示</el-tag>
        </div>
        <el-form label-position="top" class="publish-form">
          <div class="form-section">
            <div class="section-title">基础信息</div>
            <div class="form-grid">
              <el-form-item label="标题" class="wide">
                <el-input v-model="form.title" maxlength="60" show-word-limit placeholder="例如：第一次带猫咪体检的经验" />
              </el-form-item>
              <el-form-item label="类型">
                <el-radio-group v-model="form.postType" class="type-switch">
                  <el-radio-button label="image">图文</el-radio-button>
                  <el-radio-button label="video">短视频</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="关联宠物">
                <el-select v-model="form.petId" clearable placeholder="可选，绑定到某只宠物" class="full">
                  <el-option v-for="pet in pets" :key="pet.id" :label="pet.name" :value="pet.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="话题" class="wide">
                <el-select v-model="form.topicIds" multiple filterable placeholder="选择关联话题" class="full">
                  <el-option v-for="topic in topics" :key="topic.id" :label="'# ' + topic.name" :value="topic.id" />
                </el-select>
              </el-form-item>
            </div>
          </div>

          <div class="form-section">
            <div class="section-title">内容素材</div>
            <div class="media-grid">
              <el-form-item label="封面">
                <pet-upload v-model="coverValue" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
              </el-form-item>
              <el-form-item label="媒体">
                <pet-upload v-model="mediaValue" :limit="9" :file-type="['jpg','jpeg','png','gif','mp4','mov','webm']" :file-size="80" />
              </el-form-item>
            </div>
            <el-form-item label="正文" class="content-item">
              <el-input v-model="form.content" type="textarea" :rows="8" maxlength="2000" show-word-limit placeholder="分享护理过程、服务体验或问题描述" />
            </el-form-item>
          </div>

          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="submit">提交审核</el-button>
            <el-button v-if="form.id" @click="resetForm">取消编辑</el-button>
            <el-button @click="$router.push('/')">返回首页</el-button>
          </div>
        </el-form>
      </el-card>

      <aside class="publish-side">
        <div class="side-card">
          <span class="side-label">当前状态</span>
          <strong>{{ form.id ? '编辑未通过或待审动态' : '新动态' }}</strong>
          <p>{{ form.title || '标题未填写' }}</p>
        </div>
        <div class="side-card">
          <span class="side-label">已选内容</span>
          <div class="side-stat">
            <span>话题</span>
            <strong>{{ form.topicIds.length }}</strong>
          </div>
          <div class="side-stat">
            <span>媒体</span>
            <strong>{{ splitPaths(mediaValue).length }}</strong>
          </div>
        </div>
      </aside>
    </section>

    <el-card shadow="never" class="post-card">
      <div slot="header" class="card-head">
        <span>我的帖子</span>
        <div class="post-tools">
          <el-select v-model="query.auditStatus" clearable size="small" placeholder="全部状态" @change="loadMyPosts">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="未通过" :value="2" />
          </el-select>
          <el-button size="small" @click="loadMyPosts">刷新</el-button>
        </div>
      </div>
      <el-table :data="myPosts" v-loading="loadingPosts">
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="90">
          <template slot-scope="scope">{{ scope.row.postType === 'video' ? '短视频' : '图文' }}</template>
        </el-table-column>
        <el-table-column label="审核状态" width="110">
          <template slot-scope="scope">
            <el-tag size="mini" :type="auditTag(scope.row.auditStatus)">{{ auditLabel(scope.row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="150" />
        <el-table-column label="数据" min-width="160">
          <template slot-scope="scope">
            浏览 {{ scope.row.viewCount || 0 }} / 点赞 {{ scope.row.likeCount || 0 }} / 评论 {{ scope.row.commentCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="156">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="$router.push('/posts/' + scope.row.id)" v-if="scope.row.auditStatus === 1">查看</el-button>
            <el-button size="mini" type="text" @click="editPost(scope.row)" v-if="scope.row.auditStatus !== 1">编辑</el-button>
            <el-button size="mini" type="text" class="danger-action" @click="removePost(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="loadMyPosts" />
    </el-card>
  </div>
</template>

<script>
import { deletePosts, listMyPosts, listPets, listTopics, publishPost, updatePost } from '@/api/pet'

export default {
  name: 'PostPublish',
  data() {
    return {
      form: this.emptyForm(),
      coverValue: '',
      mediaValue: '',
      topics: [],
      pets: [],
      myPosts: [],
      total: 0,
      query: { pageNum: 1, pageSize: 10, auditStatus: '' },
      submitting: false,
      loadingPosts: false
    }
  },
  created() {
    listTopics({ pageNum: 1, pageSize: 100 }).then(res => { this.topics = res.rows || [] })
    listPets({ pageNum: 1, pageSize: 100 }).then(res => { this.pets = res.rows || [] })
    this.loadMyPosts()
  },
  methods: {
    emptyForm() {
      return { postType: 'image', visibility: 0, topicIds: [], petId: '' }
    },
    loadMyPosts() {
      this.loadingPosts = true
      const params = { ...this.query, auditStatus: this.query.auditStatus === '' ? undefined : this.query.auditStatus }
      listMyPosts(params).then(res => {
        this.myPosts = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loadingPosts = false
      })
    },
    submit() {
      if (!this.form.title || !this.form.content) {
        this.$message.warning('请填写标题和正文')
        return
      }
      const mediaUrls = this.splitPaths(this.mediaValue)
      const coverUrl = this.splitPaths(this.coverValue)[0] || mediaUrls[0] || ''
      const payload = { ...this.form, coverUrl, mediaUrls, petId: this.form.petId || null }
      this.submitting = true
      const request = payload.id ? updatePost(payload) : publishPost(payload)
      request.then(() => {
        this.$message.success('动态已提交审核')
        this.resetForm()
        this.loadMyPosts()
      }).finally(() => {
        this.submitting = false
      })
    },
    editPost(row) {
      this.form = {
        id: row.id,
        title: row.title,
        content: row.content,
        postType: row.postType || 'image',
        visibility: row.visibility || 0,
        topicIds: row.topicIds || [],
        petId: row.petId || ''
      }
      this.coverValue = row.coverUrl || ''
      this.mediaValue = (row.mediaUrls || []).join(',')
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    removePost(row) {
      this.$confirm(`确认删除动态「${row.title || row.id}」吗？删除后评论、点赞和收藏记录也会一并清理。`, '系统提示', { type: 'warning' }).then(() => {
        return deletePosts(row.id)
      }).then(() => {
        this.$message.success('动态已删除')
        if (this.form.id === row.id) {
          this.resetForm()
        }
        this.loadMyPosts()
      }).catch(() => {})
    },
    resetForm() {
      this.form = this.emptyForm()
      this.coverValue = ''
      this.mediaValue = ''
    },
    splitPaths(value) {
      return String(value || '').split(',').map(item => item.trim()).filter(Boolean)
    },
    auditLabel(status) {
      return ({ 0: '待审核', 1: '已通过', 2: '未通过' })[status] || '待审核'
    },
    auditTag(status) {
      return ({ 1: 'success', 2: 'danger' })[status] || 'warning'
    }
  }
}
</script>

<style scoped>
.publish-page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 20px 56px;
}
.publish-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 24px 26px;
  border: 1px solid rgba(200, 216, 210, .8);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .94), rgba(248, 251, 250, .82)),
    linear-gradient(135deg, rgba(35, 130, 118, .13), rgba(215, 138, 43, .08));
  box-shadow: var(--pet-shadow-soft);
}
.publish-hero h1 {
  margin: 0 0 8px;
  color: var(--pet-text);
  font-size: 31px;
  line-height: 1.2;
}
.publish-hero p {
  margin: 0;
  color: var(--pet-text-muted);
  line-height: 1.7;
}
.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.publish-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 270px;
  gap: 18px;
  align-items: start;
}
.publish-card,
.post-card {
  border-radius: 8px;
  border-color: var(--pet-border);
  box-shadow: var(--pet-shadow-soft);
}
.publish-card {
  min-width: 0;
}
.post-card {
  margin-top: 18px;
}
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.card-head > div {
  display: flex;
  align-items: center;
  gap: 8px;
}
.card-head > span {
  color: var(--pet-text);
  font-weight: 800;
}
.publish-form {
  display: grid;
  gap: 22px;
}
.form-section {
  padding-bottom: 4px;
}
.section-title {
  margin-bottom: 14px;
  color: var(--pet-text);
  font-size: 15px;
  font-weight: 800;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 18px;
}
.form-grid .wide {
  grid-column: 1 / -1;
}
.media-grid {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}
.content-item {
  margin-top: 4px;
}
.form-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  padding-top: 2px;
}
.publish-side {
  display: grid;
  gap: 14px;
  position: sticky;
  top: 90px;
}
.side-card {
  padding: 18px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.side-card strong {
  display: block;
  margin-top: 6px;
  color: var(--pet-primary-dark);
  font-size: 20px;
  line-height: 1.3;
}
.side-card p {
  margin: 10px 0 0;
  color: var(--pet-text-muted);
  line-height: 1.6;
  word-break: break-word;
}
.side-label {
  color: var(--pet-text-muted);
  font-size: 13px;
  font-weight: 700;
}
.side-stat {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--pet-border);
  color: var(--pet-text-muted);
}
.side-stat strong {
  margin: 0;
  color: var(--pet-text);
  font-size: 18px;
}
.post-tools {
  min-width: 260px;
  justify-content: flex-end;
}
.full {
  width: 100%;
}
::v-deep .publish-card > .el-card__body {
  padding: 24px;
}
::v-deep .post-card > .el-card__body {
  padding: 0;
}
::v-deep .post-card .el-table {
  border-radius: 0 0 8px 8px;
}
::v-deep .post-card .pagination-container {
  padding: 16px 20px !important;
}
::v-deep .publish-form .el-form-item {
  margin-bottom: 18px;
}
::v-deep .el-form-item__label {
  color: #44524e;
  font-weight: 700;
  line-height: 1.35;
  padding-bottom: 8px;
}
::v-deep .type-switch,
::v-deep .type-switch .el-radio-button,
::v-deep .type-switch .el-radio-button__inner {
  width: 100%;
}
::v-deep .type-switch {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
::v-deep .type-switch .el-radio-button__inner {
  border-radius: 0;
}
::v-deep .type-switch .el-radio-button:first-child .el-radio-button__inner {
  border-radius: 8px 0 0 8px;
}
::v-deep .type-switch .el-radio-button:last-child .el-radio-button__inner {
  border-radius: 0 8px 8px 0;
}
::v-deep .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: var(--pet-primary);
  border-color: var(--pet-primary);
  box-shadow: -1px 0 0 0 var(--pet-primary);
}
::v-deep .pet-upload .el-upload--picture-card,
::v-deep .pet-upload .el-upload-list--picture-card .el-upload-list__item {
  width: 148px;
  height: 148px;
  line-height: 148px;
}
.danger-action {
  color: #d9534f;
}
@media (max-width: 760px) {
  .publish-page {
    padding: 22px 12px 44px;
  }
  .publish-hero,
  .card-head {
    display: grid;
    align-items: start;
  }
  .hero-actions {
    justify-content: flex-start;
  }
  .publish-layout,
  .form-grid,
  .media-grid {
    grid-template-columns: 1fr;
  }
  .publish-side {
    position: static;
  }
  .post-tools {
    min-width: 0;
    width: 100%;
  }
  .post-tools .el-select {
    flex: 1;
  }
}
@media (min-width: 761px) and (max-width: 1040px) {
  .publish-side {
    top: 146px;
  }
}
@media (max-width: 980px) {
  .publish-layout {
    grid-template-columns: 1fr;
  }
  .publish-side {
    position: static;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 620px) {
  .publish-side {
    grid-template-columns: 1fr;
  }
  .publish-hero h1 {
    font-size: 26px;
  }
  ::v-deep .publish-card > .el-card__body {
    padding: 18px;
  }
}
</style>
