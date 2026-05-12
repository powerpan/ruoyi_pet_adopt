<template>
  <div class="page home-page">
    <section class="hero" aria-label="宠物动态工作台">
      <div class="hero-copy">
        <div class="eyebrow">Pet Community</div>
        <h1>今日养宠动态</h1>
        <p>用帖子沉淀经验，用话题聚合问题，用附近服务完成线下对接。</p>
        <div class="hero-actions">
          <el-button type="primary" icon="el-icon-edit-outline" @click="$router.push('/publish')">发布动态</el-button>
          <el-button icon="el-icon-location-outline" @click="$router.push('/services')">查找服务</el-button>
        </div>
      </div>
      <div class="hero-board">
        <div class="board-row">
          <span>已展示动态</span>
          <strong>{{ posts.length }}</strong>
        </div>
        <div class="board-row">
          <span>可用话题</span>
          <strong>{{ topics.length }}</strong>
        </div>
        <div class="board-topics">
          <button v-for="topic in topTopics" :key="topic.id" @click="selectTopic(topic.id)"># {{ topic.name }}</button>
        </div>
      </div>
    </section>

    <section class="quick-grid">
      <button v-for="item in quickEntries" :key="item.path" @click="$router.push(item.path)">
        <i :class="item.icon"></i>
        <span>{{ item.label }}</span>
      </button>
    </section>

    <section class="toolbar">
      <el-input v-model="keyword" placeholder="搜索帖子标题或正文" clearable @keyup.enter.native="loadPosts" />
      <el-select v-model="topicId" clearable placeholder="全部话题" class="topic-select" @change="changeTopic">
        <el-option v-for="topic in topics" :key="topic.id" :label="'# ' + topic.name" :value="topic.id" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" :loading="loading" @click="loadPosts">搜索</el-button>
    </section>

    <section class="topic-strip" v-if="topics.length">
      <button
        v-for="topic in topics"
        :key="topic.id"
        :class="{ active: Number(topicId) === Number(topic.id) }"
        @click="selectTopic(topic.id)"
      >
        # {{ topic.name }}
      </button>
    </section>

    <div class="feed-head">
      <h2>信息流</h2>
      <span>{{ topicId ? '当前话题筛选' : '全部已审核内容' }}</span>
    </div>

    <section class="feed" v-loading="loading">
      <article v-for="post in posts" :key="post.id" class="post" @click="$router.push('/posts/' + post.id)">
        <div class="post-cover" v-if="post.coverUrl" :style="{ backgroundImage: backgroundImage(post.coverUrl) }"></div>
        <div v-else class="post-cover empty-cover">{{ post.postType === 'video' ? '视频' : '图文' }}</div>
        <div class="post-body">
          <div class="post-type">{{ post.postType === 'video' ? '短视频' : '图文动态' }}</div>
          <h3>{{ post.title || '未命名动态' }}</h3>
          <p>{{ post.content || '暂无正文' }}</p>
          <div class="post-meta">
            <span>浏览 {{ post.viewCount || 0 }}</span>
            <span>点赞 {{ post.likeCount || 0 }}</span>
            <span>评论 {{ post.commentCount || 0 }}</span>
            <span>收藏 {{ post.favoriteCount || 0 }}</span>
          </div>
        </div>
      </article>
      <el-empty v-if="!loading && posts.length === 0" description="暂无内容，去发布第一条宠物动态吧" />
    </section>
  </div>
</template>

