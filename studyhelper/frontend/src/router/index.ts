import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/knowledge/Knowledge.vue'),
    meta: { title: '知识库', requiresAuth: true }
  },
  {
    path: '/knowledge/:id',
    name: 'KnowledgeDetail',
    component: () => import('@/views/knowledge/Detail.vue'),
    meta: { title: '知识点详情', requiresAuth: true }
  },
  {
    path: '/plan',
    name: 'Plan',
    component: () => import('@/views/plan/Plan.vue'),
    meta: { title: '学习计划', requiresAuth: true }
  },
  {
    path: '/plan/create',
    name: 'PlanCreate',
    component: () => import('@/views/plan/Create.vue'),
    meta: { title: '创建计划', requiresAuth: true }
  },
  {
    path: '/exam',
    name: 'Exam',
    component: () => import('@/views/exam/Exam.vue'),
    meta: { title: '试卷', requiresAuth: true }
  },
  {
    path: '/exam/:id',
    name: 'ExamDetail',
    component: () => import('@/views/exam/Detail.vue'),
    meta: { title: '考试', requiresAuth: true }
  },
  {
    path: '/exam/:id/result',
    name: 'ExamResult',
    component: () => import('@/views/exam/Result.vue'),
    meta: { title: '成绩分析', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
  } else {
    next()
  }
})

export default router
