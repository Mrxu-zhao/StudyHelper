import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// Mock axios
vi.mock('@/api/request', () => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn()
}))

// Import after mocking
import * as userApi from '@/api/user'
import { get, post } from '@/api/request'

const { get: mockGet, post: mockPost } = vi.mocked({ get, post })

describe('User API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('should call post with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          grade: '高一',
          role: 'student' as const
        }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await userApi.login({
        username: 'testuser',
        password: 'password123'
      })

      expect(mockPost).toHaveBeenCalledWith('/auth/login', {
        username: 'testuser',
        password: 'password123'
      })
      expect(result).toEqual(mockResponse)
    })

    it('should handle login error', async () => {
      mockPost.mockRejectedValue(new Error('Invalid credentials'))

      await expect(
        userApi.login({ username: 'wrong', password: 'wrong' })
      ).rejects.toThrow('Invalid credentials')
    })
  })

  describe('register', () => {
    it('should call post with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          id: 2,
          username: 'newuser',
          email: 'new@example.com',
          grade: '高二',
          role: 'student' as const
        }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await userApi.register({
        username: 'newuser',
        password: 'password123',
        email: 'new@example.com',
        grade: '高二'
      })

      expect(mockPost).toHaveBeenCalledWith('/auth/register', {
        username: 'newuser',
        password: 'password123',
        email: 'new@example.com',
        grade: '高二'
      })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getCurrentUser', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com'
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await userApi.getCurrentUser()

      expect(mockGet).toHaveBeenCalledWith('/auth/current')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('logout', () => {
    it('should call post with correct endpoint', async () => {
      const mockResponse = { code: 200, message: 'success', data: null }
      mockPost.mockResolvedValue(mockResponse)

      await userApi.logout()

      expect(mockPost).toHaveBeenCalledWith('/auth/logout')
    })
  })

  describe('updateUser', () => {
    it('should call post with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, username: 'updated' }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await userApi.updateUser({ username: 'updated' })

      expect(mockPost).toHaveBeenCalledWith('/auth/update', { username: 'updated' })
      expect(result).toEqual(mockResponse)
    })
  })
})
