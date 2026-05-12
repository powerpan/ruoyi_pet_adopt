<template>
  <div class="page favorites-page">
    <section class="favorites-hero">
      <div>
        <div class="eyebrow">My Collections</div>
        <h1>我的收藏</h1>
        <p>集中查看已收藏的图文动态和短视频动态，取消收藏后会从列表移除。</p>
      </div>
      <el-button type="primary" icon="el-icon-edit-outline" @click="$router.push('/publish')">发布动态</el-button>
    </section>

    <section class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索收藏动态标题或正文" clearable @keyup.enter.native="reload" />
      <el-button type="primary" icon="el-icon-search" :loading="loading" @click="reload">搜索</el-button>
      <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
    </section>

    <section class="favorite-feed" v-loading="loading">
      <article v-for="post in posts" :key="post.id" class="favorite-card">
        <div class="cover" v-if="post.coverUrl" :style="{ backgroundImage: backgroundImage(post.coverUrl) }" @click="openPost(post)"></div>
        <div v-else class="cover cover-empty" @click="openPost(post)">{{ post.postType === 'video' ? '视频' : '图文' }}</div>
        <div class="favorite-body">
          <div class="favorite-top">
            <el-tag size="mini" :type="post.postType === 'video' ? 'warning' : 'success'">{{ post.postType === 'video' ? '短视频' : '图文动态' }}</el-tag>
            <span>{{ post.favoriteTime || post.createTime || '' }}</span>
          </div>
          <h2 @click="openPost(post)">{{ post.title || '未命名动态' }}</h2>
          <p>{{ post.content || '暂无正文' }}</p>
          <div class="favorite-meta">
            <span>{{ post.authorName || '匿名用户' }}</span>
            <span v-if="post.petName">{{ post.petName }}</span>
            <span>点赞 {{ post.likeCount || 0 }}</span>
            <span>评论 {{ post.commentCount || 0 }}</span>
          </div>
          <div class="favorite-actions">
            <el-button size="small" type="primary" plain @click="openPost(post)">查看详情</el-button>
            <el-button size="small" icon="el-icon-star-off" @click="removeFavorite(post)">取消收藏</el-button>
          </div>
        </div>
      </article>
      <el-empty v-if="!loading && posts.length === 0" description="暂无收藏动态">
        <el-button type="primary" @click="$router.push('/')">去首页看看</el-button>
      </el-empty>
    </section>

    <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
  </div>
</template>

<script>
import { interactPost, listFavoritePosts } from '@/api/pet'
import { resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'Favorites',
  data() {
    return {
      posts: [],
      total: 0,
      loading: false,
      query: { pageNum: 1, pageSize: 10, keyword: '' }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      const params = { ...this.query, keyword: this.query.keyword || undefined }
      listFavoritePosts(params).then(res => {
        this.posts = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    reload() {
      this.query.pageNum = 1
      this.load()
    },
    resetQuery() {
      this.query = { pageNum: 1, pageSize: this.query.pageSize || 10, keyword: '' }
      this.load()
    },
    openPost(post) {
      this.$router.push('/posts/' + post.id)
    },
    removeFavorite(post) {
      this.$confirm(`确认取消收藏「${post.title || post.id}」吗？`, '系统提示', { type: 'warning' }).then(() => {
        return interactPost(post.id, 'favorite')
      }).then(() => {
        this.$message.success('已取消收藏')
        this.load()
      }).catch(() => {})
    },
    backgroundImage(url) {
      return resolveCanvasBackgroundImage(url)
    }
  }
}
</script>

<style scoped>
.favorites-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 32px 20px 52px;
}
.favorites-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 18px;
  padding: 26px;
  border: 1px solid rgba(200, 216, 210, .8);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .94), rgba(248, 251, 250, .84)),
    linear-gradient(135deg, rgba(35, 130, 118, .14), rgba(215, 138, 43, .08));
  box-shadow: var(--pet-shadow-soft);
}
.eyebrow {
  color: var(--pet-primary);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  text-transform: uppercase;
}
.favorites-hero h1 {
  margin: 8px 0;
  color: var(--pet-text);
  font-size: 31px;
  line-height: 1.2;
}
.favorites-hero p {
  margin: 0;
  color: var(--pet-text-muted);
  line-height: 1.7;
}
.toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  gap: 12px;
  margin: 18px 0;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.favorite-feed {
  display: grid;
  gap: 14px;
}
.favorite-card {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 18px;
  padding: 14px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.cover {
  min-height: 160px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  cursor: pointer;
}
.cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--pet-primary);
  background: var(--pet-primary-soft);
  font-weight: 800;
}
.favorite-body {
  min-width: 0;
  display: grid;
  gap: 10px;
}
.favorite-top,
.favorite-meta,
.favorite-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.favorite-top {
  justify-content: space-between;
  color: var(--pet-text-muted);
  font-size: 12px;
}
.favorite-body h2 {
  margin: 0;
  color: var(--pet-text);
  font-size: 20px;
  line-height: 1.35;
  cursor: pointer;
}
.favorite-body p {
  margin: 0;
  color: #475569;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.favorite-meta {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.favorite-actions {
  justify-content: flex-end;
}
@media (max-width: 760px) {
  .favorites-page {
    padding: 22px 12px 44px;
  }
  .favorites-hero,
  .toolbar,
  .favorite-card {
    grid-template-columns: 1fr;
  }
  .favorites-hero {
    display: grid;
    align-items: start;
  }
}
</style>
