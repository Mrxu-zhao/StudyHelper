<template>
  <el-aside :width="collapsed ? '64px' : '200px'" class="sidebar-container">
    <el-menu
      :default-active="activeRoute"
      :collapse="collapsed"
      class="sidebar-menu"
      @select="handleSelect"
    >
      <el-menu-item index="/home">
        <el-icon><HomeFilled /></el-icon>
        <template #title>首页</template>
      </el-menu-item>
      
      <el-menu-item index="/knowledge">
        <el-icon><Reading /></el-icon>
        <template #title>知识库</template>
      </el-menu-item>
      
      <el-menu-item index="/plan">
        <el-icon><Calendar /></el-icon>
        <template #title>学习计划</template>
      </el-menu-item>
      
      <el-menu-item index="/exam">
        <el-icon><Document /></el-icon>
        <template #title>试卷</template>
      </el-menu-item>
      
      <el-divider />
      
      <el-menu-item index="/favorites">
        <el-icon><Star /></el-icon>
        <template #title>我的收藏</template>
      </el-menu-item>
      
      <el-menu-item index="/history">
        <el-icon><Clock /></el-icon>
        <template #title>学习记录</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { HomeFilled, Reading, Calendar, Document, Star, Clock } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const collapsed = computed(() => appStore.isSidebarCollapsed)
const activeRoute = computed(() => '/' + (route.path.split('/')[1] || ''))

const handleSelect = (index: string) => {
  router.push(index)
}
</script>

<style scoped>
.sidebar-container {
  height: calc(100vh - 60px);
  background: white;
  transition: width 0.3s;
  overflow-x: hidden;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style>
