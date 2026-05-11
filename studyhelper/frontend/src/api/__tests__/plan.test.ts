import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock axios
vi.mock('@/api/request', () => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn()
}))

import * as planApi from '@/api/plan'
import { get, post, put, del } from '@/api/request'

const { get: mockGet, post: mockPost, put: mockPut, del: mockDel } = vi.mocked({ get, post, put, del })

describe('Plan API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getPlans', () => {
    it('should call get with plans endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          list: [{ id: 1, title: '学习计划1' }],
          total: 1,
          page: 1,
          pageSize: 10
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await planApi.getPlans()

      expect(mockGet).toHaveBeenCalledWith('/plans', undefined)
      expect(result).toEqual(mockResponse)
    })

    it('should call get with params when provided', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: { list: [], total: 0, page: 1, pageSize: 10 } })

      await planApi.getPlans({ status: 'in_progress', page: 2 })

      expect(mockGet).toHaveBeenCalledWith('/plans', { status: 'in_progress', page: 2 })
    })
  })

  describe('getPlan', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, title: '学习计划1', description: '描述' }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await planApi.getPlan(1)

      expect(mockGet).toHaveBeenCalledWith('/plans/1')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('createPlan', () => {
    it('should call post with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, title: '新计划' }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await planApi.createPlan({ title: '新计划', description: '描述' })

      expect(mockPost).toHaveBeenCalledWith('/plans', { title: '新计划', description: '描述' })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('updatePlan', () => {
    it('should call put with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, title: '更新后的计划' }
      }
      mockPut.mockResolvedValue(mockResponse)

      const result = await planApi.updatePlan(1, { title: '更新后的计划' })

      expect(mockPut).toHaveBeenCalledWith('/plans/1', { title: '更新后的计划' })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('deletePlan', () => {
    it('should call del with correct endpoint', async () => {
      const mockResponse = { code: 200, message: 'success', data: null }
      mockDel.mockResolvedValue(mockResponse)

      await planApi.deletePlan(1)

      expect(mockDel).toHaveBeenCalledWith('/plans/1')
    })
  })

  describe('getPlanTasks', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: [
          { id: 1, planId: 1, title: '任务1', completed: false }
        ]
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await planApi.getPlanTasks(1)

      expect(mockGet).toHaveBeenCalledWith('/plans/1/tasks')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('createTask', () => {
    it('should call post with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, planId: 1, title: '新任务' }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await planApi.createTask(1, { title: '新任务', description: '描述' })

      expect(mockPost).toHaveBeenCalledWith('/plans/1/tasks', { title: '新任务', description: '描述' })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('updateTask', () => {
    it('should call put with correct endpoint and data', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, completed: true }
      }
      mockPut.mockResolvedValue(mockResponse)

      const result = await planApi.updateTask(1, { completed: true })

      expect(mockPut).toHaveBeenCalledWith('/tasks/1', { completed: true })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getCalendarTasks', () => {
    it('should call get with date range', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: [{ id: 1, targetDate: '2024-03-15' }]
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await planApi.getCalendarTasks('2024-03-01', '2024-03-31')

      expect(mockGet).toHaveBeenCalledWith('/plans/calendar', { startDate: '2024-03-01', endDate: '2024-03-31' })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getExecutionRecords', () => {
    it('should call get with default pagination', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { list: [], total: 0, page: 1, pageSize: 10 }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await planApi.getExecutionRecords()

      expect(mockGet).toHaveBeenCalledWith('/plans/records', undefined)
      expect(result).toEqual(mockResponse)
    })

    it('should call get with custom params', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: { list: [], total: 0, page: 2, pageSize: 20 } })

      await planApi.getExecutionRecords({ page: 2, pageSize: 20 })

      expect(mockGet).toHaveBeenCalledWith('/plans/records', { page: 2, pageSize: 20 })
    })
  })
})
