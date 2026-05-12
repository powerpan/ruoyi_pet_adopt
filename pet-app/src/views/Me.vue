<!--
  Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
-->
<template>
  <div class="page">
    <section class="profile-hero" v-loading="loading.profile" :style="coverStyle">
      <div class="avatar-wrap">
        <img v-if="profile.avatar" :src="mediaUrl(profile.avatar)" alt="头像" class="avatar">
        <div v-else class="avatar-fallback">{{ avatarText }}</div>
      </div>
      <div class="profile-copy">
        <div class="title-row">
          <h1>{{ profile.nickname || '我的主页' }}</h1>
          <el-tag size="small" :type="bloggerTagType">{{ bloggerLabel }}</el-tag>
        </div>
        <p>{{ profile.bio || '记录养宠日常、内容交流和本地服务体验。' }}</p>
        <div class="stats">
          <span><strong>{{ profile.postCount || 0 }}</strong>动态</span>
          <span><strong>{{ profile.followerCount || 0 }}</strong>粉丝</span>
          <span><strong>{{ profile.followingCount || 0 }}</strong>关注</span>
        </div>
      </div>
      <div class="hero-actions">
        <el-button type="primary" :loading="saving" @click="saveProfile">保存主页</el-button>
        <el-button icon="el-icon-star-off" @click="$router.push('/favorites')">我的收藏</el-button>
        <el-button :disabled="profile.bloggerStatus === 1 || profile.bloggerStatus === 2" @click="apply">申请宠物博主</el-button>
      </div>
    </section>

    <el-alert v-if="profileError" :title="profileError" type="warning" show-icon class="page-alert" :closable="false" />

    <div class="content-grid">
      <el-card shadow="never" class="panel form-panel">
        <div slot="header" class="panel-head">
          <span><i class="el-icon-user"></i>主页资料</span>
          <el-button size="mini" icon="el-icon-refresh" @click="loadProfile">刷新</el-button>
        </div>
        <el-form label-width="78px">
          <el-form-item label="昵称">
            <el-input v-model="profile.nickname" maxlength="30" placeholder="例如：团子铲屎官" />
          </el-form-item>
          <el-form-item label="头像">
            <pet-upload v-model="profile.avatar" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
          </el-form-item>
          <el-form-item label="封面">
            <pet-upload v-model="profile.homepageCover" :limit="1" list-type="picture-card" :file-type="['jpg','jpeg','png','gif']" :file-size="8" />
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="profile.bio" type="textarea" :rows="5" maxlength="180" show-word-limit placeholder="介绍你的养宠经验、内容方向或服务偏好" />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import {
  applyBlogger,
  getProfile,
  updateProfile
} from '@/api/pet'
import { resolveCanvasAssetUrl, resolveCanvasBackgroundImage } from '@/utils/canvasAssetUrl'

export default {
  name: 'Me',
  data() {
    return {
      profile: this.emptyProfile(),
      loading: { profile: false },
      saving: false,
      profileError: ''
    }
  },
  computed: {
    avatarText() {
      return (this.profile.nickname || '我').slice(0, 1)
    },
    bloggerLabel() {
      return ({ 0: '普通用户', 1: '宠物博主', 2: '审核中', 3: '未通过' })[this.profile.bloggerStatus] || '普通用户'
    },
    bloggerTagType() {
      return ({ 1: 'success', 2: 'warning', 3: 'danger' })[this.profile.bloggerStatus] || 'info'
    },
    coverStyle() {
      if (!this.profile.homepageCover) {
        return {}
      }
      return { backgroundImage: `linear-gradient(135deg, rgba(255,255,255,.9), rgba(255,255,255,.78)), ${resolveCanvasBackgroundImage(this.profile.homepageCover)}` }
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    emptyProfile() {
      return { nickname: '', avatar: '', bio: '', homepageCover: '', bloggerStatus: 0 }
    },
    loadProfile() {
      this.loading.profile = true
      this.profileError = ''
      getProfile({ noErrorMessage: true }).then(res => {
        this.profile = { ...this.emptyProfile(), ...(res.data || {}) }
      }).catch(err => {
        this.profileError = (err && err.message) || '个人主页接口暂不可用，请确认已登录并检查本地代理'
      }).finally(() => {
        this.loading.profile = false
      })
    },
    mediaUrl(url) {
      return resolveCanvasAssetUrl(url)
    },
    saveProfile() {
      if (!this.profile.nickname) {
        this.$message.warning('请先填写昵称')
        return
      }
      this.saving = true
      updateProfile(this.profile).then(() => {
        this.$message.success('主页已保存')
        this.loadProfile()
      }).finally(() => {
        this.saving = false
      })
    },
    apply() {
      applyBlogger().then(() => {
        this.$message.success('博主认证申请已提交')
        this.loadProfile()
      })
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
.profile-hero,
.panel {
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.profile-hero {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 22px;
  align-items: center;
  padding: 28px;
  background-size: cover;
  background-position: center;
}
.avatar-wrap,
.avatar,
.avatar-fallback {
  width: 92px;
  height: 92px;
}
.avatar {
  object-fit: cover;
  border-radius: 8px;
}
.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  color: #fff;
  font-size: 36px;
  font-weight: 700;
}
.title-row,
.panel-head,
.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.panel-head {
  justify-content: space-between;
}
.title-row {
  justify-content: flex-start;
  flex-wrap: wrap;
}
.title-row h1 {
  margin: 0;
  font-size: 28px;
}
.profile-copy p {
  margin: 10px 0 14px;
  color: var(--pet-text-muted);
  line-height: 1.7;
}
.stats {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  color: #6b7280;
}
.stats strong {
  margin-right: 4px;
  color: #111827;
}
.hero-actions {
  flex-wrap: wrap;
  justify-content: flex-end;
}
.page-alert {
  margin-top: 16px;
}
.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 18px;
  margin-top: 18px;
}
.panel {
  border-color: var(--pet-border);
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
.panel-head .el-button {
  border-color: #d6e7e1;
  background: #fff;
  color: var(--pet-primary-dark);
  font-weight: 700;
}
.panel-head .el-button:hover,
.panel-head .el-button:focus {
  border-color: var(--pet-primary);
  color: var(--pet-primary);
  background: var(--pet-primary-soft);
}
@media (max-width: 820px) {
  .profile-hero {
    grid-template-columns: 1fr;
  }
  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