<script>
import { listPosts, listTopics } from '@/api/pet'
import { resolveCanvasAssetUrl, resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'Home',
  data() {
    return {
      keyword: '',
      topicId: '',
      topics: [],
      posts: [],
      loading: false,
      quickEntries: [
        { label: '话题广场', path: '/topics', icon: 'el-icon-collection-tag' },
        { label: '宠物档案', path: '/pets', icon: 'el-icon-document' },
        { label: '健康档案', path: '/health', icon: 'el-icon-first-aid-kit' },
        { label: '附近服务', path: '/services', icon: 'el-icon-location-outline' },
        { label: '我的主页', path: '/me', icon: 'el-icon-user' }
      ]
    }
  },
  computed: {
    topTopics() {
      return this.topics.slice(0, 4)
    }
  },
  created() {
    this.topicId = this.$route.query.topicId || ''
    this.loadTopics()
    this.loadPosts()
  },
  watch: {
    '$route.query.topicId'(value) {
      this.topicId = value || ''
      this.loadPosts()
    }
  },
  methods: {
    loadTopics() {
      listTopics({ pageNum: 1, pageSize: 100 }).then(res => {
        this.topics = res.rows || []
      })
    },
    loadPosts() {
      this.loading = true
      const params = {
        keyword: this.keyword,
        topicId: this.topicId || undefined,
        pageNum: 1,
        pageSize: 20
      }
      listPosts(params).then(res => {
        this.posts = res.rows || []
      }).finally(() => {
        this.loading = false
      })
    },
    selectTopic(id) {
      if (Number(this.topicId) === Number(id)) {
        this.topicId = ''
        this.$router.push({ path: '/' }).catch(() => {})
        return
      }
      this.topicId = id
      this.$router.push({ path: '/', query: { topicId: id } }).catch(() => {})
    },
    changeTopic() {
      const query = this.topicId ? { topicId: this.topicId } : {}
      this.$router.push({ path: '/', query }).catch(() => {})
      this.loadPosts()
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    backgroundImage(url) {
      return resolveCanvasBackgroundImage(url)
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 30px 20px 52px;
}
.hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 330px;
  gap: 24px;
  align-items: stretch;
  min-height: 278px;
  padding: 34px;
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(23, 33, 31, .78), rgba(23, 33, 31, .36)),
    url("../assets/img/login-background.jpg") center / cover;
  box-shadow: var(--pet-shadow);
  overflow: hidden;
}
.hero-copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.eyebrow {
  margin-bottom: 12px;
  color: #f5c26f;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
}
.hero h1 {
  max-width: 690px;
  margin: 0 0 14px;
  font-size: 42px;
  line-height: 1.25;
  color: #fff;
}
.hero p {
  margin: 0 0 24px;
  color: rgba(255, 255, 255, .86);
  font-size: 16px;
  line-height: 1.8;
}
.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.hero-board {
  display: grid;
  align-content: space-between;
  gap: 14px;
  padding: 18px;
  border-radius: 8px;
  background: rgba(255, 255, 255, .9);
  border: 1px solid rgba(255, 255, 255, .72);
  backdrop-filter: blur(12px);
}
.board-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px;
  border-radius: 8px;
  background: #f8fbfa;
}
.board-row span {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.board-row strong {
  color: var(--pet-primary-dark);
  font-size: 28px;
}
.board-topics {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.board-topics button {
  height: 32px;
  padding: 0 12px;
  border: 1px solid #d8e7e3;
  border-radius: 8px;
  background: #fff;
  color: var(--pet-primary);
  cursor: pointer;
}
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(156px, 1fr));
  gap: 12px;
  margin-top: 18px;
}
.quick-grid button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  height: 52px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  color: var(--pet-text);
  cursor: pointer;
  font-weight: 700;
  box-shadow: var(--pet-shadow-soft);
}
.quick-grid i {
  color: var(--pet-primary);
  font-size: 18px;
}
.toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 220px auto;
  gap: 12px;
  margin: 22px 0 14px;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.topic-strip {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}
.topic-strip button {
  height: 34px;
  padding: 0 14px;
  border: 1px solid #d8e7e3;
  border-radius: 8px;
  background: #fff;
  color: var(--pet-primary-dark);
  cursor: pointer;
}
.topic-strip button.active {
  background: var(--pet-primary);
  color: #fff;
  border-color: var(--pet-primary);
}
.feed-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 14px;
  margin: 8px 0 14px;
}
.feed-head h2 {
  margin: 0;
  font-size: 22px;
}
.feed-head span {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.feed {
  min-height: 220px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.post {
  overflow: hidden;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: transform .18s ease, box-shadow .18s ease;
}
.post:hover {
  transform: translateY(-2px);
  box-shadow: var(--pet-shadow);
}
.post-cover {
  height: 164px;
  background-size: cover;
  background-position: center;
}
.empty-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--pet-primary-soft);
  color: var(--pet-primary);
  font-weight: 700;
}
.post-body {
  padding: 16px;
}
.post-type {
  margin-bottom: 8px;
  color: var(--pet-primary);
  font-size: 12px;
  font-weight: 700;
}
.post h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #1f2937;
}
.post p {
  height: 46px;
  margin: 0 0 14px;
  color: #606266;
  line-height: 1.6;
  overflow: hidden;
}
.post-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: #909399;
  font-size: 13px;
}
@media (max-width: 820px) {
  .hero,
  .toolbar,
  .quick-grid {
    grid-template-columns: 1fr;
  }
  .hero {
    padding: 28px;
  }
  .hero h1 {
    font-size: 30px;
  }
}
</style>
