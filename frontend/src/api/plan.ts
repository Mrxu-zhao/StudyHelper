import { get, post, put, del } from './request'
import type { StudyPlan, PlanTask, PageResult } from '@/types'

// 获取学习计划列表
export const getPlans = (params?: { status?: string; page?: number; pageSize?: number }) => {
  return get<PageResult<StudyPlan>>('/plans', params)
}

// 获取计划详情
export const getPlan = (id: number) => {
  return get<StudyPlan>(`/plans/${id}`)
}

// 创建学习计划
export const createPlan = (data: Partial<StudyPlan>) => {
  return post<StudyPlan>('/plans', data)
}

// 更新学习计划
export const updatePlan = (id: number, data: Partial<StudyPlan>) => {
  return put<StudyPlan>(`/plans/${id}`, data)
}

// 删除学习计划
export const deletePlan = (id: number) => {
  return del(`/plans/${id}`)
}

// 获取计划任务列表
export const getPlanTasks = (planId: number) => {
  return get<PlanTask[]>(`/plans/${planId}/tasks`)
}

// 创建任务
export const createTask = (planId: number, data: Partial<PlanTask>) => {
  return post<PlanTask>(`/plans/${planId}/tasks`, data)
}

// 更新任务状态
export const updateTask = (taskId: number, data: Partial<PlanTask>) => {
  return put<PlanTask>(`/tasks/${taskId}`, data)
}

// 获取日历任务
export const getCalendarTasks = (startDate: string, endDate: string) => {
  return get<PlanTask[]>('/plans/calendar', { startDate, endDate })
}

// 获取执行记录
export const getExecutionRecords = (params?: { page?: number; pageSize?: number }) => {
  return get<PageResult<PlanTask>>('/plans/records', params)
}
