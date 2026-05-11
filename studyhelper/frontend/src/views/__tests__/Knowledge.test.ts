import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Knowledge from '@/views/knowledge/Knowledge.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

// Mock router
const mockRouter = {
  push: vi.fn()
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

// Mock components
vi.mock('@/components/Header.vue', () => ({
  default: { template: '<div class="mock-header"></div>' }
}))

vi.mock('@/components/Sidebar.vue', () => ({
  default: { template: '<div class="mock-sidebar"></div>' }
}))

vi.mock('@/components/KnowledgeTree.vue', () => ({
  default: {
    template: '<div class="mock-knowledge-tree"></div>',
    props: ['chapters', 'loading', 'error']
  }
}))

describe('Knowledge View', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should have header component', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.find('.mock-header').exists()).toBe(true)
  })

  it('should have sidebar component', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.find('.mock-sidebar').exists()).toBe(true)
  })

  it('should display page title', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.find('h2').text()).toBe('知识库')
  })

  it('should have search input', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    const searchInput = wrapper.find('input')
    expect(searchInput.exists()).toBe(true)
  })

  it('should have refresh button', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    const buttons = wrapper.findAllComponents({ name: 'ElButton' })
    const refreshBtn = buttons.find(btn => btn.text().includes('刷新'))
    expect(refreshBtn).toBeDefined()
  })

  it('should display subject cards', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    await flushPromises()

    const subjectCards = wrapper.findAll('.subject-card')
    expect(subjectCards.length).toBe(6) // 6 subjects in mock data
  })

  it('should show empty state initially', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.find('.el-empty').exists()).toBe(true)
  })

  it('should select subject on card click', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    await flushPromises()

    const firstCard = wrapper.find('.subject-card')
    await firstCard.trigger('click')

    await flushPromises()

    expect(wrapper.vm.activeSubject).toBe(1)
    expect(firstCard.classes()).toContain('active')
  })

  it('should load chapters when subject is selected', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    await flushPromises()

    const firstCard = wrapper.find('.subject-card')
    await firstCard.trigger('click')

    await flushPromises()

    expect(wrapper.vm.chapters.length).toBeGreaterThan(0)
  })

  it('should emit nodeClick when tree node is clicked', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    await flushPromises()

    // Select a subject first
    const firstCard = wrapper.find('.subject-card')
    await firstCard.trigger('click')

    await flushPromises()

    // Get the tree component and emit event
    const tree = wrapper.find('.mock-knowledge-tree')
    await tree.trigger('nodeClick', { id: 1, type: 'point' })
  })

  it('should handle search', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    const searchInput = wrapper.find('input')
    await searchInput.setValue('函数')
    await searchInput.trigger('keyup.enter')

    expect(mockRouter.push).toHaveBeenCalled()
  })

  it('should not search when keyword is empty', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    const searchInput = wrapper.find('input')
    await searchInput.setValue('   ')
    await searchInput.trigger('keyup.enter')

    expect(mockRouter.push).not.toHaveBeenCalled()
  })

  it('should get subject icon correctly', () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    expect(wrapper.vm.getSubjectIcon('Math')).toBeDefined()
    expect(wrapper.vm.getSubjectIcon('Atom')).toBeDefined()
    expect(wrapper.vm.getSubjectIcon('Unknown')).toBeDefined()
  })

  it('should hide empty state when subject is selected', async () => {
    const wrapper = mount(Knowledge, {
      global: {
        stubs: {
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true
        }
      }
    })

    await flushPromises()

    const firstCard = wrapper.find('.subject-card')
    await firstCard.trigger('click')

    await flushPromises()

    expect(wrapper.find('.el-empty').exists()).toBe(false)
  })
})
