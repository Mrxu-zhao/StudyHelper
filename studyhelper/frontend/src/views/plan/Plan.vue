<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <div class="section-header">
            <h2>学习计划</h2>
            <el-button type="primary" :icon="Plus" @click="goToCreate">创建计划</el-button>
          </div>

          <!-- 筛选器 -->
          <div class="filters">
            <el-radio-group v-model="statusFilter" @change="loadPlans">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="pending">未开始</el-radio-button>
              <el-radio-button label="in_progress">进行中</el-radio-button>
              <el-radio-button label="completed">已完成</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          </div>

          <!-- 计划列表 -->
          <div v-else class="plans-grid">
            <div v-for="plan in plans" :key="plan.id" class="plan-card" @click="viewPlan(plan.id)">
              <div class="plan-header">
                <el-tag :type="getStatusType(plan.status)" size="small">{{ getStatusText(plan.status) }}</el-tag>
                <el-dropdown trigger="click" @command="handleCommand($event, plan.id)">
                  <el-icon class="more-icon"><More /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="edit">编辑</el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              
              <h3>{{ plan.title }}</h3>
              <p class="plan-desc">{{ plan.description }}</p>
              
              <div class="plan-meta">
                <span><el-icon><Calendar /></el-icon> {{ plan.startDate }} ~ {{ plan.endDate }}</span>
              </div>
              
              <div class="plan-progress">
                <div class="progress-label">
                  <span>进度</span>
                  <span>{{ plan.progress }}%</span>
                </div>
                <el-progress :percentage="plan.progress" :color="getProgressColor(plan.progress)" />
              </div>
              
              <div class="plan-tasks">
                <span class="task-count">
                  <el-icon><Check /></el-icon> {{ plan.tasks.filter(t => t.completed).length }}/{{ plan.tasks.length }} 任务
                </span>
              </div>
            </div>
            
            <el-empty v-if="plans.length === 0" description="暂无学习计划" />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import { Plus, Loading, More, Calendar, Check } from '@element-plus/icons-vue'
import type { StudyPlan } from '@/types'

const router = useRouter()

const loading = ref(false)
const statusFilter = ref('')
const plans = ref<StudyPlan[]>([])

// 模拟数据
const mockPlans: StudyPlan[] = [
  {
    id: 1,
    title: '数学三角函数专项训练',
    description: '系统学习三角函数的概念、公式和综合应用',
    subjectId: 1,
    startDate: '2024-03-01',
    endDate: '2024-03-31',
    status: 'in_progress',
    progress: 65,
    tasks: [
      { id: 1, planId: 1, title: '学习弧度制', description: '', targetDate: '2024-03-05', completed: true },
      { id: 2, planId: 1, title: '掌握任意角三角函数', description: '', targetDate: '2024-03-10', completed: true },
      { id: 3, planId: 1, title: '学习诱导公式', description: '', targetDate: '2024-03-15', completed: true },
      { id: 4, planId: 1, title: '掌握和差化积公式', description: '', targetDate: '2024-03-20', completed: false },
      { id: 5, planId: 1, title: '综合练习', description: '', targetDate: '2024-03-31', completed: false }
    ]
  },
  {
    id: 2,
    title: '英语阅读理解提升计划',
    description: '每天练习两篇阅读理解，提高阅读能力',
    subjectId: 4,
    startDate: '2024-03-01',
    endDate: '2024-04-01',
    status: 'in_progress',
    progress: 40,
    tasks: [
      { id: 6, planId: 2, title: 'Day 1-7: 基础训练', description: '', targetDate: '2024-03-07', completed: true },
      { id: 7, planId: 2, title: 'Day 8-14: 技巧提升', description: '', targetDate: '2024-03-14', completed: true },
      { id: 8, planId: 2, title: 'Day 15-21: 强化训练', description: '', targetDate: '2024-03-21', completed: false },
      { id: 9, planId: 2, title: 'Day 22-30: 模拟测试', description: '', targetDate: '2024-04-01', completed: false }
    ]
  },
  {
    id: 3,
    title: '物理力学复习',
    description: '全面复习力学知识点',
    subjectId: 2,
    startDate: '2024-02-01',
    endDate: '2024-02-28',
    status: 'completed',
    progress: 100,
    tasks: [
      { id: 10, planId: 3, title: '力和运动', description: '', targetDate: '2024-02-10', completed: true },
      { id: 11, planId: 3, title: '能量守恒', description: '', targetDate: '2024-02-20', completed: true },
      { id: 12, planId: 3, title: '动量守恒', description: '', targetDate: '2024-02-28', completed: true }
    ]
  }
]

const loadPlans = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    if (statusFilter.value) {
      plans.value = mockPlans.filter(p => p.status === statusFilter.value)
    } else {
      plans.value = mockPlans
    }
  } finally {
    loading.value = false
  }
}

const goToCreate = () => {
  router.push('/plan/create')
}

const viewPlan = (id: number) => {
  router.push(`/plan/${id}`)
}

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    pending: 'info',
    in_progress: 'warning',
    completed: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '未开始',
    in_progress: '进行中',
    completed: '已完成'
  }
  return texts[status] || ''
}

const getProgressColor = (progress: number) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 50) return '#e6a23c'
  return '#409eff'
}

const handleCommand = (command: string, planId: number) => {
  switch (command) {
    case 'edit':
      ElMessage.info('编辑计划')
      break
    case 'delete':
      ElMessageBox.confirm('确定要删除这个计划吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        plans.value = plans.value.filter(p => p.id !== planId)
        ElMessage.success('删除成功')
      }).catch(() => {})
      break
  }
}

onMounted(() => {
  loadPlans()
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

.filters {
  margin-bottom: 24px;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.plan-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.more-icon {
  cursor: pointer;
  font-size: 18px;
  color: #909399;
}

.plan-card h3 {
  margin: 0 0 8px;
  color: #303133;
  font-size: 18px;
}

.plan-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.5;
}

.plan-meta {
  color: #909399;
  font-size: 14px;
  margin-bottom: 16px;
}

.plan-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.plan-progress {
  margin-bottom: 16px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.plan-tasks {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.task-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 60px;
}
</style>
