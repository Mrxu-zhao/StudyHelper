import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock axios
vi.mock('@/api/request', () => ({
  get: vi.fn(),
  post: vi.fn()
}))

import * as knowledgeApi from '@/api/knowledge'
import { get, post } from '@/api/request'

const { get: mockGet, post: mockPost } = vi.mocked({ get, post })

describe('Knowledge API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getSubjects', () => {
    it('should call get with correct endpoint', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: [
          { id: 1, name: '数学', icon: 'Math', color: '#409eff', chapterCount: 12 },
          { id: 2, name: '物理', icon: 'Atom', color: '#67c23a', chapterCount: 10 }
        ]
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await knowledgeApi.getSubjects()

      expect(mockGet).toHaveBeenCalledWith('/knowledge/subjects')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getChapters', () => {
    it('should call get with correct endpoint and subjectId', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: [
          { id: 1, subjectId: 1, name: '集合与函数', order: 1, sections: [] }
        ]
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await knowledgeApi.getChapters(1)

      expect(mockGet).toHaveBeenCalledWith('/knowledge/subjects/1/chapters')
      expect(result).toEqual(mockResponse)
    })

    it('should handle different subject ids', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: [] })

      await knowledgeApi.getChapters(2)
      expect(mockGet).toHaveBeenCalledWith('/knowledge/subjects/2/chapters')
    })
  })

  describe('getKnowledgePoint', () => {
    it('should call get with correct endpoint and id', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          id: 1,
          sectionId: 1,
          title: '集合的概念',
          content: '集合是由某些确定的对象组成的整体...',
          difficulty: 1,
          isFavorite: false,
          tags: ['基础']
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await knowledgeApi.getKnowledgePoint(1)

      expect(mockGet).toHaveBeenCalledWith('/knowledge/points/1')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('favoritePoint', () => {
    it('should call post with correct endpoint', async () => {
      const mockResponse = { code: 200, message: 'success', data: null }
      mockPost.mockResolvedValue(mockResponse)

      await knowledgeApi.favoritePoint(1)

      expect(mockPost).toHaveBeenCalledWith('/knowledge/points/1/favorite')
    })
  })

  describe('unfavoritePoint', () => {
    it('should call post with correct endpoint', async () => {
      const mockResponse = { code: 200, message: 'success', data: null }
      mockPost.mockResolvedValue(mockResponse)

      await knowledgeApi.unfavoritePoint(1)

      expect(mockPost).toHaveBeenCalledWith('/knowledge/points/1/unfavorite')
    })
  })

  describe('getFavorites', () => {
    it('should call get with default pagination', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          list: [{ id: 1, title: '收藏1' }],
          total: 1,
          page: 1,
          pageSize: 10
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await knowledgeApi.getFavorites()

      expect(mockGet).toHaveBeenCalledWith('/knowledge/favorites', { page: 1, pageSize: 10 })
      expect(result).toEqual(mockResponse)
    })

    it('should call get with custom pagination', async () => {
      mockGet.mockResolvedValue({ code: 200, message: 'success', data: { list: [], total: 0, page: 2, pageSize: 20 } })

      await knowledgeApi.getFavorites(2, 20)

      expect(mockGet).toHaveBeenCalledWith('/knowledge/favorites', { page: 2, pageSize: 20 })
    })
  })

  describe('searchPoints', () => {
    it('should call get with keyword and pagination', async () => {
      const mockResponse = {
        code: 200,
        message: 'success',
        data: {
          list: [{ id: 1, title: '函数' }],
          total: 1,
          page: 1,
          pageSize: 10
        }
      }
      mockGet.mockResolvedValue(mockResponse)

      const result = await knowledgeApi.searchPoints('函数')

      expect(mockGet).toHaveBeenCalledWith('/knowledge/search', { keyword: '函数', page: 1, pageSize: 10 })
      expect(result).toEqual(mockResponse)
    })
  })
})
