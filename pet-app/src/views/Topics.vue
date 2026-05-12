<template>
  <div class="page">
    <div class="pet-page-title">
      <div>
        <h1>话题广场</h1>
        <p>围绕喂养、健康、训练、出行等主题找到同好内容。</p>
      </div>
      <el-button type="primary" icon="el-icon-edit-outline" @click="$router.push('/publish')">发布动态</el-button>
    </div>
    <div class="topics">
      <article v-for="topic in topics" :key="topic.id" class="topic" @click="$router.push({ path: '/', query: { topicId: topic.id } })">
        <div class="topic-mark">#</div>
        <h3>{{ topic.name }}</h3>
        <p>{{ topic.description || '暂无话题说明' }}</p>
        <span>{{ topic.postCount || 0 }} 条内容<i class="el-icon-right"></i></span>
      </article>
      <el-empty v-if="topics.length === 0" description="暂无话题" />
    </div>
  </div>
</template>

<script>
import { listTopics } from '@/api/pet'

export default {
  name: 'Topics',
  data() {
    return { topics: [] }
  },
  created() {
    listTopics({ pageNum: 1, pageSize: 100 }).then(res => {
      this.topics = res.rows || []
    })
  }
}
</script>

<style scoped>
.page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 32px 20px 52px;
}
.topics {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}
.topic {
  position: relative;
  min-height: 178px;
  padding: 22px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--pet-border);
  cursor: pointer;
  box-shadow: var(--pet-shadow-soft);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.topic:hover {
  transform: translateY(-2px);
  border-color: var(--pet-border-strong);
  box-shadow: var(--pet-shadow);
}
.topic-mark {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
  border-radius: 8px;
  background: var(--pet-primary-soft);
  color: var(--pet-primary);
  font-weight: 800;
  font-size: 22px;
}
.topic h3 {
  margin: 0 0 10px;
  color: var(--pet-text);
  font-size: 19px;
}
.topic p {
  min-height: 46px;
  margin: 0 0 18px;
  color: var(--pet-text-muted);
  line-height: 1.65;
}
.topic span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--pet-primary);
  font-size: 13px;
  font-weight: 700;
}
</style>
