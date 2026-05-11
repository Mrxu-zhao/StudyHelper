import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types'
import { login as apiLogin, register as apiRegister, logout as apiLogout, getCurrentUser } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)
  const isLoading = ref(false)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: User) => {
    userInfo.value = info
  }

  const login = async (username: string, password: string) => {
    isLoading.value = true
    try {
      const res = await apiLogin({ username, password })
      setToken(res.data.id.toString()) // 模拟token
      setUserInfo(res.data)
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    } finally {
      isLoading.value = false
    }
  }

  const register = async (data: { username: string; password: string; email: string; grade: string }) => {
    isLoading.value = true
    try {
      const res = await apiRegister(data)
      setToken(res.data.id.toString())
      setUserInfo(res.data)
      return true
    } catch (error) {
      console.error('注册失败:', error)
      return false
    } finally {
      isLoading.value = false
    }
  }

  const logout = async () => {
    try {
      await apiLogout()
    } catch (error) {
      console.error('退出失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      setUserInfo(res.data)
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    token,
    userInfo,
    isLoading,
    setToken,
    setUserInfo,
    login,
    register,
    logout,
    fetchUserInfo,
    isLoggedIn
  }
})
