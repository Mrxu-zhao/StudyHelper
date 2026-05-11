import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock axios
vi.mock('@/api/request', () => ({
  get: vi.fn(),
  post: vi.fn()
}))

import * as paperApi from '@/api/paper'
import { get, post } from '@/api/request'

const { get: mockGet, post: mockPost } = vi.mocked({ get, post })

describe('Paper API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getPapers', () => {
    it('should call get with papers endpoint without params', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          list: [{ id: 1, title: '高一数学月考' }],
          total: 1,
          page: 1,
          pageSize: 10
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await paperApi.getPapers()

      expect(mockGet).toHaveBeenCalledWith('/papers', undefined)
      expect(result).toEqual(mockResponse)
    })

    it('should call get with filter params', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: { list: [], total: 0, page: 1, pageSize: 10 } })

      await paperApi.getPapers({ subject: '数学', grade: '高一' })

      expect(mockGet).toHaveBeenCalledWith('/papers', { subject: '数学', grade: '高一' })
    })
  })

  describe('getPaper', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          paper: { id: 1, title: '高一数学月考', totalScore: 100 },
          questions: [{ id: 1, type: 'choice', content: '题目1' }]
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await paperApi.getPaper(1)

      expect(mockGet).toHaveBeenCalledWith('/papers/1')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('startExam', () => {
    it('should call post with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, paperId: 1, paperTitle: '考试1', score: 0, totalScore: 100, duration: 0, submittedAt: '', answers: {} }
      }
      mockPost.mockResolvedValue(mockResponse)

      const result = await paperApi.startExam(1)

      expect(mockPost).toHaveBeenCalledWith('/papers/1/start')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('submitExam', () => {
    it('should call post with answers', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { id: 1, paperId: 1, paperTitle: '考试1', score: 85, totalScore: 100, duration: 90, submittedAt: '2024-03-15', answers: { 1: 'A' } }
      }
      mockPost.mockResolvedValue(mockResponse)

      const answers = { 1: 'A', 2: 'B', 3: 'C' }
      const result = await paperApi.submitExam(1, answers)

      expect(mockPost).toHaveBeenCalledWith('/exams/1/submit', { answers })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getExamRecords', () => {
    it('should call get with default pagination', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: { list: [], total: 0, page: 1, pageSize: 10 }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await paperApi.getExamRecords()

      expect(mockGet).toHaveBeenCalledWith('/exams/records', undefined)
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getExamAnalysis', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          score: 85,
          totalScore: 100,
          correctRate: 85,
          wrongQuestions: [],
          knowledgeAnalysis: [{ name: '集合', correctRate: 80 }]
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await paperApi.getExamAnalysis(1)

      expect(mockGet).toHaveBeenCalledWith('/exams/1/analysis')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getScoreTrend', () => {
    it('should call get without subject filter', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: [
          { date: '2024-03-01', score: 80 },
          { date: '2024-03-08', score: 85 },
          { date: '2024-03-15', score: 90 }
        ]
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await paperApi.getScoreTrend()

      expect(mockGet).toHaveBeenCalledWith('/exams/trend', { subject: undefined })
      expect(result).toEqual(mockResponse)
    })

    it('should call get with subject filter', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: [] })

      await paperApi.getScoreTrend('数学')

      expect(mockGet).toHaveBeenCalledWith('/exams/trend', { subject: '数学' })
    })
  })
})
