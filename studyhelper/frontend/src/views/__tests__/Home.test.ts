import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Home from '@/views/home/Home.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

// Mock router
const mockRouter = {
  push: vi.fn()
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  useRoute: () => ({ path: '/home' })
}))

// Mock stores
const mockUserStore = {
  userInfo: { id: 1, username: 'testuser', email: 'test@test.com', grade: '高一', role: 'student', createdAt: '' }
}

const mockDashboardStore = {
  stats: {
    totalStudyTime: 156,
    weeklyProgress: 78,
    planCompletion: 85,
    recentExams: [
      { id: 1, paperId: 1, paperTitle: '高一数学月考', score: 92, totalScore: 100, duration: 90, submittedAt: '2024-03-15', answers: {} }
    ],
    subjectProgress: [
      { subject: '数学', progress: 75 },
      { subject: '物理', progress: 60 }
    ]
  },
  isLoading: false,
  fetchStats: vi.fn()
}

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore
}))

vi.mock('@/stores/dashboard', () => ({
  useDashboardStore: () => mockDashboardStore
}))

// Mock components
vi.mock('@/components/Header.vue', () => ({
  default: { template: '<div class="mock-header"></div>' }
}))

vi.mock('@/components/Sidebar.vue', () => ({
  default: { template: '<div class="mock-sidebar"></div>' }
}))

describe('Home View', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    mockDashboardStore.fetchStats.mockResolvedValue(undefined)
  })

  it('should render correctly', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should have header component', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.find('.mock-header').exists()).toBe(true)
  })

  it('should have sidebar component', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.find('.mock-sidebar').exists()).toBe(true)
  })

  it('should call fetchStats on mount', async () => {
    mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    await flushPromises()
    expect(mockDashboardStore.fetchStats).toHaveBeenCalled()
  })

  it('should display welcome message with username', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    const welcomeText = wrapper.find('.welcome-text h2')
    expect(welcomeText.text()).toContain('testuser')
  })

  it('should display default username when userInfo is null', () => {
    mockUserStore.userInfo = null

    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    const welcomeText = wrapper.find('.welcome-text h2')
    expect(welcomeText.text()).toContain('同学')

    mockUserStore.userInfo = { id: 1, username: 'testuser', email: 'test@test.com', grade: '高一', role: 'student', createdAt: '' }
  })

  it('should display stats values', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    const statValue = wrapper.find('.stat-value')
    expect(statValue.text()).toBe('156')
  })

  it('should display quick action cards', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    const actionCards = wrapper.findAll('.action-card')
    expect(actionCards.length).toBeGreaterThanOrEqual(4)
  })

  it('should calculate completion color correctly', () => {
    mockDashboardStore.stats = { ...mockDashboardStore.stats, planCompletion: 85 }
    
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.vm.completionColor).toBe('#67c23a')
  })

  it('should get subject color correctly', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.vm.getSubjectColor('数学')).toBe('#409eff')
    expect(wrapper.vm.getSubjectColor('物理')).toBe('#67c23a')
    expect(wrapper.vm.getSubjectColor('未知')).toBe('#409eff')
  })

  it('should get score class correctly', () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    expect(wrapper.vm.getScoreClass(95, 100)).toBe('score-excellent')
    expect(wrapper.vm.getScoreClass(75, 100)).toBe('score-good')
    expect(wrapper.vm.getScoreClass(65, 100)).toBe('score-pass')
    expect(wrapper.vm.getScoreClass(50, 100)).toBe('score-fail')
  })

  it('should navigate to exam result on view click', async () => {
    const wrapper = mount(Home, {
      global: {
        stubs: {
          'el-row': true,
          'el-col': true,
          'el-icon': true,
          'el-progress': true,
          'el-table': true,
          'el-table-column': true,
          'el-button': true
        }
      }
    })

    wrapper.vm.viewExamResult(1)
    expect(mockRouter.push).toHaveBeenCalledWith('/exam/1/result')
  })
})
