import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { DashboardStats } from '@/types'

export const useDashboardStore = defineStore('dashboard', () => {
  const stats = ref<DashboardStats | null>(null)
  const isLoading = ref(false)

  // 模拟数据
  const mockStats: DashboardStats = {
    totalStudyTime: 156,
    weeklyProgress: 78,
    planCompletion: 85,
    recentExams: [
      { id: 1, paperId: 1, paperTitle: '高一数学月考', score: 92, totalScore: 100, duration: 90, submittedAt: '2024-03-15', answers: {} },
      { id: 2, paperId: 2, paperTitle: '高一物理测试', score: 78, totalScore: 100, duration: 85, submittedAt: '2024-03-12', answers: {} }
    ],
    subjectProgress: [
      { subject: '数学', progress: 75 },
      { subject: '物理', progress: 60 },
      { subject: '化学', progress: 45 },
      { subject: '英语', progress: 82 }
    ]
  }

  const fetchStats = async () => {
    isLoading.value = true
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500))
      stats.value = mockStats
    } finally {
      isLoading.value = false
    }
  }

  return {
    stats,
    isLoading,
    fetchStats
  }
})
