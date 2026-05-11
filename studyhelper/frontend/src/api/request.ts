import axios from 'axios'
import type { ApiResponse } from '@/types'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    const message = error.response?.data?.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

export default request

// 通用请求方法
export const get = <T>(url: string, params?: object): Promise<ApiResponse<T>> => {
  return request.get(url, { params })
}

export const post = <T>(url: string, data?: object): Promise<ApiResponse<T>> => {
  return request.post(url, data)
}

export const put = <T>(url: string, data?: object): Promise<ApiResponse<T>> => {
  return request.put(url, data)
}

export const del = <T>(url: string): Promise<ApiResponse<T>> => {
  return request.delete(url)
}
