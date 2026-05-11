import { config } from '@vue/test-utils'
import { vi } from 'vitest'

// Global test setup
globalThis.fetch = vi.fn()

// Mock Element Plus components
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
      warning: vi.fn(),
      info: vi.fn()
    }
  }
})

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}
vi.stubGlobal('localStorage', localStorageMock)

// Mock document
Object.defineProperty(document, 'documentElement', {
  value: {
    classList: {
      toggle: vi.fn()
    }
  },
  writable: true
})
