<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        错题本
      </h1>
      <el-button type="primary" @click="startPractice" :disabled="wrongQuestions.length === 0">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
        &nbsp;开始练习
      </el-button>
    </div>

    <div class="filter-bar card">
      <CyberSelect
        v-model="selectedSubjectId"
        :options="subjectOptions"
        placeholder="全部科目"
        clearable
        @change="loadData"
      />
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loader-ring"></div>
    </div>

    <el-empty v-else-if="wrongQuestions.length === 0" description="暂无错题记录">
      <el-button type="primary" @click="router.push('/home')">去练习</el-button>
    </el-empty>

    <div v-else class="wrong-question-list">
      <div v-for="wq in wrongQuestions" :key="wq.id" class="wrong-question-item card">
        <div class="wq-header">
          <div class="wq-tags">
            <el-tag size="small" type="danger">错误 {{ wq.wrongCount }} 次</el-tag>
            <el-tag size="small" type="info">{{ wq.subjectName }}</el-tag>
            <el-tag size="small">{{ typeNames[wq.question?.type] || '未知' }}</el-tag>
          </div>
          <div class="wq-actions">
            <span class="last-wrong-time">最后错误: {{ formatTime(wq.lastWrongAt) }}</span>
            <el-button text type="danger" size="small" @click="handleRemove(wq.questionId)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
              &nbsp;移除
            </el-button>
          </div>
        </div>
        <p class="wq-content">{{ wq.question?.content }}</p>
        <div v-if="wq.question?.options" class="wq-options">
          <span v-for="(opt, i) in wq.question.options" :key="i">{{ String.fromCharCode(65 + i) }}. {{ opt }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWrongQuestions, removeWrongQuestion, WrongQuestion } from '@/api/wrongQuestion'
import { getSubjects } from '@/api/subject'
import CyberSelect from '@/components/CyberSelect.vue'

const router = useRouter()
const wrongQuestions = ref<WrongQuestion[]>([])
const subjects = ref<any[]>([])
const loading = ref(true)
const selectedSubjectId = ref<number | undefined>()

const typeNames: Record<number, string> = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }

const subjectOptions = computed(() => subjects.value.map(s => ({ label: s.name, value: s.id })))

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getWrongQuestions(selectedSubjectId.value)
    wrongQuestions.value = res.data
  } finally {
    loading.value = false
  }
}

const handleRemove = async (questionId: number) => {
  try {
    await ElMessageBox.confirm('确定要从错题本中移除这道题吗？', '提示', { type: 'warning' })
    await removeWrongQuestion(questionId)
    ElMessage.success('移除成功')
    loadData()
  } catch {}
}

const startPractice = () => {
  const questionIds = wrongQuestions.value.map(wq => wq.questionId)
  router.push({
    path: '/practice',
    query: { questionIds: JSON.stringify(questionIds) }
  })
}

const formatTime = (t: string) => t ? t.replace('T', ' ').substring(0, 16) : ''

onMounted(async () => {
  const res: any = await getSubjects()
  subjects.value = res.data
  loadData()
})
</script>

<style scoped lang="scss">
.loading-state { text-align: center; padding: 60px; }
.loader-ring { width: 32px; height: 32px; border: 3px solid var(--cyber-border); border-top-color: var(--neon-cyan); border-radius: 50%; margin: 0 auto; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 20px; padding: 16px; }
.wrong-question-list { display: flex; flex-direction: column; gap: 12px; }
.wrong-question-item { padding: 20px; }
.wq-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.wq-tags { display: flex; gap: 8px; }
.wq-actions { display: flex; align-items: center; gap: 12px; }
.last-wrong-time { font-size: 12px; color: var(--text-muted); }
.wq-content { font-size: 15px; font-weight: 600; line-height: 1.6; margin-bottom: 8px; }
.wq-options { display: flex; flex-wrap: wrap; gap: 16px; span { font-size: 13px; color: var(--text-secondary); } }
</style>
