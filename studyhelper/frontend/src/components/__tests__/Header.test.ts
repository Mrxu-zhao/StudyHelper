import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import Header from '@/components/Header.vue'
import { Search, Bell } from '@element-plus/icons-vue'

// Mock router
const mockRouter = {
  push: vi.fn()
}

// Mock stores
const mockUserStore = {
  userInfo: { id: 1, username: 'testuser', email: 'test@test.com' },
  logout: vi.fn().mockResolvedValue(undefined)
}

const mockAppStore = {
  isSidebarCollapsed: false,
  toggleSidebar: vi.fn()
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  useRoute: () => ({ path: '/home' })
}))

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore
}))

vi.mock('@/stores/app', () => ({
  useAppStore: () => mockAppStore
}))

describe('Header Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true,
          Search,
          Bell
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should display logo text', () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    expect(wrapper.find('.logo').text()).toBe('StudyHelper')
  })

  it('should display username from store', () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    expect(wrapper.find('.username').text()).toBe('testuser')
  })

  it('should display default username when not logged in', () => {
    mockUserStore.userInfo = null

    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    expect(wrapper.find('.username').text()).toBe('用户')

    mockUserStore.userInfo = { id: 1, username: 'testuser', email: 'test@test.com' }
  })

  it('should toggle sidebar on button click', async () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    const collapseBtn = wrapper.find('.collapse-btn')
    await collapseBtn.trigger('click')

    expect(mockAppStore.toggleSidebar).toHaveBeenCalled()
  })

  it('should have search input', () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    const searchInput = wrapper.find('.search-input')
    expect(searchInput.exists()).toBe(true)
  })

  it('should handle search on enter key', async () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    const searchInput = wrapper.find('input')
    await searchInput.setValue('test search')
    await searchInput.trigger('keyup.enter')

    expect(mockRouter.push).toHaveBeenCalled()
  })

  it('should not navigate when search is empty', async () => {
    const wrapper = mount(Header, {
      global: {
        stubs: {
          'el-header': true,
          'el-icon': true,
          'el-input': true,
          'el-badge': true,
          'el-dropdown': true,
          'el-avatar': true,
          'el-dropdown-menu': true,
          'el-dropdown-item': true
        }
      }
    })

    const searchInput = wrapper.find('input')
    await searchInput.setValue('   ')
    await searchInput.trigger('keyup.enter')

    expect(mockRouter.push).not.toHaveBeenCalled()
  })
})
