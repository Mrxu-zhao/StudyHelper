<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <el-page-header @back="goBack" :title="'返回'" content="知识点详情" />
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading" :size="40"><Loading /></el-icon>
            <p>加载中...</p>
          </div>
          
          <div v-else-if="point" class="knowledge-detail">
            <div class="detail-header">
              <h1>{{ point.title }}</h1>
              <div class="detail-actions">
                <el-tag :type="getDifficultyType(point.difficulty)" size="large">
                  {{ getDifficultyText(point.difficulty) }}
                </el-tag>
                <el-button 
                  :type="point.isFavorite ? 'warning' : 'default'"
                  :icon="point.isFavorite ? 'Star' : 'StarFilled'"
                  @click="toggleFavorite"
                >
                  {{ point.isFavorite ? '取消收藏' : '收藏' }}
                </el-button>
              </div>
            </div>
            
            <div class="detail-tags">
              <el-tag v-for="tag in point.tags" :key="tag" type="info">{{ tag }}</el-tag>
            </div>
            
            <el-card class="content-card">
              <div class="knowledge-content" v-html="point.content"></div>
            </el-card>
            
            <el-card class="actions-card">
              <div class="action-buttons">
                <el-button type="primary" :icon="ArrowLeft" @click="goToPrev">上一个知识点</el-button>
                <el-button type="primary" :icon="ArrowRight" @click="goToNext">下一个知识点</el-button>
              </div>
            </el-card>
          </div>
          
          <el-empty v-else description="知识点不存在" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, Loading, Star, StarFilled } from '@element-plus/icons-vue'
import type { KnowledgePoint } from '@/types'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const point = ref<KnowledgePoint | null>(null)

const mockPoints: Record<number, KnowledgePoint> = {
  1: {
    id: 1,
    sectionId: 1,
    title: '集合的概念',
    content: `
      <h3>一、集合的定义</h3>
      <p>集合是由某些确定的对象组成的整体。组成集合的对象称为集合的元素。</p>
      
      <h3>二、集合的表示方法</h3>
      <ol>
        <li><strong>列举法</strong>：把集合中的元素一一列举出来，写在大括号内。如：{1, 2, 3}</li>
        <li><strong>描述法</strong>：把集合中元素的共同特征描述出来。如：{x | x > 0}</li>
      </ol>
      
      <h3>三、常见数集</h3>
      <ul>
        <li>N：自然数集</li>
        <li>Z：整数集</li>
        <li>Q：有理数集</li>
        <li>R：实数集</li>
      </ul>
      
      <h3>四、例题</h3>
      <p>判断下列对象能否组成集合：</p>
      <p>（1）小于10的奇数 → 可以组成集合</p>
      <p>（2）很小的数 → 不能组成集合（"很小"不确定）</p>
    `,
    difficulty: 1,
    isFavorite: false,
    tags: ['基础', '重要']
  },
  2: {
    id: 2,
    sectionId: 1,
    title: '集合的运算',
    content: `
      <h3>一、并集</h3>
      <p>由属于集合A或集合B的所有元素组成的集合，称为A与B的并集，记作A∪B。</p>
      <p><strong>符号表示</strong>：A∪B = {x | x∈A 或 x∈B}</p>
      
      <h3>二、交集</h3>
      <p>由属于集合A且属于集合B的所有元素组成的集合，称为A与B的交集，记作A∩B。</p>
      <p><strong>符号表示</strong>：A∩B = {x | x∈A 且 x∈B}</p>
      
      <h3>三、补集</h3>
      <p>在全集I中，属于集合A的所有元素组成的集合，称为A在I中的补集，记作∁IA或A'。</p>
      
      <h3>四、交并补运算律</h3>
      <ul>
        <li>交换律：A∪B = B∪A，A∩B = B∩A</li>
        <li>结合律：(A∪B)∪C = A∪(B∪C)</li>
        <li>分配律：A∪(B∩C) = (A∪B)∩(A∪C)</li>
      </ul>
    `,
    difficulty: 2,
    isFavorite: true,
    tags: ['重点', '难点']
  }
}

const loadPoint = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 300))
    const id = Number(route.params.id)
    point.value = mockPoints[id] || null
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const getDifficultyType = (difficulty: number) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty: number) => {
  const texts = ['', '基础', '中等', '困难']
  return texts[difficulty] || ''
}

const toggleFavorite = () => {
  if (point.value) {
    point.value.isFavorite = !point.value.isFavorite
    ElMessage.success(point.value.isFavorite ? '已收藏' : '已取消收藏')
  }
}

const goToPrev = () => {
  ElMessage.info('前往上一个知识点')
}

const goToNext = () => {
  ElMessage.info('前往下一个知识点')
}

onMounted(() => {
  loadPoint()
})
</script>

<style scoped>
.knowledge-detail {
  margin-top: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-header h1 {
  margin: 0;
  font-size: 28px;
  color: #303133;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

.detail-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.knowledge-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
}

.knowledge-content :deep(h3) {
  color: #409eff;
  margin-top: 24px;
  margin-bottom: 12px;
}

.knowledge-content :deep(p) {
  margin-bottom: 12px;
}

.knowledge-content :deep(ol),
.knowledge-content :deep(ul) {
  margin-left: 24px;
  margin-bottom: 16px;
}

.knowledge-content :deep(li) {
  margin-bottom: 8px;
}

.knowledge-content :deep(strong) {
  color: #409eff;
}

.actions-card {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #909399;
}

.loading-container p {
  margin-top: 12px;
}
</style>
