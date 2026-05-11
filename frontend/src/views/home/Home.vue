<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div v-if="loading" class="loading-container">
          <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          <p>加载中...</p>
        </div>
        
        <div v-else class="page-container">
          <!-- 欢迎横幅 -->
          <div class="welcome-banner">
            <div class="welcome-text">
              <h2>你好，{{ userStore.userInfo?.username || '同学' }}！</h2>
              <p>继续保持学习的热情，今天也要加油哦~</p>
            </div>
            <div class="welcome-stats">
              <div class="stat-item">
                <span class="stat-value">{{ stats?.totalStudyTime || 0 }}</span>
                <span class="stat-label">学习时长(小时)</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ stats?.weeklyProgress || 0 }}%</span>
                <span class="stat-label">本周进度</span>
              </div>
            </div>
          </div>

          <!-- 快速入口 -->
          <el-row :gutter="20" class="quick-actions">
            <el-col :span="6">
              <div class="action-card" @click="$router.push('/knowledge')">
                <el-icon :size="40" color="#409eff"><Reading /></el-icon>
                <h3>知识库</h3>
                <p>浏览学习各科知识</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-card" @click="$router.push('/plan')">
                <el-icon :size="40" color="#67c23a"><Calendar /></el-icon>
                <h3>学习计划</h3>
                <p>制定并跟踪学习计划</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-card" @click="$router.push('/exam')">
                <el-icon :size="40" color="#e6a23c"><Document /></el-icon>
                <h3>在线考试</h3>
                <p>测验学习成果</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-card" @click="$router.push('/favorites')">
                <el-icon :size="40" color="#f56c6c"><Star /></el-icon>
                <h3>我的收藏</h3>
                <p>查看收藏的知识点</p>
              </div>
            </el-col>
          </el-row>

          <!-- 统计数据 -->
          <el-row :gutter="20">
            <el-col :span="16">
              <div class="card-container">
                <div class="section-title">学科进度</div>
                <div class="progress-list">
                  <div v-for="item in stats?.subjectProgress" :key="item.subject" class="progress-item">
                    <div class="progress-header">
                      <span>{{ item.subject }}</span>
                      <span>{{ item.progress }}%</span>
                    </div>
                    <el-progress :percentage="item.progress" :color="getSubjectColor(item.subject)" />
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="card-container">
                <div class="section-title">计划完成率</div>
                <div class="completion-rate">
                  <el-progress
                    type="circle"
                    :percentage="stats?.planCompletion || 0"
                    :width="120"
                    :color="completionColor"
                  />
                  <p>本周目标完成</p>
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 最近考试 -->
          <div class="card-container">
            <div class="section-title">最近考试</div>
            <el-table :data="stats?.recentExams" style="width: 100%">
              <el-table-column prop="paperTitle" label="试卷名称" />
              <el-table-column prop="score" label="得分" width="100">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.score, row.totalScore)">
                    {{ row.score }}/{{ row.totalScore }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="用时(分钟)" width="120" />
              <el-table-column prop="submittedAt" label="考试时间" width="180" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" link @click="viewExamResult(row.id)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import { useUserStore } from '@/stores/user'
import { useDashboardStore } from '@/stores/dashboard'
import { Loading, Reading, Calendar, Document, Star } from '@element-plus/icons-vue'
import type { ExamRecord } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const dashboardStore = useDashboardStore()

const loading = computed(() => dashboardStore.isLoading)
const stats = computed(() => dashboardStore.stats)

const completionColor = computed(() => {
  const rate = stats.value?.planCompletion || 0
  if (rate >= 80) return '#67c23a'
  if (rate >= 50) return '#e6a23c'
  return '#f56c6c'
})

const getSubjectColor = (subject: string) => {
  const colors: Record<string, string> = {
    '数学': '#409eff',
    '物理': '#67c23a',
    '化学': '#e6a23c',
    '英语': '#f56c6c',
    '语文': '#909399'
  }
  return colors[subject] || '#409eff'
}

const getScoreClass = (score: number, total: number) => {
  const rate = (score / total) * 100
  if (rate >= 90) return 'score-excellent'
  if (rate >= 70) return 'score-good'
  if (rate >= 60) return 'score-pass'
  return 'score-fail'
}

const viewExamResult = (id: number) => {
  router.push(`/exam/${id}/result`)
}

onMounted(() => {
  dashboardStore.fetchStats()
})
</script>

<style scoped>
.home-layout {
  min-height: 100vh;
}

.home-content {
  display: flex;
}

.main-content {
  flex: 1;
  min-height: calc(100vh - 60px);
  overflow-y: auto;
}

.page-container {
  padding: 24px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.welcome-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 600;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.quick-actions {
  margin-bottom: 24px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.action-card h3 {
  margin: 12px 0 8px;
  color: #303133;
}

.action-card p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.progress-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.progress-item {
  padding: 8px 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.completion-rate {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.completion-rate p {
  margin-top: 16px;
  color: #606266;
}

.score-excellent {
  color: #67c23a;
  font-weight: 600;
}

.score-good {
  color: #409eff;
  font-weight: 600;
}

.score-pass {
  color: #e6a23c;
}

.score-fail {
  color: #f56c6c;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #909399;
}

.loading-container p {
  margin-top: 12px;
}
</style>
