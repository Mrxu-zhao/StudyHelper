import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useDashboardStore } from '@/stores/dashboard'

describe('Dashboard Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have null stats initially', () => {
      const dashboardStore = useDashboardStore()
      expect(dashboardStore.stats).toBeNull()
    })

    it('should have isLoading as false initially', () => {
      const dashboardStore = useDashboardStore()
      expect(dashboardStore.isLoading).toBe(false)
    })
  })

  describe('fetchStats', () => {
    it('should fetch stats successfully', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()

      expect(dashboardStore.stats).not.toBeNull()
      expect(dashboardStore.isLoading).toBe(false)
      expect(dashboardStore.stats).toHaveProperty('totalStudyTime')
      expect(dashboardStore.stats).toHaveProperty('weeklyProgress')
      expect(dashboardStore.stats).toHaveProperty('planCompletion')
    })

    it('should set isLoading to true during fetch', async () => {
      const dashboardStore = useDashboardStore()

      const fetchPromise = dashboardStore.fetchStats()
      
      expect(dashboardStore.isLoading).toBe(true)

      await fetchPromise
      expect(dashboardStore.isLoading).toBe(false)
    })

    it('should have correct stats structure', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()

      const stats = dashboardStore.stats
      expect(stats).toHaveProperty('totalStudyTime', 156)
      expect(stats).toHaveProperty('weeklyProgress', 78)
      expect(stats).toHaveProperty('planCompletion', 85)
      expect(stats).toHaveProperty('recentExams')
      expect(stats).toHaveProperty('subjectProgress')
    })

    it('should have recent exams with correct structure', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()

      const exams = dashboardStore.stats?.recentExams
      expect(exams).toHaveLength(2)
      expect(exams?.[0]).toHaveProperty('id')
      expect(exams?.[0]).toHaveProperty('paperId')
      expect(exams?.[0]).toHaveProperty('paperTitle')
      expect(exams?.[0]).toHaveProperty('score')
      expect(exams?.[0]).toHaveProperty('totalScore')
    })

    it('should have subject progress with correct structure', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()

      const progress = dashboardStore.stats?.subjectProgress
      expect(progress).toHaveLength(4)
      expect(progress?.[0]).toHaveProperty('subject')
      expect(progress?.[0]).toHaveProperty('progress')
    })

    it('should have multiple subject progress entries', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()

      const progress = dashboardStore.stats?.subjectProgress
      const subjects = progress?.map(p => p.subject)
      expect(subjects).toContain('数学')
      expect(subjects).toContain('物理')
      expect(subjects).toContain('化学')
      expect(subjects).toContain('英语')
    })
  })

  describe('stats updates', () => {
    it('should update stats on multiple fetches', async () => {
      const dashboardStore = useDashboardStore()

      await dashboardStore.fetchStats()
      const firstStats = dashboardStore.stats

      await dashboardStore.fetchStats()
      const secondStats = dashboardStore.stats

      expect(firstStats).toEqual(secondStats)
    })
  })
})
