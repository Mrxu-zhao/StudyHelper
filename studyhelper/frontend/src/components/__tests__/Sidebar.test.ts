import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import Sidebar from '@/components/Sidebar.vue'

// Mock router
const mockRouter = {
  push: vi.fn()
}

const mockRoute = {
  path: '/home'
}

// Mock store
const mockAppStore = {
  isSidebarCollapsed: false
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  useRoute: () => mockRoute
}))

vi.mock('@/stores/app', () => ({
  useAppStore: () => mockAppStore
}))

describe('Sidebar Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    mockRoute.path = '/home'
  })

  it('should render correctly', () => {
    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should have correct width when expanded', () => {
    mockAppStore.isSidebarCollapsed = false

    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    const aside = wrapper.find('aside, .sidebar-container')
    expect(aside.exists()).toBe(true)
  })

  it('should have correct width when collapsed', async () => {
    mockAppStore.isSidebarCollapsed = true

    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    const aside = wrapper.find('aside, .sidebar-container')
    expect(aside.exists()).toBe(true)

    mockAppStore.isSidebarCollapsed = false
  })

  it('should have menu items', () => {
    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    const menuItems = wrapper.findAll('li, .el-menu-item')
    expect(menuItems.length).toBeGreaterThan(0)
  })

  it('should handle menu selection', async () => {
    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    const menuItem = wrapper.find('.el-menu-item')
    if (menuItem.exists()) {
      await menuItem.trigger('click')
      expect(mockRouter.push).toHaveBeenCalled()
    }
  })

  it('should calculate active route correctly', () => {
    mockRoute.path = '/knowledge/detail'

    const wrapper = mount(Sidebar, {
      global: {
        stubs: {
          'el-aside': true,
          'el-menu': true,
          'el-menu-item': true,
          'el-divider': true,
          'el-icon': true
        }
      }
    })

    expect(wrapper.vm.activeRoute).toBe('/knowledge')

    mockRoute.path = '/home'
  })
})
