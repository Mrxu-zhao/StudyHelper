import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/stores/user'

// Mock API modules
vi.mock('@/api/user', () => ({
  login: vi.fn(),
  register: vi.fn(),
  logout: vi.fn(),
  getCurrentUser: vi.fn()
}))

import { login as apiLogin, register as apiRegister, logout as apiLogout, getCurrentUser } from '@/api/user'

const mockApiLogin = apiLogin as ReturnType<typeof vi.fn>
const mockApiRegister = apiRegister as ReturnType<typeof vi.fn>
const mockApiLogout = apiLogout as ReturnType<typeof vi.fn>
const mockGetCurrentUser = getCurrentUser as ReturnType<typeof vi.fn>

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('initial state', () => {
    it('should have empty token initially', () => {
      const userStore = useUserStore()
      expect(userStore.token).toBe('')
    })

    it('should have null userInfo initially', () => {
      const userStore = useUserStore()
      expect(userStore.userInfo).toBeNull()
    })

    it('should have isLoading as false initially', () => {
      const userStore = useUserStore()
      expect(userStore.isLoading).toBe(false)
    })
  })

  describe('setToken', () => {
    it('should set token and save to localStorage', () => {
      const userStore = useUserStore()
      userStore.setToken('test-token-123')

      expect(userStore.token).toBe('test-token-123')
      expect(localStorage.setItem).toHaveBeenCalledWith('token', 'test-token-123')
    })
  })

  describe('setUserInfo', () => {
    it('should set user info', () => {
      const userStore = useUserStore()
      const userInfo = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        grade: '高一',
        role: 'student' as const,
        createdAt: '2024-01-01'
      }

      userStore.setUserInfo(userInfo)

      expect(userStore.userInfo).toEqual(userInfo)
    })
  })

  describe('login', () => {
    it('should login successfully', async () => {
      const userStore = useUserStore()
      const mockUser = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com',
        grade: '高一',
        role: 'student' as const,
        createdAt: '2024-01-01'
      }

      mockApiLogin.mockResolvedValue({
        code: 200,
        message: 'success',
        data: mockUser
      })

      const result = await userStore.login('testuser', 'password123')

      expect(result).toBe(true)
      expect(userStore.userInfo).toEqual(mockUser)
      expect(userStore.isLoading).toBe(false)
    })

    it('should handle login failure', async () => {
      const userStore = useUserStore()
      mockApiLogin.mockRejectedValue(new Error('Invalid credentials'))

      const result = await userStore.login('wrong', 'wrong')

      expect(result).toBe(false)
      expect(userStore.isLoading).toBe(false)
    })

    it('should set isLoading to true during login', async () => {
      const userStore = useUserStore()
      mockApiLogin.mockImplementation(() => new Promise(resolve => setTimeout(() => resolve({
        code: 200,
        message: 'success',
        data: { id: 1, username: 'test', email: 'test@test.com', grade: '高一', role: 'student' as const, createdAt: '' }
      }), 100)))

      const loginPromise = userStore.login('test', 'test')

      expect(userStore.isLoading).toBe(true)

      await loginPromise
      expect(userStore.isLoading).toBe(false)
    })
  })

  describe('register', () => {
    it('should register successfully', async () => {
      const userStore = useUserStore()
      const registerData = {
        username: 'newuser',
        password: 'password123',
        email: 'new@example.com',
        grade: '高二'
      }

      mockApiRegister.mockResolvedValue({
        code: 200,
        message: 'success',
        data: { id: 2, ...registerData, role: 'student' as const, createdAt: '2024-01-01' }
      })

      const result = await userStore.register(registerData)

      expect(result).toBe(true)
      expect(mockApiRegister).toHaveBeenCalledWith(registerData)
    })

    it('should handle register failure', async () => {
      const userStore = useUserStore()
      mockApiRegister.mockRejectedValue(new Error('Registration failed'))

      const result = await userStore.register({
        username: 'test',
        password: 'test',
        email: 'test@test.com',
        grade: '高一'
      })

      expect(result).toBe(false)
    })
  })

  describe('logout', () => {
    it('should logout successfully and clear state', async () => {
      const userStore = useUserStore()
      userStore.setToken('test-token')
      userStore.setUserInfo({
        id: 1,
        username: 'test',
        email: 'test@test.com',
        grade: '高一',
        role: 'student',
        createdAt: ''
      })

      mockApiLogout.mockResolvedValue({ code: 200, message: 'success', data: null })

      await userStore.logout()

      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
      expect(localStorage.removeItem).toHaveBeenCalledWith('token')
    })

    it('should clear state even if API fails', async () => {
      const userStore = useUserStore()
      userStore.setToken('test-token')

      mockApiLogout.mockRejectedValue(new Error('Logout failed'))

      await userStore.logout()

      expect(userStore.token).toBe('')
    })
  })

  describe('fetchUserInfo', () => {
    it('should fetch user info when token exists', async () => {
      const userStore = useUserStore()
      userStore.setToken('test-token')

      const mockUser = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      }
      mockGetCurrentUser.mockResolvedValue({
        code: 200,
        message: 'success',
        data: mockUser
      })

      await userStore.fetchUserInfo()

      expect(mockGetCurrentUser).toHaveBeenCalled()
      expect(userStore.userInfo).toEqual(mockUser)
    })

    it('should not fetch when no token', async () => {
      const userStore = useUserStore()
      // token is empty by default

      await userStore.fetchUserInfo()

      expect(mockGetCurrentUser).not.toHaveBeenCalled()
    })
  })

  describe('isLoggedIn', () => {
    it('should return true when token exists', () => {
      const userStore = useUserStore()
      userStore.setToken('test-token')

      expect(userStore.isLoggedIn()).toBe(true)
    })

    it('should return false when no token', () => {
      const userStore = useUserStore()

      expect(userStore.isLoggedIn()).toBe(false)
    })
  })
})
