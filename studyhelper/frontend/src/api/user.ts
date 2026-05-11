import { get, post } from './request'
import type { User, LoginForm, RegisterForm } from '@/types'

// 登录
export const login = (data: LoginForm) => {
  return post<User>('/auth/login', data)
}

// 注册
export const register = (data: RegisterForm) => {
  return post<User>('/auth/register', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return get<User>('/auth/current')
}

// 退出登录
export const logout = () => {
  return post('/auth/logout')
}

// 更新用户信息
export const updateUser = (data: Partial<User>) => {
  return post('/auth/update', data)
}
