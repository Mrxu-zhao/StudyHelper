import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAppStore } from '@/stores/app'

describe('App Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  afterEach(() => {
    document.documentElement.classList.toggle = vi.fn()
  })

  describe('initial state', () => {
    it('should have isSidebarCollapsed as false', () => {
      const appStore = useAppStore()
      expect(appStore.isSidebarCollapsed).toBe(false)
    })

    it('should have activeSubject as null', () => {
      const appStore = useAppStore()
      expect(appStore.activeSubject).toBeNull()
    })

    it('should have isDarkMode as false', () => {
      const appStore = useAppStore()
      expect(appStore.isDarkMode).toBe(false)
    })

    it('should have default tags', () => {
      const appStore = useAppStore()
      expect(appStore.tags).toHaveLength(5)
      expect(appStore.tags[0]).toHaveProperty('id')
      expect(appStore.tags[0]).toHaveProperty('name')
      expect(appStore.tags[0]).toHaveProperty('color')
    })
  })

  describe('toggleSidebar', () => {
    it('should toggle sidebar collapsed state', () => {
      const appStore = useAppStore()

      expect(appStore.isSidebarCollapsed).toBe(false)

      appStore.toggleSidebar()
      expect(appStore.isSidebarCollapsed).toBe(true)

      appStore.toggleSidebar()
      expect(appStore.isSidebarCollapsed).toBe(false)
    })

    it('should toggle multiple times correctly', () => {
      const appStore = useAppStore()

      appStore.toggleSidebar()
      appStore.toggleSidebar()
      appStore.toggleSidebar()

      expect(appStore.isSidebarCollapsed).toBe(true)
    })
  })

  describe('setActiveSubject', () => {
    it('should set active subject', () => {
      const appStore = useAppStore()

      appStore.setActiveSubject(1)
      expect(appStore.activeSubject).toBe(1)

      appStore.setActiveSubject(2)
      expect(appStore.activeSubject).toBe(2)
    })

    it('should set active subject to null', () => {
      const appStore = useAppStore()

      appStore.setActiveSubject(1)
      expect(appStore.activeSubject).toBe(1)

      appStore.setActiveSubject(null)
      expect(appStore.activeSubject).toBeNull()
    })
  })

  describe('toggleDarkMode', () => {
    it('should toggle dark mode state', () => {
      const appStore = useAppStore()

      expect(appStore.isDarkMode).toBe(false)

      appStore.toggleDarkMode()
      expect(appStore.isDarkMode).toBe(true)

      appStore.toggleDarkMode()
      expect(appStore.isDarkMode).toBe(false)
    })

    it('should add dark class to document when enabled', () => {
      const appStore = useAppStore()

      appStore.toggleDarkMode()

      expect(document.documentElement.classList.toggle).toHaveBeenCalledWith('dark', true)
    })

    it('should remove dark class from document when disabled', () => {
      const appStore = useAppStore()

      appStore.toggleDarkMode()
      appStore.toggleDarkMode()

      expect(document.documentElement.classList.toggle).toHaveBeenCalledWith('dark', false)
    })
  })

  describe('tags', () => {
    it('should have correct tag structure', () => {
      const appStore = useAppStore()

      appStore.tags.forEach(tag => {
        expect(tag).toHaveProperty('id')
        expect(tag).toHaveProperty('name')
        expect(tag).toHaveProperty('color')
        expect(typeof tag.id).toBe('number')
        expect(typeof tag.name).toBe('string')
        expect(typeof tag.color).toBe('string')
      })
    })

    it('should have expected subjects as tags', () => {
      const appStore = useAppStore()
      const tagNames = appStore.tags.map(t => t.name)

      expect(tagNames).toContain('数学')
      expect(tagNames).toContain('物理')
      expect(tagNames).toContain('化学')
      expect(tagNames).toContain('英语')
      expect(tagNames).toContain('语文')
    })

    it('should have unique ids', () => {
      const appStore = useAppStore()
      const ids = appStore.tags.map(t => t.id)
      const uniqueIds = new Set(ids)

      expect(uniqueIds.size).toBe(ids.length)
    })
  })
})
