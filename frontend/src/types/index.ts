// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  grade: string
  role: 'student' | 'teacher' | 'admin'
  createdAt: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  email: string
  grade: string
}

// 知识库相关类型
export interface Subject {
  id: number
  name: string
  icon: string
  color: string
  chapterCount: number
}

export interface Chapter {
  id: number
  subjectId: number
  name: string
  order: number
  sections: Section[]
}

export interface Section {
  id: number
  chapterId: number
  name: string
  order: number
  knowledgePoints: KnowledgePoint[]
}

export interface KnowledgePoint {
  id: number
  sectionId: number
  title: string
  content: string
  difficulty: 1 | 2 | 3
  isFavorite: boolean
  tags: string[]
}

// 学习计划相关类型
export interface StudyPlan {
  id: number
  title: string
  description: string
  subjectId: number
  startDate: string
  endDate: string
  status: 'pending' | 'in_progress' | 'completed'
  progress: number
  tasks: PlanTask[]
}

export interface PlanTask {
  id: number
  planId: number
  title: string
  description: string
  targetDate: string
  completed: boolean
  completedAt?: string
}

// 试卷相关类型
export interface Paper {
  id: number
  title: string
  subject: string
  grade: string
  duration: number
  totalScore: number
  questionCount: number
  difficulty: 1 | 2 | 3
  createdAt: string
}

export interface Question {
  id: number
  paperId: number
  type: 'choice' | 'fill' | 'essay'
  content: string
  options?: string[]
  answer: string
  score: number
}

export interface ExamRecord {
  id: number
  paperId: number
  paperTitle: string
  score: number
  totalScore: number
  duration: number
  submittedAt: string
  answers: Record<number, string>
}

// 通用响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

// 首页统计类型
export interface DashboardStats {
  totalStudyTime: number
  weeklyProgress: number
  planCompletion: number
  recentExams: ExamRecord[]
  subjectProgress: { subject: string; progress: number }[]
}
