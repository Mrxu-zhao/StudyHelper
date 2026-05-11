<template>
  <div class="home-layout">
    <AppHeader />
    <div class="home-content">
      <AppSidebar />
      <main class="main-content">
        <div class="page-container">
          <el-page-header @back="goBack" :title="'返回'" content="创建学习计划" />
          
          <div class="form-container">
            <el-card>
              <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="120px"
                class="plan-form"
              >
                <el-form-item label="计划名称" prop="title">
                  <el-input v-model="form.title" placeholder="请输入计划名称" />
                </el-form-item>
                
                <el-form-item label="计划描述" prop="description">
                  <el-input
                    v-model="form.description"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入计划描述"
                  />
                </el-form-item>
                
                <el-form-item label="学科" prop="subjectId">
                  <el-select v-model="form.subjectId" placeholder="请选择学科" style="width: 100%">
                    <el-option label="数学" :value="1" />
                    <el-option label="物理" :value="2" />
                    <el-option label="化学" :value="3" />
                    <el-option label="英语" :value="4" />
                    <el-option label="语文" :value="5" />
                    <el-option label="生物" :value="6" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="开始日期" prop="startDate">
                  <el-date-picker
                    v-model="form.startDate"
                    type="date"
                    placeholder="选择开始日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                  />
                </el-form-item>
                
                <el-form-item label="结束日期" prop="endDate">
                  <el-date-picker
                    v-model="form.endDate"
                    type="date"
                    placeholder="选择结束日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                  />
                </el-form-item>
                
                <el-divider content-position="left">学习任务</el-divider>
                
                <div v-for="(task, index) in form.tasks" :key="index" class="task-item">
                  <el-form-item label-width="0">
                    <div class="task-row">
                      <el-input v-model="task.title" placeholder="任务名称" style="flex: 1" />
                      <el-date-picker
                        v-model="task.targetDate"
                        type="date"
                        placeholder="目标日期"
                        style="width: 180px"
                        format="YYYY-MM-DD"
                      />
                      <el-button type="danger" :icon="Delete" circle @click="removeTask(index)" />
                    </div>
                  </el-form-item>
                </div>
                
                <el-form-item>
                  <el-button type="dashed" :icon="Plus" @click="addTask">添加任务</el-button>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
                    创建计划
                  </el-button>
                  <el-button size="large" @click="goBack">取消</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/Header.vue'
import AppSidebar from '@/components/Sidebar.vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  title: '',
  description: '',
  subjectId: null as number | null,
  startDate: '',
  endDate: '',
  tasks: [
    { title: '', targetDate: '' }
  ]
})

const rules = {
  title: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入计划描述', trigger: 'blur' }],
  subjectId: [{ required: true, message: '请选择学科', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const addTask = () => {
  form.tasks.push({ title: '', targetDate: '' })
}

const removeTask = (index: number) => {
  if (form.tasks.length > 1) {
    form.tasks.splice(index, 1)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    const validTasks = form.tasks.filter(t => t.title && t.targetDate)
    if (validTasks.length === 0) {
      ElMessage.warning('请至少添加一个有效任务')
      return
    }
    
    submitting.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 1000))
      ElMessage.success('计划创建成功')
      router.push('/plan')
    } catch (error) {
      ElMessage.error('创建失败，请重试')
    } finally {
      submitting.value = false
    }
  })
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.form-container {
  max-width: 800px;
  margin: 24px auto;
}

.plan-form {
  padding: 20px 0;
}

.task-item {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.task-row {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
