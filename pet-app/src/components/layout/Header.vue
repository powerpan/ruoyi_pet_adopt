<template>
  <header class="header">
    <div class="brand" @click="$router.push('/')">
      <span class="brand-mark">宠</span>
      <span class="brand-copy">
        <strong>宠物领养平台</strong>
        <small>Pet Community</small>
      </span>
    </div>
    <nav class="nav">
      <router-link to="/"><i class="el-icon-s-home"></i>首页</router-link>
      <router-link to="/adoptions"><i class="el-icon-guide"></i>领养</router-link>
      <router-link to="/topics"><i class="el-icon-collection-tag"></i>话题</router-link>
      <router-link to="/publish"><i class="el-icon-edit-outline"></i>发布</router-link>
      <router-link v-if="token" to="/favorites"><i class="el-icon-star-off"></i>收藏</router-link>
      <router-link v-if="token" to="/pets"><i class="el-icon-document"></i>宠物</router-link>
      <router-link to="/services"><i class="el-icon-location-outline"></i>服务</router-link>
      <router-link v-if="hasMerchant" to="/merchant"><i class="el-icon-office-building"></i>商家</router-link>
      <router-link to="/health"><i class="el-icon-first-aid-kit"></i>健康</router-link>
      <router-link to="/me"><i class="el-icon-user"></i>我的</router-link>
    </nav>
    <div class="actions">
      <el-button v-if="!token" size="small" type="primary" icon="el-icon-user" @click="$router.push('/login')">登录</el-button>
      <template v-else>
        <el-badge :value="unreadCount" :hidden="unreadCount <= 0" :max="99" class="notice-badge">
          <el-button class="notice-button" size="small" icon="el-icon-bell" circle @click="$router.push('/notifications')" />
        </el-badge>
        <el-dropdown>
          <span class="user-entry">{{ name || '已登录' }}<i class="el-icon-arrow-down el-icon--right"></i></span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="$router.push('/notifications')">通知中心</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/adoption-manage')">我的领养</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/favorites')">我的收藏</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/pets')">宠物档案</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/me')">个人主页</el-dropdown-item>
            <el-dropdown-item v-if="hasMerchant" @click.native="$router.push('/merchant')">商家工作台</el-dropdown-item>
            <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </div>
  </header>
</template>

<script>
import { mapGetters } from 'vuex'
import { getUnreadNotificationCount, listMyMerchants } from '@/api/pet'

export default {
  name: 'Header',
  data() {
    return {
      hasMerchant: false,
      unreadCount: 0,
      noticeTimer: null
    }
  },
  computed: {
    ...mapGetters(['token', 'name'])
  },
  watch: {
    token: {
      immediate: true,
      handler() {
        this.refreshMerchantEntry()
        this.refreshUnreadCount()
      }
    },
    '$route.path'() {
      this.refreshMerchantEntry()
      this.refreshUnreadCount()
    }
  },
  mounted() {
    window.addEventListener('pet-merchant-updated', this.refreshMerchantEntry)
    window.addEventListener('pet-notification-updated', this.refreshUnreadCount)
    this.noticeTimer = window.setInterval(this.refreshUnreadCount, 30000)
  },
  beforeDestroy() {
    window.removeEventListener('pet-merchant-updated', this.refreshMerchantEntry)
    window.removeEventListener('pet-notification-updated', this.refreshUnreadCount)
    if (this.noticeTimer) {
      window.clearInterval(this.noticeTimer)
    }
  },
  methods: {
    refreshMerchantEntry() {
      if (!this.token) {
        this.hasMerchant = false
        return
      }
      listMyMerchants({ pageNum: 1, pageSize: 1 }, { noErrorMessage: true }).then(res => {
        this.hasMerchant = (res.rows || []).length > 0
      }).catch(() => {
        this.hasMerchant = false
      })
    },
    refreshUnreadCount() {
      if (!this.token) {
        this.unreadCount = 0
        return
      }
      getUnreadNotificationCount({ noErrorMessage: true }).then(res => {
        this.unreadCount = Number((res && res.data) || 0)
      }).catch(() => {
        this.unreadCount = 0
      })
    },
    logout() {
      this.$store.dispatch('LogOut').finally(() => {
        this.hasMerchant = false
        this.unreadCount = 0
        this.$router.push('/')
      })
    }
  }
}
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
  min-height: 72px;
  padding: 0 34px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  background: rgba(255, 255, 255, .92);
  border-bottom: 1px solid rgba(226, 232, 229, .86);
  backdrop-filter: blur(14px);
  box-shadow: 0 10px 26px rgba(23, 33, 31, .05);
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 230px;
  cursor: pointer;
}
.brand-mark {
  width: 42px;
  height: 42px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, var(--pet-primary), var(--pet-info));
  box-shadow: 0 9px 20px rgba(35, 130, 118, .22);
  font-weight: 800;
}
.brand-copy {
  display: grid;
  gap: 2px;
  line-height: 1.2;
}
.brand-copy strong {
  font-size: 17px;
  color: var(--pet-text);
}
.brand-copy small {
  color: var(--pet-text-muted);
  font-size: 12px;
}
.nav {
  display: flex;
  gap: 6px;
  padding: 6px;
  border: 1px solid #edf2ef;
  border-radius: 8px;
  background: #f8fbfa;
  scrollbar-width: none;
}
.nav::-webkit-scrollbar {
  display: none;
}
.nav a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  color: #344440;
  text-decoration: none;
  font-size: 14px;
  white-space: nowrap;
  transition: background .18s ease, color .18s ease;
}
.nav a.router-link-exact-active {
  color: var(--pet-primary);
  font-weight: 700;
  background: #fff;
  box-shadow: 0 6px 16px rgba(23, 33, 31, .06);
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: flex-end;
  min-width: 120px;
}
.notice-badge {
  line-height: 1;
}
.notice-button {
  border-color: #e1ece8;
  color: var(--pet-primary);
  background: #fff;
}
.user-entry {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 10px;
  border-radius: 8px;
  background: var(--pet-primary-soft);
  cursor: pointer;
  color: var(--pet-primary-dark);
  font-weight: 700;
}
@media (max-width: 1040px) {
  .header {
    align-items: flex-start;
    flex-wrap: wrap;
    padding: 12px 18px;
  }
  .brand {
    min-width: 0;
  }
  .nav {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }
  .actions {
    min-width: auto;
  }
}
@media (max-width: 560px) {
  .brand-copy small {
    display: none;
  }
  .brand-copy strong {
    font-size: 15px;
  }
  .nav a {
    padding: 0 10px;
  }
}
</style>
