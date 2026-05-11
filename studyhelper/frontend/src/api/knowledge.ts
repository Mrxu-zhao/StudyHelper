import { get, post } from './request'
import type { Subject, Chapter, KnowledgePoint, PageResult } from '@/types'

// 获取学科列表
export const getSubjects = () => {
  return get<Subject[]>('/knowledge/subjects')
}

// 获取学科章节树
export const getChapters = (subjectId: number, grade?: number) => {
  return get<Chapter[]>('/knowledge/chapters', { subjectId, grade })
}

// 获取知识点详情
export const getKnowledgePoint = (id: number) => {
  return get<KnowledgePoint>(`/knowledge/points/${id}`)
}

// 收藏知识点
export const favoritePoint = (id: number) => {
  return post(`/knowledge/points/${id}/favorite`)
}

// 取消收藏
export const unfavoritePoint = (id: number) => {
  return post(`/knowledge/points/${id}/unfavorite`)
}

// 获取收藏列表
export const getFavorites = (page = 1, pageSize = 10) => {
  return get<PageResult<KnowledgePoint>>('/knowledge/favorites', { page, pageSize })
}

// 搜索知识点
export const searchPoints = (keyword: string, page = 1, pageSize = 10) => {
  return get<PageResult<KnowledgePoint>>('/knowledge/search', { keyword, page, pageSize })
}
