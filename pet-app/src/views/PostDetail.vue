<template>
  <div class="page">
    <div class="detail-nav">
      <el-button size="small" icon="el-icon-back" @click="$router.back()">返回</el-button>
      <div class="nav-actions">
        <el-button v-if="canDeletePost" size="small" type="danger" icon="el-icon-delete" @click="removePost">删除动态</el-button>
        <el-button size="small" type="primary" icon="el-icon-edit-outline" @click="$router.push('/publish')">发布动态</el-button>
      </div>
    </div>
    <article class="post" v-loading="loading.post">
      <img v-if="post.coverUrl" :src="mediaUrl(post.coverUrl)" alt="封面" class="cover">
      <div class="post-main">
        <div class="type">{{ post.postType === 'video' ? '短视频' : '图文动态' }}</div>
        <h1>{{ post.title || '动态详情' }}</h1>
        <div class="meta">浏览 {{ post.viewCount || 0 }} · 点赞 {{ post.likeCount || 0 }} · 评论 {{ post.commentCount || 0 }} · 收藏 {{ post.favoriteCount || 0 }}</div>
        <p class="content">{{ post.content || '暂无正文' }}</p>
        <div class="media-list" v-if="post.mediaUrls && post.mediaUrls.length">
          <div v-for="url in post.mediaUrls" :key="url" class="media-item">
            <video v-if="isVideo(url)" :src="mediaUrl(url)" controls />
            <img v-else :src="mediaUrl(url)" alt="动态图片">
          </div>
        </div>
        <div class="actions">
          <el-button size="small" :type="post.liked ? 'primary' : 'default'" icon="el-icon-thumb" @click="interact('like')">
            {{ post.liked ? '取消点赞' : '点赞' }}
          </el-button>
          <el-button size="small" :type="post.favorited ? 'warning' : 'default'" icon="el-icon-star-off" @click="interact('favorite')">
            {{ post.favorited ? '取消收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </article>

    <section class="comments" v-loading="loading.comments">
      <div class="section-head">
        <h2>评论</h2>
        <span>评论提交后需后台审核</span>
      </div>
      <div class="comment-box">
        <el-input v-model="comment" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="写下你的想法" />
        <el-button type="primary" @click="submitComment">发表评论</el-button>
      </div>
      <div v-for="item in comments" :key="item.id" class="comment">
        <div class="comment-meta">{{ item.userName || '匿名用户' }} · {{ item.createTime || '' }}</div>
        <p>{{ item.content }}</p>
      </div>
      <el-empty v-if="!loading.comments && comments.length === 0" description="暂无已通过审核的评论" />
    </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { addComment, deletePosts, getPost, interactPost, listComments } from '@/api/pet'
import { getToken } from '@/utils/auth'
import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

export default {
  name: 'PostDetail',
  data() {
    return { post: {}, comments: [], comment: '', loading: { post: false, comments: false } }
  },
  computed: {
    ...mapGetters(['id', 'token']),
    canDeletePost() {
      return Boolean(this.token && this.post && this.post.authorId && String(this.post.authorId) === String(this.id))
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      const postId = this.$route.params.id
      this.loading.post = true
      this.loading.comments = true
      getPost(postId).then(res => {
        this.post = res.data || {}
      }).finally(() => {
        this.loading.post = false
      })
      listComments(postId).then(res => {
        this.comments = res.rows || []
      }).finally(() => {
        this.loading.comments = false
      })
    },
    ensureLogin() {
      if (getToken()) return true
      this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
      return false
    },
    isVideo(url) {
      return /\.(mp4|mov|m4v|webm)$/i.test(url || '')
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    interact(type) {
      if (!this.ensureLogin()) return
      interactPost(this.$route.params.id, type).then(res => {
        const active = res && res.data && res.data.active
        const action = active === false ? '已取消' : '已记录'
        this.$message.success(action)
        this.load()
      })
    },
    removePost() {
      if (!this.canDeletePost) return
      this.$confirm(`确认删除动态「${this.post.title || this.post.id}」吗？删除后评论、点赞和收藏记录也会一并清理。`, '系统提示', { type: 'warning' }).then(() => {
        return deletePosts(this.post.id)
      }).then(() => {
        this.$message.success('动态已删除')
        this.$router.push('/publish')
      }).catch(() => {})
    },
    submitComment() {
      if (!this.ensureLogin()) return
      if (!this.comment.trim()) return
      addComment(this.$route.params.id, { content: this.comment.trim() }).then(() => {
        this.comment = ''
        this.$message.success('评论已提交，审核后展示')
      })
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 20px 52px;
}
.detail-nav {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}
.nav-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.post,
.comments {
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--pet-border);
  overflow: hidden;
  box-shadow: var(--pet-shadow-soft);
}
.cover {
  width: 100%;
  max-height: 420px;
  object-fit: cover;
  display: block;
}
.post-main,
.comments {
  padding: 26px;
}
.type {
  color: var(--pet-primary);
  font-size: 13px;
  font-weight: 800;
  margin-bottom: 8px;
}
.post h1 {
  margin: 0 0 10px;
  color: var(--pet-text);
  font-size: 30px;
  line-height: 1.25;
}
.meta {
  color: var(--pet-text-muted);
  margin-bottom: 18px;
}
.content {
  white-space: pre-wrap;
  color: #303133;
  line-height: 1.8;
  font-size: 15px;
}
.media-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-top: 18px;
}
.media-item img,
.media-item video {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  border-radius: 8px;
  background: #f2f6f4;
}
.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.comments {
  margin-top: 18px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}
.section-head h2 {
  margin: 0;
  font-size: 20px;
}
.section-head span {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.comment-box {
  display: grid;
  gap: 10px;
  margin: 18px 0;
}
.comment {
  padding: 14px 0;
  border-top: 1px solid var(--pet-border);
}
.comment-meta {
  color: var(--pet-text-muted);
  font-size: 13px;
}
.comment p {
  margin: 8px 0 0;
  color: #303133;
  line-height: 1.7;
}
@media (max-width: 640px) {
  .detail-nav {
    display: grid;
  }
  .post h1 {
    font-size: 25px;
  }
}
</style>
