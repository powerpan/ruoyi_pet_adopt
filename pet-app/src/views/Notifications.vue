<template>
  <div class="page notifications-page">
    <div class="page-head pet-page-title">
      <div>
        <h1>通知中心</h1>
        <p>预约、订单对话、审核结果、服务评价和健康提醒都会沉淀在这里。</p>
      </div>
      <div class="head-actions">
        <el-select v-model="query.status" clearable size="small" placeholder="全部状态" @change="load">
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
        </el-select>
        <el-button icon="el-icon-refresh" :loading="loading" @click="load">刷新</el-button>
        <el-button type="primary" icon="el-icon-check" :disabled="unreadCount === 0" @click="markAllRead">全部已读</el-button>
      </div>
    </div>

    <section class="notice-summary">
      <div>
        <span>未读通知</span>
        <strong>{{ unreadCount }}</strong>
      </div>
      <div>
        <span>当前列表</span>
        <strong>{{ total }}</strong>
      </div>
    </section>

    <section class="notice-list" v-loading="loading">
      <article
        v-for="item in notifications"
        :key="item.id"
        class="notice-item"
        :class="{ unread: item.status === 0 }"
        @click="openNotice(item)"
      >
        <div class="notice-icon">
          <i :class="noticeIcon(item.noticeType)"></i>
        </div>
        <div class="notice-main">
          <div class="notice-title">
            <h2>{{ item.title }}</h2>
            <el-tag size="mini" :type="item.status === 0 ? 'warning' : 'info'">{{ item.status === 0 ? '未读' : '已读' }}</el-tag>
          </div>
          <p>{{ item.content || '暂无详情' }}</p>
          <div class="notice-meta">
            <span>{{ noticeTypeLabel(item.noticeType) }}</span>
            <span v-if="item.actorName">来自 {{ item.actorName }}</span>
            <time>{{ item.createTime }}</time>
          </div>
        </div>
        <div class="notice-actions" @click.stop>
          <el-button v-if="item.status === 0" size="mini" type="text" @click="markRead(item)">标记已读</el-button>
          <el-button size="mini" type="text" class="danger-text" @click="removeNotice(item)">删除</el-button>
        </div>
      </article>
      <el-empty v-if="!loading && notifications.length === 0" description="暂无通知" />
    </section>

    <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="load" />
  </div>
</template>

<script>
import {
  deleteNotifications,
  getUnreadNotificationCount,
  listNotifications,
  readAllNotifications,
  readNotification
} from '@/api/pet'

export default {
  name: 'Notifications',
  data() {
    return {
      notifications: [],
      total: 0,
      unreadCount: 0,
      loading: false,
      query: { pageNum: 1, pageSize: 10, status: '' }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      const params = { ...this.query, status: this.query.status === '' ? undefined : this.query.status }
      listNotifications(params).then(res => {
        this.notifications = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
        this.loadUnreadCount()
      })
    },
    loadUnreadCount() {
      getUnreadNotificationCount({ noErrorMessage: true }).then(res => {
        this.unreadCount = Number((res && res.data) || 0)
        window.dispatchEvent(new Event('pet-notification-updated'))
      })
    },
    markRead(item) {
      readNotification(item.id).then(() => {
        this.load()
      })
    },
    markAllRead() {
      readAllNotifications().then(() => {
        this.$message.success('已全部标记为已读')
        this.load()
      })
    },
    removeNotice(item) {
      this.$confirm('确认删除这条通知吗？', '系统提示', { type: 'warning' }).then(() => {
        return deleteNotifications(item.id)
      }).then(() => {
        this.$message.success('通知已删除')
        this.load()
      }).catch(() => {})
    },
    openNotice(item) {
      const go = () => {
        if (item.actionUrl) {
          this.$router.push(item.actionUrl).catch(() => {})
        }
      }
      if (item.status === 0) {
        readNotification(item.id).then(() => {
          this.loadUnreadCount()
          go()
        })
        return
      }
      go()
    },
    noticeTypeLabel(type) {
      return ({
        service_request: '预约咨询',
        service_request_status: '订单进度',
        service_message: '订单对话',
        service_review: '服务评价',
        review_hide_audit: '评价屏蔽',
        boarding_request: '寄养授权',
        boarding_approved: '寄养授权',
        boarding_rejected: '寄养授权',
        boarding_cancelled: '寄养变更',
        merchant_audit: '商家审核',
        blogger_audit: '博主认证',
        post_audit: '动态审核',
        comment_audit: '评论审核',
        post_comment: '内容互动',
        reminder_due: '健康提醒'
      })[type] || '系统通知'
    },
    noticeIcon(type) {
      return ({
        service_request: 'el-icon-tickets',
        service_request_status: 'el-icon-finished',
        service_message: 'el-icon-chat-dot-round',
        service_review: 'el-icon-star-off',
        review_hide_audit: 'el-icon-view',
        boarding_request: 'el-icon-first-aid-kit',
        boarding_approved: 'el-icon-circle-check',
        boarding_rejected: 'el-icon-circle-close',
        boarding_cancelled: 'el-icon-refresh-left',
        merchant_audit: 'el-icon-office-building',
        blogger_audit: 'el-icon-user',
        post_audit: 'el-icon-document-checked',
        comment_audit: 'el-icon-chat-line-round',
        post_comment: 'el-icon-chat-dot-square',
        reminder_due: 'el-icon-alarm-clock'
      })[type] || 'el-icon-bell'
    }
  }
}
</script>

<style scoped>
.notifications-page {
  max-width: 980px;
  margin: 0 auto;
  padding: 32px 20px 56px;
}
.head-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}
.notice-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}
.notice-summary div {
  padding: 18px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
}
.notice-summary span {
  display: block;
  color: var(--pet-text-muted);
  font-size: 13px;
}
.notice-summary strong {
  display: block;
  margin-top: 6px;
  color: var(--pet-text);
  font-size: 28px;
}
.notice-list {
  min-height: 220px;
  display: grid;
  gap: 12px;
}
.notice-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: start;
  padding: 16px;
  border: 1px solid var(--pet-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--pet-shadow-soft);
  cursor: pointer;
  transition: border-color .18s ease, transform .18s ease, box-shadow .18s ease;
}
.notice-item:hover {
  transform: translateY(-1px);
  border-color: rgba(35, 130, 118, .34);
  box-shadow: var(--pet-shadow);
}
.notice-item.unread {
  background: linear-gradient(90deg, rgba(35, 130, 118, .08), #fff 34%);
}
.notice-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: var(--pet-primary);
  background: var(--pet-primary-soft);
  font-size: 20px;
}
.notice-title {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: space-between;
}
.notice-title h2 {
  min-width: 0;
  margin: 0;
  color: var(--pet-text);
  font-size: 17px;
  line-height: 1.35;
}
.notice-main p {
  margin: 8px 0 10px;
  color: #4b5c57;
  line-height: 1.7;
  white-space: pre-wrap;
}
.notice-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  color: var(--pet-text-muted);
  font-size: 12px;
}
.notice-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}
.danger-text {
  color: var(--pet-danger);
}
@media (max-width: 680px) {
  .notice-summary,
  .notice-item {
    grid-template-columns: 1fr;
  }
  .notice-actions {
    justify-content: flex-end;
  }
}
</style>
