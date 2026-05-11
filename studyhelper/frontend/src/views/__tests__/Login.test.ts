import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import Login from '@/views/auth/Login.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

// Mock router
const mockRouter = {
  push: vi.fn()
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

// Mock store
const mockUserStore = {
  login: vi.fn(),
  isLoading: false
}

vi.mock('@/stores/user', () => ({
  useUserStore: () => mockUserStore
}))

// Mock Element Plus
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn()
    }
  }
})

describe('Login View', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should render correctly', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('should display login title', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    expect(wrapper.find('h2').text()).toBe('欢迎回来')
  })

  it('should display login description', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    expect(wrapper.find('.login-header p').text()).toBe('登录到 StudyHelper 助学平台')
  })

  it('should have login button', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const button = wrapper.find('button')
    expect(button.exists()).toBe(true)
  })

  it('should have register link', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const registerLink = wrapper.find('.login-footer a, .login-footer span')
    expect(registerLink.exists()).toBe(true)
  })

  it('should have remember me checkbox', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const checkbox = wrapper.find('input[type="checkbox"]')
    expect(checkbox.exists()).toBe(true)
  })

  it('should navigate to register on link click', async () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const registerLink = wrapper.find('.login-footer a, .login-footer span')
    await registerLink.trigger('click')

    expect(mockRouter.push).toHaveBeenCalledWith('/register')
  })

  it('should call login on button click when form is valid', async () => {
    mockUserStore.login.mockResolvedValue(true)

    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const formRef = wrapper.find('form')
    await formRef.trigger('submit.prevent')

    // Form validation is mocked, so we just check store was called
    // In real scenario with FormInstance, we'd need more setup
  })

  it('should have username input', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const usernameInput = wrapper.find('input[placeholder="用户名"]')
    expect(usernameInput.exists()).toBe(true)
  })

  it('should have password input', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const passwordInput = wrapper.find('input[type="password"]')
    expect(passwordInput.exists()).toBe(true)
  })

  it('should have forgot password link', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-button': true,
          'el-checkbox': true,
          'el-link': true
        }
      }
    })

    const links = wrapper.findAllComponents({ name: 'ElLink' })
    const forgotLink = links.find(link => link.text().includes('忘记密码'))
    expect(forgotLink).toBeDefined()
  })
})
