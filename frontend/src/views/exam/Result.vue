<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <el-page-header @back="goBack" :title="'返回'" content="成绩分析" />
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          </div>
          
          <div v-else class="result-container">
            <!-- 成绩概览 -->
            <div class="score-overview">
              <el-card class="score-card">
                <div class="score-circle">
                  <el-progress
                    type="circle"
                    :percentage="scoreRate"
                    :width="160"
                    :color="scoreColor"
                  />
                </div>
                <div class="score-info">
                  <h2>{{ examRecord?.score || 0 }} 分</h2>
                  <p>满分 {{ examRecord?.totalScore || 100 }}</p>
                  <el-tag :type="getResultType(scoreRate)" size="large">
                    {{ getResultText(scoreRate) }}
                  </el-tag>
                </div>
              </el-card>
              
              <el-card class="stats-card">
                <div class="stat-grid">
                  <div class="stat-item">
                    <el-icon :size="24"><Timer /></el-icon>
                    <span class="stat-label">用时</span>
                    <span class="stat-value">{{ examRecord?.duration || 0 }}分钟</span>
                  </div>
                  <div class="stat-item">
                    <el-icon :size="24"><Check /></el-icon>
                    <span class="stat-label">正确率</span>
                    <span class="stat-value">{{ analysis?.correctRate || 0 }}%</span>
                  </div>
                  <div class="stat-item">
                    <el-icon :size="24"><Close /></el-icon>
                    <span class="stat-label">错题数</span>
                    <span class="stat-value">{{ analysis?.wrongQuestions?.length || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon :size="24"><Star /></el-icon>
                    <span class="stat-label">排名</span>
                    <span class="stat-value">前{{ '20%' }}</span>
                  </div>
                </div>
              </el-card>
            </div>

            <!-- 成绩趋势 -->
            <el-card class="trend-card">
              <div class="section-title">成绩趋势</div>
              <div class="chart-placeholder">
                <el-icon :size="60" color="#c0c4cc"><TrendCharts /></el-icon>
                <p>成绩趋势图表</p>
              </div>
            </el-card>

            <!-- 错题分析 -->
            <el-card class="wrong-questions-card">
              <div class="section-title">错题分析</div>
              <el-table :data="analysis?.wrongQuestions || []" style="width: 100%">
                <el-table-column prop="id" label="题号" width="80" />
                <el-table-column prop="content" label="题目内容" />
                <el-table-column prop="score" label="分值" width="80" />
                <el-table-column label="操作" width="100">
                  <template #default>
                    <el-button type="primary" link>查看解析</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!analysis?.wrongQuestions?.length" description="太棒了，没有错题！" />
            </el-card>

            <!-- 知识点掌握情况 -->
            <el-card class="knowledge-card">
              <div class="section-title">知识点掌握情况</div>
              <div class="knowledge-list">
                <div v-for="item in analysis?.knowledgeAnalysis || []" :key="item.name" class="knowledge-item">
                  <div class="knowledge-header">
                    <span>{{ item.name }}</span>
                    <span>{{ item.correctRate }}%</span>
                  </div>
                  <el-progress :percentage="item.correctRate" :color="getKnowledgeColor(item.correctRate)" />
                </div>
              </div>
            </el-card>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="doAgain">再考一次</el-button>
              <el-button size="large" @click="goBack">返回试卷列表</el-button>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import { Loading, Timer, Check, Close, Star, TrendCharts } from '@element-plus/icons-vue'
import type { ExamRecord, Question } from '@/types'

const route = useRoute()
const router = useRouter()

const loading = ref(false)

const examRecord = ref<ExamRecord>({
  id: 1,
  paperId: 1,
  paperTitle: '高一数学月考',
  score: 92,
  totalScore: 100,
  duration: 85,
  submittedAt: '2024-03-15 14:30',
  answers: {}
})

const analysis = ref<{
  score: number
  totalScore: number
  correctRate: number
  wrongQuestions: Question[]
  knowledgeAnalysis: { name: string; correctRate: number }[]
} | null>({
  score: 92,
  totalScore: 100,
  correctRate: 85,
  wrongQuestions: [
    { id: 1, paperId: 1, type: 'choice', content: '这道题考查集合的运算...', answer: 'C', score: 5 },
    { id: 3, paperId: 1, type: 'fill', content: '这道题考查三角函数...', answer: '45', score: 3 }
  ],
  knowledgeAnalysis: [
    { name: '集合与函数', correctRate: 90 },
    { name: '三角函数', correctRate: 75 },
    { name: '数列', correctRate: 95 },
    { name: '不等式', correctRate: 80 }
  ]
})

const scoreRate = computed(() => {
  if (!examRecord.value) return 0
  return Math.round((examRecord.value.score / examRecord.value.totalScore) * 100)
})

const scoreColor = computed(() => {
  const rate = scoreRate.value
  if (rate >= 90) return '#67c23a'
  if (rate >= 70) return '#409eff'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
})

const getResultType = (rate: number) => {
  if (rate >= 90) return 'success'
  if (rate >= 70) return 'primary'
  if (rate >= 60) return 'warning'
  return 'danger'
}

const getResultText = (rate: number) => {
  if (rate >= 90) return '优秀'
  if (rate >= 70) return '良好'
  if (rate >= 60) return '及格'
  return '不及格'
}

const getKnowledgeColor = (rate: number) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
}

const goBack = () => {
  router.push('/exam')
}

const doAgain = () => {
  router.push(`/exam/${route.params.id}`)
}

onMounted(() => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.result-container {
  margin-top: 24px;
}

.score-overview {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.score-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 32px;
}

.score-circle {
  flex-shrink: 0;
}

.score-info h2 {
  margin: 0 0 8px;
  font-size: 32px;
  color: #303133;
}

.score-info p {
  margin: 0 0 16px;
  color: #909399;
}

.stats-card {
  flex: 1.5;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.trend-card {
  margin-bottom: 24px;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
}

.chart-placeholder p {
  margin-top: 12px;
}

.wrong-questions-card,
.knowledge-card {
  margin-bottom: 24px;
}

.knowledge-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.knowledge-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.knowledge-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 24px;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 100px;
}
</style>
