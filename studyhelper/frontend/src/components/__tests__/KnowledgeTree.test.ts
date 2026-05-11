import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import KnowledgeTree from '@/components/KnowledgeTree.vue'
import type { Chapter } from '@/types'

// Mock icons
vi.mock('@element-plus/icons-vue', () => ({
  Loading: { template: '<span class="loading-icon"></span>' },
  WarningFilled: { template: '<span class="warning-icon"></span>' },
  Folder: { template: '<span class="folder-icon"></span>' },
  FolderOpened: { template: '<span class="folder-opened-icon"></span>' },
  Document: { template: '<span class="document-icon"></span>' }
}))

describe('KnowledgeTree Component', () => {
  const mockChapters: Chapter[] = [
    {
      id: 1,
      subjectId: 1,
      name: '第一章：集合与函数',
      order: 1,
      sections: [
        {
          id: 1,
          chapterId: 1,
          name: '1.1 集合',
          order: 1,
          knowledgePoints: [
            { id: 1, sectionId: 1, title: '集合的概念', content: '内容1', difficulty: 1, isFavorite: false, tags: ['基础'] },
            { id: 2, sectionId: 1, title: '集合的运算', content: '内容2', difficulty: 2, isFavorite: true, tags: ['重点'] }
          ]
        }
      ]
    },
    {
      id: 2,
      subjectId: 1,
      name: '第二章：三角函数',
      order: 2,
      sections: [
        {
          id: 2,
          chapterId: 2,
          name: '2.1 三角函数基础',
          order: 1,
          knowledgePoints: [
            { id: 3, sectionId: 2, title: '弧度制', content: '内容3', difficulty: 1, isFavorite: false, tags: ['基础'] }
          ]
        }
      ]
    }
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should show loading state', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: [],
        loading: true,
        error: ''
      }
    })

    expect(wrapper.find('.loading-container').exists()).toBe(true)
    expect(wrapper.find('.loading-icon').exists()).toBe(true)
  })

  it('should show error state', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: [],
        loading: false,
        error: '加载失败'
      }
    })

    expect(wrapper.find('.error-container').exists()).toBe(true)
    expect(wrapper.find('.error-container span').text()).toBe('加载失败')
  })

  it('should show retry button in error state', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: [],
        loading: false,
        error: '加载失败'
      }
    })

    const retryBtn = wrapper.find('.error-container button')
    expect(retryBtn.exists()).toBe(true)
  })

  it('should emit retry event on button click', async () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: [],
        loading: false,
        error: '加载失败'
      }
    })

    const retryBtn = wrapper.find('.error-container button')
    await retryBtn.trigger('click')

    expect(wrapper.emitted('retry')).toBeTruthy()
  })

  it('should render tree structure', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    expect(wrapper.find('.el-tree').exists()).toBe(true)
  })

  it('should have correct tree data', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    const treeData = wrapper.vm.treeData
    expect(treeData).toHaveLength(2)
    expect(treeData[0]).toHaveProperty('id', 1)
    expect(treeData[0]).toHaveProperty('type', 'chapter')
    expect(treeData[0]).toHaveProperty('name', '第一章：集合与函数')
  })

  it('should have correct tree data with nested sections', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    const treeData = wrapper.vm.treeData
    expect(treeData[0].sections).toHaveLength(1)
    expect(treeData[0].sections[0].name).toBe('1.1 集合')
  })

  it('should have correct tree data with knowledge points', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    const treeData = wrapper.vm.treeData
    expect(treeData[0].sections[0].children).toHaveLength(2)
    expect(treeData[0].sections[0].children[0].type).toBe('point')
    expect(treeData[0].sections[0].children[0].name).toBe('集合的概念')
  })

  it('should set default expanded keys', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    expect(wrapper.vm.expandedKeys).toContain(1)
  })

  it('should emit nodeClick event', async () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: mockChapters,
        loading: false,
        error: ''
      }
    })

    // Simulate node click by triggering the tree
    wrapper.vm.handleNodeClick({ id: 1, type: 'chapter' })

    expect(wrapper.emitted('nodeClick')).toBeTruthy()
  })

  describe('difficulty helpers', () => {
    it('should get correct difficulty type', () => {
      const wrapper = mount(KnowledgeTree, {
        props: {
          chapters: mockChapters,
          loading: false,
          error: ''
        }
      })

      expect(wrapper.vm.getDifficultyType(1)).toBe('success')
      expect(wrapper.vm.getDifficultyType(2)).toBe('warning')
      expect(wrapper.vm.getDifficultyType(3)).toBe('danger')
      expect(wrapper.vm.getDifficultyType(4)).toBe('info')
    })

    it('should get correct difficulty text', () => {
      const wrapper = mount(KnowledgeTree, {
        props: {
          chapters: mockChapters,
          loading: false,
          error: ''
        }
      })

      expect(wrapper.vm.getDifficultyText(1)).toBe('基础')
      expect(wrapper.vm.getDifficultyText(2)).toBe('中等')
      expect(wrapper.vm.getDifficultyText(3)).toBe('困难')
      expect(wrapper.vm.getDifficultyText(0)).toBe('')
    })
  })

  it('should handle empty chapters', () => {
    const wrapper = mount(KnowledgeTree, {
      props: {
        chapters: [],
        loading: false,
        error: ''
      }
    })

    expect(wrapper.vm.treeData).toHaveLength(0)
  })
})
