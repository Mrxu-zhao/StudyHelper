<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <div class="section-header">
            <h2>在线考试</h2>
            <div class="header-filters">
              <el-select v-model="subjectFilter" placeholder="选择学科" clearable style="width: 150px">
                <el-option label="数学" value="数学" />
                <el-option label="物理" value="物理" />
                <el-option label="化学" value="化学" />
                <el-option label="英语" value="英语" />
                <el-option label="语文" value="语文" />
              </el-select>
              <el-select v-model="gradeFilter" placeholder="选择年级" clearable style="width: 150px">
                <el-option label="高一" value="高一" />
                <el-option label="高二" value="高二" />
                <el-option label="高三" value="高三" />
              </el-select>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          </div>

          <!-- 试卷列表 -->
          <div v-else class="papers-grid">
            <div v-for="paper in filteredPapers" :key="paper.id" class="paper-card">
              <div class="paper-header">
                <el-tag :type="getDifficultyType(paper.difficulty)" size="small">
                  {{ getDifficultyText(paper.difficulty) }}
                </el-tag>
                <span class="paper-grade">{{ paper.grade }}</span>
              </div>
              
              <h3>{{ paper.title }}</h3>
              <p class="paper-subject">{{ paper.subject }}</p>
              
              <div class="paper-stats">
                <div class="stat">
                  <el-icon><Timer /></el-icon>
                  <span>{{ paper.duration }}分钟</span>
                </div>
                <div class="stat">
                  <el-icon><Document /></el-icon>
                  <span>{{ paper.questionCount }}题</span>
                </div>
                <div class="stat">
                  <el-icon><Star /></el-icon>
                  <span>{{ paper.totalScore }}分</span>
                </div>
              </div>
              
              <div class="paper-footer">
                <span class="paper-date">{{ formatDate(paper.createdAt) }}</span>
                <el-button type="primary" @click="startExam(paper.id)">开始考试</el-button>
              </div>
            </div>
            
            <el-empty v-if="filteredPapers.length === 0" description="暂无试卷" />
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
import { Loading, Timer, Document, Star } from '@element-plus/icons-vue'
import type { Paper } from '@/types'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const subjectFilter = ref('')
const gradeFilter = ref('')
const papers = ref<Paper[]>([])

// 模拟试卷数据
const mockPapers: Paper[] = [
  { id: 1, title: '高一数学月考', subject: '数学', grade: '高一', duration: 120, totalScore: 150, questionCount: 22, difficulty: 2, createdAt: '2024-03-15' },
  { id: 2, title: '高一物理测试', subject: '物理', grade: '高一', duration: 90, totalScore: 100, questionCount: 18, difficulty: 1, createdAt: '2024-03-12' },
  { id: 3, title: '三角函数单元测试', subject: '数学', grade: '高一', duration: 60, totalScore: 100, questionCount: 15, difficulty: 2, createdAt: '2024-03-10' },
  { id: 4, title: '英语阅读理解专项', subject: '英语', grade: '高一', duration: 60, totalScore: 50, questionCount: 10, difficulty: 2, createdAt: '2024-03-08' },
  { id: 5, title: '化学实验题测试', subject: '化学', grade: '高一', duration: 90, totalScore: 100, questionCount: 20, difficulty: 3, createdAt: '2024-03-05' },
  { id: 6, title: '文言文阅读练习', subject: '语文', grade: '高一', duration: 120, totalScore: 100, questionCount: 12, difficulty: 2, createdAt: '2024-03-01' }
]

const filteredPapers = computed(() => {
  return mockPapers.filter(paper => {
    const matchSubject = !subjectFilter.value || paper.subject === subjectFilter.value
    const matchGrade = !gradeFilter.value || paper.grade === gradeFilter.value
    return matchSubject && matchGrade
  })
})

const loadPapers = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    papers.value = mockPapers
  } finally {
    loading.value = false
  }
}

const getDifficultyType = (difficulty: number) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty: number) => {
  const texts = ['', '基础', '中等', '困难']
  return texts[difficulty] || ''
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const startExam = (id: number) => {
  router.push(`/exam/${id}`)
}

onMounted(() => {
  loadPapers()
})
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.header-filters {
  display: flex;
  gap: 12px;
}

.papers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.paper-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.paper-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.paper-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.paper-grade {
  color: #909399;
  font-size: 14px;
}

.paper-card h3 {
  margin: 0 0 4px;
  color: #303133;
  font-size: 18px;
}

.paper-subject {
  color: #409eff;
  font-size: 14px;
  margin-bottom: 16px;
}

.paper-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.paper-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.paper-date {
  color: #909399;
  font-size: 14px;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 60px;
}
</style>
