import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Tag {
  id: number
  name: string
  color: string
}

export const useAppStore = defineStore('app', () => {
  const isSidebarCollapsed = ref(false)
  const activeSubject = ref<number | null>(null)
  const isDarkMode = ref(false)

  const tags = ref<Tag[]>([
    { id: 1, name: '数学', color: '#409eff' },
    { id: 2, name: '物理', color: '#67c23a' },
    { id: 3, name: '化学', color: '#e6a23c' },
    { id: 4, name: '英语', color: '#f56c6c' },
    { id: 5, name: '语文', color: '#909399' }
  ])

  const toggleSidebar = () => {
    isSidebarCollapsed.value = !isSidebarCollapsed.value
  }

  const setActiveSubject = (id: number | null) => {
    activeSubject.value = id
  }

  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
    document.documentElement.classList.toggle('dark', isDarkMode.value)
  }

  return {
    isSidebarCollapsed,
    activeSubject,
    isDarkMode,
    tags,
    toggleSidebar,
    setActiveSubject,
    toggleDarkMode
  }
})
