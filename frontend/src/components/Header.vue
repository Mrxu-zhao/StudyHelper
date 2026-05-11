<template>
  <el-header class="header-container">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="toggleSidebar">
        <Expand v-if="appStore.isSidebarCollapsed" />
        <Fold v-else />
      </el-icon>
      <h1 class="logo">StudyHelper</h1>
    </div>
    
    <div class="header-center">
      <el-input
        v-model="searchQuery"
        placeholder="搜索知识点、试卷..."
        class="search-input"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
      />
    </div>
    
    <div class="header-right">
      <el-badge :value="3" class="notification-badge">
        <el-icon class="header-icon"><Bell /></el-icon>
      </el-badge>
      
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { Search, Bell, UserFilled, User, Setting, SwitchButton, Expand, Fold } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const searchQuery = ref('')

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/knowledge', query: { keyword: searchQuery.value } })
  }
}

const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      break
    case 'settings':
      break
    case 'logout':
      await userStore.logout()
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  background: white;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
  margin: 0;
}

.header-center {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
}

.notification-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #303133;
}
</style>
