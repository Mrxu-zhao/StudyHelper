import { get, post } from './request'
import type { Paper, Question, ExamRecord, PageResult } from '@/types'

// 获取试卷列表
export const getPapers = (params?: { subject?: string; grade?: string; page?: number; pageSize?: number }) => {
  return get<PageResult<Paper>>('/papers', params)
}

// 获取试卷详情（包含题目）
export const getPaper = (id: number) => {
  return get<{ paper: Paper; questions: Question[] }>(`/papers/${id}`)
}

// 开始考试
export const startExam = (paperId: number) => {
  return post<ExamRecord>(`/papers/${paperId}/start`)
}

// 提交答案
export const submitExam = (examId: number, answers: Record<number, string>) => {
  return post<ExamRecord>(`/exams/${examId}/submit`, { answers })
}

// 获取考试记录
export const getExamRecords = (params?: { page?: number; pageSize?: number }) => {
  return get<PageResult<ExamRecord>>('/exams/records', params)
}

// 获取成绩分析
export const getExamAnalysis = (examId: number) => {
  return get<{
    score: number
    totalScore: number
    correctRate: number
    wrongQuestions: Question[]
    knowledgeAnalysis: { name: string; correctRate: number }[]
  }>(`/exams/${examId}/analysis`)
}

// 获取历史成绩趋势
export const getScoreTrend = (subject?: string) => {
  return get<{ date: string; score: number }[]>(`/exams/trend`, { subject })
}
