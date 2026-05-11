<template>
  <div class="exam-detail">
    <div class="exam-header">
      <div class="exam-info">
        <h1>{{ paper.title }}</h1>
        <div class="exam-meta">
          <el-tag type="info">{{ paper.subject }}</el-tag>
          <span>考试时长: {{ paper.duration }}分钟</span>
          <span>总分: {{ paper.totalScore }}分</span>
        </div>
      </div>
      <div class="exam-timer">
        <el-icon><Timer /></el-icon>
        <span :class="{ warning: remainingTime < 300 }">{{ formatTime(remainingTime) }}</span>
      </div>
    </div>

    <div class="exam-content">
      <div class="questions-panel">
        <div v-for="(question, index) in questions" :key="question.id" class="question-card">
          <div class="question-header">
            <span class="question-number">第 {{ index + 1 }} 题</span>
            <el-tag size="small" type="info">{{ getQuestionTypeText(question.type) }}</el-tag>
            <el-tag size="small">{{ question.score }}分</el-tag>
          </div>
          
          <div class="question-content">
            <p>{{ question.content }}</p>
          </div>
          
          <div v-if="question.type === 'choice' && question.options" class="question-options">
            <el-radio-group v-model="answers[question.id]">
              <el-radio 
                v-for="(option, oIndex) in question.options" 
                :key="oIndex"
                :label="String.fromCharCode(65 + oIndex)"
              >
                {{ String.fromCharCode(65 + oIndex) }}. {{ option }}
              </el-radio>
            </el-radio-group>
          </div>
          
          <div v-else-if="question.type === 'fill'" class="question-input">
            <el-input
              v-model="answers[question.id]"
              placeholder="请输入答案"
              type="textarea"
              :rows="2"
            />
          </div>
          
          <div v-else class="question-input">
            <el-input
              v-model="answers[question.id]"
              placeholder="请输入答案"
              type="textarea"
              :rows="4"
            />
          </div>
        </div>
        
        <div class="submit-section">
          <el-button type="warning" @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" size="large" @click="handleSubmit">提交试卷</el-button>
        </div>
      </div>
      
      <div class="answer-sheet">
        <h3>答题卡</h3>
        <div class="sheet-grid">
          <div 
            v-for="(question, index) in questions" 
            :key="question.id"
            class="sheet-item"
            :class="{ answered: answers[question.id] }"
            @click="scrollToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
        <div class="sheet-legend">
          <div class="legend-item">
            <span class="legend-dot"></span>
            <span>未答</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot answered"></span>
            <span>已答</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer } from '@element-plus/icons-vue'
import type { Paper, Question } from '@/types'

const route = useRoute()
const router = useRouter()

const paper = ref<Paper>({
  id: 1,
  title: '高一数学月考',
  subject: '数学',
  grade: '高一',
  duration: 120,
  totalScore: 150,
  questionCount: 22,
  difficulty: 2,
  createdAt: '2024-03-15'
})

const questions = ref<Question[]>([
  { id: 1, paperId: 1, type: 'choice', content: '已知集合A={1,2,3}, B={2,3,4}, 则A∪B等于？', options: ['{1,2,3}', '{2,3,4}', '{1,2,3,4}', '{1,4}'], answer: 'C', score: 5 },
  { id: 2, paperId: 1, type: 'choice', content: '函数f(x)=x²-2x+1的最小值是？', options: ['0', '1', '-1', '2'], answer: 'A', score: 5 },
  { id: 3, paperId: 1, type: 'fill', content: '若sinθ=1/2, 且θ在第一象限, 则θ=_____°', answer: '30', score: 5 },
  { id: 4, paperId: 1, type: 'choice', content: '下列哪个选项是正确的？', options: ['A', 'B', 'C', 'D'], answer: 'B', score: 5 },
  { id: 5, paperId: 1, type: 'essay', content: '请证明：三角形内角和为180°', answer: '', score: 10 }
])

const answers = reactive<Record<number, string>>({})
const remainingTime = ref(7200) // 120分钟
let timer: number | null = null

const answeredCount = computed(() => {
  return Object.keys(answers).length
})

const formatTime = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getQuestionTypeText = (type: string) => {
  const texts: Record<string, string> = {
    choice: '选择题',
    fill: '填空题',
    essay: '解答题'
  }
  return texts[type] || type
}

const scrollToQuestion = (index: number) => {
  const element = document.querySelectorAll('.question-card')[index] as HTMLElement
  element?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const saveDraft = () => {
  localStorage.setItem(`exam_draft_${route.params.id}`, JSON.stringify(answers))
  ElMessage.success('草稿已保存')
}

const handleSubmit = async () => {
  const unanswered = questions.value.filter(q => !answers[q.id])
  
  if (unanswered.length > 0) {
    try {
      await ElMessageBox.confirm(
        `还有 ${unanswered.length} 道题目未作答，确定要提交吗？`,
        '提示',
        { confirmButtonText: '确定提交', cancelButtonText: '继续答题', type: 'warning' }
      )
    } catch {
      return
    }
  }
  
  if (timer) {
    clearInterval(timer)
  }
  
  ElMessage.success('试卷提交成功')
  router.push(`/exam/${route.params.id}/result`)
}

onMounted(() => {
  // 加载草稿
  const draft = localStorage.getItem(`exam_draft_${route.params.id}`)
  if (draft) {
    Object.assign(answers, JSON.parse(draft))
  }
  
  // 启动计时器
  timer = window.setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      ElMessage.warning('考试时间到，自动提交试卷')
      handleSubmit()
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.exam-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.exam-info h1 {
  margin: 0 0 8px;
  font-size: 24px;
}

.exam-meta {
  display: flex;
  gap: 16px;
  align-items: center;
  color: #606266;
}

.exam-timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}

.exam-timer .warning {
  color: #f56c6c;
}

.exam-content {
  display: flex;
  max-width: 1400px;
  margin: 24px auto;
  gap: 24px;
}

.questions-panel {
  flex: 1;
}

.question-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.question-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.question-number {
  font-weight: 600;
  color: #409eff;
}

.question-content {
  margin-bottom: 16px;
  font-size: 16px;
  line-height: 1.6;
}

.question-options {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.question-options .el-radio {
  display: block;
  margin-bottom: 12px;
  line-height: 32px;
}

.submit-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  margin-top: 20px;
}

.answer-sheet {
  width: 200px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  height: fit-content;
  position: sticky;
  top: 24px;
}

.answer-sheet h3 {
  margin: 0 0 16px;
  text-align: center;
}

.sheet-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 16px;
}

.sheet-item {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.sheet-item:hover {
  background: #f5f7fa;
}

.sheet-item.answered {
  background: #67c23a;
  color: white;
  border-color: #67c23a;
}

.sheet-legend {
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 12px;
  color: #606266;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-dot {
  width: 16px;
  height: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
}

.legend-dot.answered {
  background: #67c23a;
  border-color: #67c23a;
}
</style>
