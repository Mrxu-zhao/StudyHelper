<template>
  <div class="knowledge-tree">
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    
    <div v-else-if="error" class="error-container">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ error }}</span>
      <el-button type="primary" size="small" @click="$emit('retry')">重试</el-button>
    </div>
    
    <el-tree
      v-else
      :data="treeData"
      :props="treeProps"
      node-key="id"
      :default-expanded-keys="expandedKeys"
      @node-click="handleNodeClick"
    >
      <template #default="{ node, data }">
        <div class="tree-node">
          <el-icon v-if="data.type === 'chapter'" color="#409eff"><Folder /></el-icon>
          <el-icon v-else-if="data.type === 'section'" color="#67c23a"><FolderOpened /></el-icon>
          <el-icon v-else color="#e6a23c"><Document /></el-icon>
          <span class="node-label">{{ node.label }}</span>
          <el-tag v-if="data.type === 'point'" :type="getDifficultyType(data.difficulty)" size="small">
            {{ getDifficultyText(data.difficulty) }}
          </el-tag>
        </div>
      </template>
    </el-tree>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Chapter } from '@/types'
import { Loading, WarningFilled, Folder, FolderOpened, Document } from '@element-plus/icons-vue'

interface Props {
  chapters: Chapter[]
  loading?: boolean
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  error: ''
})

const emit = defineEmits<{
  (e: 'nodeClick', data: any): void
  (e: 'retry'): void
}>()

const treeProps = {
  children: 'sections',
  label: 'name'
}

const expandedKeys = ref<number[]>([])

const treeData = computed(() => {
  return props.chapters.map(chapter => ({
    id: chapter.id,
    type: 'chapter',
    name: chapter.name,
    sections: chapter.sections?.map(section => ({
      id: section.id,
      type: 'section',
      name: section.name,
      children: section.knowledgePoints?.map(point => ({
        id: point.id,
        type: 'point',
        name: point.title,
        difficulty: point.difficulty
      })) || []
    })) || []
  }))
})

watch(() => treeData.value, (data) => {
  if (data.length > 0) {
    expandedKeys.value = [data[0].id]
  }
}, { immediate: true })

const handleNodeClick = (data: any) => {
  emit('nodeClick', data)
}

const getDifficultyType = (difficulty: number) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty: number) => {
  const texts = ['', '基础', '中等', '困难']
  return texts[difficulty] || ''
}
</script>

<style scoped>
.knowledge-tree {
  padding: 16px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-label {
  flex: 1;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px;
  color: #909399;
}
</style>
