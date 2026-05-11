<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <div class="section-header">
            <h2>知识库</h2>
            <div class="header-actions">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索知识点..."
                :prefix-icon="Search"
                style="width: 300px"
                @keyup.enter="handleSearch"
              />
              <el-button type="primary" :icon="Refresh" @click="loadChapters">刷新</el-button>
            </div>
          </div>

          <!-- 学科选择 -->
          <div class="subjects-grid">
            <div
              v-for="subject in subjects"
              :key="subject.id"
              class="subject-card"
              :class="{ active: activeSubject === subject.id }"
              @click="selectSubject(subject.id)"
            >
              <div class="subject-icon" :style="{ backgroundColor: subject.color }">
                <el-icon :size="32" color="white">
                  <component :is="getSubjectIcon(subject.icon)" />
                </el-icon>
              </div>
              <h3>{{ subject.name }}</h3>
              <p>{{ subject.chapterCount }} 章节</p>
            </div>
          </div>

          <!-- 章节树 -->
          <div v-if="activeSubject" class="chapters-section">
            <div class="card-container">
              <KnowledgeTree
                :chapters="chapters"
                :loading="loading"
                :error="error"
                @node-click="handleNodeClick"
                @retry="loadChapters"
              />
            </div>
          </div>

          <!-- 提示信息 -->
          <el-empty v-if="!activeSubject" description="请选择一个学科开始学习" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import KnowledgeTree from '@/components/KnowledgeTree.vue'
import { Search, Refresh, Math, Atom, Flask, Document, OfficeBuilding, Translate } from '@element-plus/icons-vue'
import type { Subject, Chapter } from '@/types'

const router = useRouter()

const searchKeyword = ref('')
const activeSubject = ref<number | null>(null)
const subjects = ref<Subject[]>([])
const chapters = ref<Chapter[]>([])
const loading = ref(false)
const error = ref('')

// 模拟学科数据
const mockSubjects: Subject[] = [
  { id: 1, name: '数学', icon: 'Math', color: '#409eff', chapterCount: 12 },
  { id: 2, name: '物理', icon: 'Atom', color: '#67c23a', chapterCount: 10 },
  { id: 3, name: '化学', icon: 'Flask', color: '#e6a23c', chapterCount: 8 },
  { id: 4, name: '英语', icon: 'Translate', color: '#f56c6c', chapterCount: 15 },
  { id: 5, name: '语文', icon: 'Document', color: '#909399', chapterCount: 14 },
  { id: 6, name: '生物', icon: 'OfficeBuilding', color: '#00b894', chapterCount: 9 }
]

// 模拟章节数据
const mockChapters: Record<number, Chapter[]> = {
  1: [
    {
      id: 1,
      subjectId: 1,
      name: '集合与函数',
      order: 1,
      sections: [
        {
          id: 1,
          chapterId: 1,
          name: '集合',
          order: 1,
          knowledgePoints: [
            { id: 1, sectionId: 1, title: '集合的概念', content: '集合是由某些确定的对象组成的整体...', difficulty: 1, isFavorite: false, tags: ['基础'] },
            { id: 2, sectionId: 1, title: '集合的运算', content: '集合的基本运算包括并集、交集、补集...', difficulty: 2, isFavorite: true, tags: ['重点'] },
            { id: 3, sectionId: 1, title: '集合间的关系', content: '子集、真子集、空集的概念...', difficulty: 1, isFavorite: false, tags: ['基础'] }
          ]
        },
        {
          id: 2,
          chapterId: 1,
          name: '函数',
          order: 2,
          knowledgePoints: [
            { id: 4, sectionId: 2, title: '函数的概念', content: '函数是描述变量之间对应关系的数学模型...', difficulty: 1, isFavorite: false, tags: ['基础'] },
            { id: 5, sectionId: 2, title: '函数的性质', content: '单调性、奇偶性、周期性...', difficulty: 2, isFavorite: false, tags: ['重点'] }
          ]
        }
      ]
    },
    {
      id: 2,
      subjectId: 1,
      name: '三角函数',
      order: 2,
      sections: [
        {
          id: 3,
          chapterId: 2,
          name: '三角函数基础',
          order: 1,
          knowledgePoints: [
            { id: 6, sectionId: 3, title: '弧度制', content: '弧度是角度的另一种度量方式...', difficulty: 1, isFavorite: false, tags: ['基础'] },
            { id: 7, sectionId: 3, title: '任意角的三角函数', content: '正弦、余弦、正切的定义...', difficulty: 2, isFavorite: false, tags: ['重点'] }
          ]
        }
      ]
    }
  ]
}

const getSubjectIcon = (iconName: string) => {
  const icons: Record<string, any> = {
    Math: markRaw(Math),
    Atom: markRaw(Atom),
    Flask: markRaw(Flask),
    Document: markRaw(Document),
    OfficeBuilding: markRaw(OfficeBuilding),
    Translate: markRaw(Translate)
  }
  return icons[iconName] || markRaw(Document)
}

const loadSubjects = async () => {
  subjects.value = mockSubjects
}

const loadChapters = async () => {
  if (!activeSubject.value) return
  
  loading.value = true
  error.value = ''
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    chapters.value = mockChapters[activeSubject.value] || []
  } catch (e) {
    error.value = '加载章节失败'
  } finally {
    loading.value = false
  }
}

const selectSubject = (id: number) => {
  activeSubject.value = id
  loadChapters()
}

const handleNodeClick = (data: any) => {
  if (data.type === 'point') {
    router.push(`/knowledge/${data.id}`)
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/knowledge', query: { keyword: searchKeyword.value } })
  }
}

onMounted(() => {
  loadSubjects()
})
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.subjects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.subject-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.subject-card.active {
  border-color: #409eff;
}

.subject-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.subject-card h3 {
  margin: 0 0 4px;
  color: #303133;
}

.subject-card p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.chapters-section {
  margin-top: 24px;
}
</style>
