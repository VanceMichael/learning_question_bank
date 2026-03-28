<template>
  <div class="page-container">
    <h1 class="page-title">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2">
        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
        <line x1="12" y1="9" x2="12" y2="13"/>
        <line x1="12" y1="17" x2="12.01" y2="17"/>
      </svg>
      错题本
    </h1>

    <div class="action-bar">
      <el-select v-model="selectedSubject" placeholder="选择科目" clearable class="subject-select" @change="loadWrongQuestions">
        <el-option
          v-for="subject in subjects"
          :key="subject.id"
          :label="subject.name"
          :value="subject.id"
        />
      </el-select>
      <el-button type="primary" @click="startPractice" :disabled="wrongQuestions.length === 0">
        开始练习
      </el-button>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loader-ring"></div>
    </div>

    <el-empty v-else-if="wrongQuestions.length === 0" description="暂无错题">
      <el-button type="primary" @click="router.push('/home')">去练习</el-button>
    </el-empty>

    <div v-else class="wrong-questions-list">
      <div v-for="item in wrongQuestions" :key="item.id" class="wrong-question-card card card-hover">
        <div class="question-header">
          <div class="question-info">
            <span class="subject-tag">{{ item.subjectName }}</span>
            <span class="difficulty-tag" :class="'difficulty-' + item.question.difficulty">
              难度 {{ item.question.difficulty }}
            </span>
          </div>
          <div class="wrong-count">
            <span class="count-text">错误次数: {{ item.wrongCount }}</span>
            <el-button type="danger" size="small" @click="removeQuestion(item.questionId)">
              移除
            </el-button>
          </div>
        </div>

        <div class="question-content">
          <div class="type-badge" :class="'type-' + item.question.type">
            {{ getQuestionTypeText(item.question.type) }}
          </div>
          <div class="content-text">{{ item.question.content }}</div>
        </div>

        <div v-if="item.question.options && item.question.options.length > 0" class="options-list">
          <div v-for="(option, index) in item.question.options" :key="index" class="option-item">
            <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
            <span class="option-text">{{ option }}</span>
          </div>
        </div>

        <div class="answer-section">
          <div class="correct-answer">
            <span class="label">正确答案:</span>
            <span class="answer">{{ formatAnswer(item.question.answer) }}</span>
          </div>
          <div v-if="item.question.analysis" class="analysis">
            <span class="label">解析:</span>
            <span class="text">{{ item.question.analysis }}</span>
          </div>
        </div>

        <div class="question-footer">
          <span class="last-wrong">
            最后错误: {{ formatTime(item.lastWrongAt) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWrongQuestions, removeWrongQuestion } from '@/api/wrongQuestion'
import { getSubjects } from '@/api/subject'
import type { WrongQuestion } from '@/api/wrongQuestion'

const router = useRouter()
const loading = ref(true)
const wrongQuestions = ref<WrongQuestion[]>([])
const subjects = ref<any[]>([])
const selectedSubject = ref<number | null>(null)

const formatTime = (t: string) => t ? t.replace('T', ' ').substring(0, 16) : ''

const getQuestionTypeText = (type: number) => {
  const types = ['', '单选', '多选', '判断', '填空', '简答']
  return types[type] || '未知'
}

const formatAnswer = (answer: string | string[]) => {
  if (Array.isArray(answer)) {
    return answer.join(', ')
  }
  return answer
}

const loadWrongQuestions = async () => {
  loading.value = true
  try {
    const res = await getWrongQuestions(selectedSubject.value || undefined)
    wrongQuestions.value = res.data
  } finally {
    loading.value = false
  }
}

const loadSubjects = async () => {
  try {
    const res = await getSubjects()
    subjects.value = res.data
  } catch (error) {
    console.error('Failed to load subjects:', error)
  }
}

const removeQuestion = async (questionId: number) => {
  try {
    await removeWrongQuestion(questionId)
    ElMessage.success('已从错题本中移除')
    await loadWrongQuestions()
  } catch (error) {
    ElMessage.error('移除失败')
  }
}

const startPractice = () => {
  const questionIds = wrongQuestions.value.map(item => item.questionId)
  router.push({
    path: '/practice',
    query: { 
      questionIds: JSON.stringify(questionIds),
      isWrongPractice: 'true'
    }
  })
}

onMounted(async () => {
  await loadSubjects()
  await loadWrongQuestions()
})
</script>

<style scoped lang="scss">
.action-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.subject-select {
  width: 200px;
}

.wrong-questions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.wrong-question-card {
  padding: 20px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.question-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.subject-tag {
  background: rgba(0, 255, 255, 0.1);
  color: var(--neon-cyan);
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  border: 1px solid rgba(0, 255, 255, 0.3);
}

.difficulty-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  
  &.difficulty-1 {
    background: rgba(0, 255, 0, 0.1);
    color: #00ff00;
    border: 1px solid rgba(0, 255, 0, 0.3);
  }
  
  &.difficulty-2 {
    background: rgba(100, 200, 0, 0.1);
    color: #aaff00;
    border: 1px solid rgba(100, 200, 0, 0.3);
  }
  
  &.difficulty-3 {
    background: rgba(255, 200, 0, 0.1);
    color: #ffc800;
    border: 1px solid rgba(255, 200, 0, 0.3);
  }
  
  &.difficulty-4 {
    background: rgba(255, 100, 0, 0.1);
    color: #ff6400;
    border: 1px solid rgba(255, 100, 0, 0.3);
  }
  
  &.difficulty-5 {
    background: rgba(255, 0, 0, 0.1);
    color: #ff0000;
    border: 1px solid rgba(255, 0, 0, 0.3);
  }
}

.wrong-count {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .count-text {
    color: var(--text-secondary);
    font-size: 14px;
  }
}

.question-content {
  margin-bottom: 16px;
}

.type-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 12px;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  
  &.type-1 {
    background: rgba(100, 200, 255, 0.2);
    color: #64c8ff;
  }
  
  &.type-2 {
    background: rgba(255, 100, 200, 0.2);
    color: #ff64c8;
  }
  
  &.type-3 {
    background: rgba(200, 255, 100, 0.2);
    color: #c8ff64;
  }
  
  &.type-4 {
    background: rgba(255, 200, 100, 0.2);
    color: #ffc864;
  }
  
  &.type-5 {
    background: rgba(200, 100, 255, 0.2);
    color: #c864ff;
  }
}

.content-text {
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-primary);
  margin-top: 8px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: 8px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  
  .option-label {
    color: var(--neon-cyan);
    font-weight: 500;
    min-width: 24px;
  }
  
  .option-text {
    color: var(--text-primary);
    line-height: 1.5;
  }
}

.answer-section {
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: 8px;
  margin-bottom: 16px;
  
  .correct-answer {
    margin-bottom: 12px;
    
    .label {
      color: var(--text-secondary);
      margin-right: 8px;
    }
    
    .answer {
      color: #00ff00;
      font-weight: 500;
    }
  }
  
  .analysis {
    .label {
      color: var(--text-secondary);
      margin-right: 8px;
    }
    
    .text {
      color: var(--text-primary);
      line-height: 1.5;
    }
  }
}

.question-footer {
  text-align: right;
  color: var(--text-tertiary);
  font-size: 12px;
}

.loading-state {
  text-align: center;
  padding: 60px;
}
</style>