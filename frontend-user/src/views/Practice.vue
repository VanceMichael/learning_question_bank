<template>
  <div class="page-container">
    <div v-if="loading" class="loading-state">
      <div class="loader-ring"></div>
      <p>加载题目中...</p>
    </div>

    <div v-else-if="questions.length === 0" class="card" style="text-align:center;padding:60px">
      <el-empty description="该科目暂无题目">
        <el-button type="primary" @click="router.push('/questions/add')">去添加题目</el-button>
      </el-empty>
    </div>

    <template v-else>
      <!-- 答题模式 -->
      <div v-if="!finished" class="practice-area">
        <div class="progress-bar">
          <div class="progress-info">
            <span class="progress-label">PROGRESS</span>
            <span>{{ currentIndex + 1 }} / {{ questions.length }}</span>
          </div>
          <div class="progress-track">
            <div class="progress-fill" :style="{ width: `${((currentIndex + 1) / questions.length) * 100}%` }"></div>
          </div>
        </div>

        <div class="question-card card" :key="currentIndex">
          <div class="question-header">
            <el-tag size="small">{{ getTypeName(currentQuestion.type) }}</el-tag>
            <span class="diff-dots">
              <span v-for="i in 5" :key="i" class="diff-dot" :class="{ active: i <= (currentQuestion.difficulty || 3) }"></span>
            </span>
          </div>
          <h3 class="question-content">{{ currentQuestion.content }}</h3>

          <!-- 单选 -->
          <div v-if="currentQuestion.type === 1" class="options">
            <div v-for="(opt, i) in currentQuestion.options" :key="i" class="option-item" :class="{ selected: userAnswers[currentIndex] === String.fromCharCode(65 + i) }" @click="selectAnswer(String.fromCharCode(65 + i))">
              <span class="option-label">{{ String.fromCharCode(65 + i) }}</span>
              <span>{{ opt }}</span>
            </div>
          </div>

          <!-- 多选 -->
          <div v-else-if="currentQuestion.type === 2" class="options">
            <div v-for="(opt, i) in currentQuestion.options" :key="i" class="option-item" :class="{ selected: multiSelected.includes(String.fromCharCode(65 + i)) }" @click="toggleMulti(String.fromCharCode(65 + i))">
              <span class="option-label">{{ String.fromCharCode(65 + i) }}</span>
              <span>{{ opt }}</span>
            </div>
          </div>

          <!-- 判断 -->
          <div v-else-if="currentQuestion.type === 3" class="options">
            <div class="option-item" :class="{ selected: userAnswers[currentIndex] === 'true' }" @click="selectAnswer('true')">
              <span class="option-label">✓</span><span>正确</span>
            </div>
            <div class="option-item" :class="{ selected: userAnswers[currentIndex] === 'false' }" @click="selectAnswer('false')">
              <span class="option-label">✗</span><span>错误</span>
            </div>
          </div>

          <!-- 填空 -->
          <div v-else-if="currentQuestion.type === 4" style="max-width:600px">
            <el-input v-model="userAnswers[currentIndex]" placeholder="请输入答案" size="large" />
          </div>

          <!-- 简答 -->
          <div v-else-if="currentQuestion.type === 5" style="max-width:600px">
            <el-input v-model="userAnswers[currentIndex]" type="textarea" :rows="4" placeholder="请输入答案" />
          </div>
        </div>

        <div class="action-bar">
          <el-button @click="prevQuestion" :disabled="currentIndex === 0" size="large">
            ← 上一题
          </el-button>
          <div class="question-dots">
            <span v-for="(_, i) in questions" :key="i" class="dot" :class="{ active: i === currentIndex, answered: !!userAnswers[i] }" @click="currentIndex = i; syncMulti()"></span>
          </div>
          <el-button v-if="currentIndex < questions.length - 1" type="primary" @click="nextQuestion" size="large">
            下一题 →
          </el-button>
          <el-button v-else type="success" @click="handleSubmit" :loading="submitting" size="large">
            提交答卷
          </el-button>
        </div>
      </div>

      <!-- 结果预览 -->
      <div v-else class="review-area">
        <div class="result-banner" :class="resultClass">
          <h2>练习完成</h2>
          <div class="result-stats">
            <div class="result-item"><span class="val">{{ result?.totalCount }}</span><span class="lbl">总题数</span></div>
            <div class="result-item"><span class="val correct">{{ result?.correctCount }}</span><span class="lbl">正确</span></div>
            <div class="result-item"><span class="val wrong">{{ (result?.totalCount || 0) - (result?.correctCount || 0) }}</span><span class="lbl">错误</span></div>
            <div class="result-item"><span class="val">{{ result?.accuracy }}%</span><span class="lbl">正确率</span></div>
          </div>
        </div>

        <div class="review-controls">
          <el-button type="primary" @click="showAnswers = !showAnswers" size="large">
            {{ showAnswers ? '隐藏答案' : '显示答案' }}
          </el-button>
          <el-button @click="router.push('/home')" size="large">返回首页</el-button>
        </div>

        <div v-for="(q, i) in questions" :key="q.id" class="review-item card" :class="{ wrong: answerResults[i] === false }">
          <div class="review-header">
            <span class="review-num">{{ i + 1 }}</span>
            <el-tag :type="answerResults[i] ? 'success' : 'danger'" size="small">{{ answerResults[i] ? '正确' : '错误' }}</el-tag>
            <el-tag size="small" type="info">{{ getTypeName(q.type) }}</el-tag>
          </div>
          <p class="review-content">{{ q.content }}</p>
          <div v-if="q.options" class="review-options">
            <div v-for="(opt, j) in q.options" :key="j">{{ String.fromCharCode(65 + j) }}. {{ opt }}</div>
          </div>
          <div class="review-answer">你的答案：<strong>{{ userAnswers[i] || '未作答' }}</strong></div>
          <div v-if="showAnswers" class="review-correct">
            正确答案：<strong>{{ formatAnswer(q.answer) }}</strong>
            <p v-if="q.analysis" class="analysis">💡 {{ q.analysis }}</p>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPracticeQuestions, getQuestionsByIds } from '@/api/question'
import { submitPractice } from '@/api/practice'

const route = useRoute()
const router = useRouter()
const subjectId = Number(route.params.subjectId)
const questionIdsParam = route.query.questionIds as string

const loading = ref(true)
const submitting = ref(false)
const questions = ref<any[]>([])
const currentIndex = ref(0)
const userAnswers = ref<string[]>([])
const multiSelected = ref<string[]>([])
const finished = ref(false)
const showAnswers = ref(false)
const result = ref<any>(null)
const answerResults = ref<boolean[]>([])
const isWrongPractice = ref(false)

const currentQuestion = computed(() => questions.value[currentIndex.value] || {})
const resultClass = computed(() => !result.value ? '' : Number(result.value.accuracy) >= 60 ? 'pass' : 'fail')

const typeNames: Record<number, string> = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
const getTypeName = (t: number) => typeNames[t] || '未知'

const formatAnswer = (answer: any) => {
  if (Array.isArray(answer)) return answer.join(', ')
  if (typeof answer === 'boolean') return answer ? '正确' : '错误'
  return String(answer).replace(/"/g, '')
}

const selectAnswer = (val: string) => { userAnswers.value[currentIndex.value] = val }

const toggleMulti = (val: string) => {
  const idx = multiSelected.value.indexOf(val)
  if (idx >= 0) multiSelected.value.splice(idx, 1)
  else multiSelected.value.push(val)
  userAnswers.value[currentIndex.value] = JSON.stringify(multiSelected.value.sort())
}

const syncMulti = () => {
  if (currentQuestion.value.type === 2) {
    try { multiSelected.value = JSON.parse(userAnswers.value[currentIndex.value] || '[]') } catch { multiSelected.value = [] }
  }
}

const prevQuestion = () => { if (currentIndex.value > 0) { currentIndex.value--; syncMulti() } }
const nextQuestion = () => { if (currentIndex.value < questions.value.length - 1) { currentIndex.value++; syncMulti() } }

const handleSubmit = async () => {
  const unanswered = userAnswers.value.filter((a, i) => i < questions.value.length && !a).length
  if (unanswered > 0) {
    try { await ElMessageBox.confirm(`还有 ${unanswered} 题未作答，确定提交吗？`, '提示', { confirmButtonText: '确定提交', cancelButtonText: '继续答题', type: 'warning' }) } catch { return }
  }
  submitting.value = true
  try {
    const answers = questions.value.map((q, i) => ({ questionId: q.id, userAnswer: userAnswers.value[i] || '', sortOrder: i }))
    const submitData: any = { answers }
    if (!isWrongPractice.value) {
      submitData.subjectId = subjectId
    }
    const res: any = await submitPractice(submitData)
    result.value = res.data
    answerResults.value = questions.value.map((q, i) => checkLocal(q, userAnswers.value[i] || ''))
    finished.value = true
    ElMessage.success('提交成功')
  } finally { submitting.value = false }
}

const checkLocal = (q: any, ua: string): boolean => {
  if (!ua) return false
  const ans = q.answer
  const ansStr = typeof ans === 'string' ? ans : JSON.stringify(ans)
  if (q.type === 2) {
    try { const c = (typeof ans === 'string' ? JSON.parse(ans) : ans) as string[]; const u = JSON.parse(ua) as string[]; return JSON.stringify(c.sort()) === JSON.stringify(u.sort()) } catch { return false }
  }
  if (q.type === 5) return ua.trim().length > 0
  return ansStr.replace(/"/g, '').trim().toLowerCase() === ua.replace(/"/g, '').trim().toLowerCase()
}

onMounted(async () => {
  try {
    if (questionIdsParam) {
      isWrongPractice.value = true
      const questionIds = JSON.parse(questionIdsParam)
      const idsStr = questionIds.join(',')
      const res: any = await getQuestionsByIds(idsStr)
      questions.value = res.data
    } else {
      const res: any = await getPracticeQuestions(subjectId, 20)
      questions.value = res.data
    }
    userAnswers.value = new Array(questions.value.length).fill('')
  } finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.loading-state {
  text-align: center;
  padding: 80px;
  p { color: var(--text-muted); margin-top: 16px; }
}

.loader-ring {
  width: 40px;
  height: 40px;
  border: 3px solid var(--cyber-border);
  border-top-color: var(--neon-cyan);
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 1s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.progress-bar { margin-bottom: 24px; }
.progress-info { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 13px; color: var(--text-muted); }
.progress-label { font-family: 'Orbitron', sans-serif; font-size: 10px; letter-spacing: 2px; color: var(--neon-cyan); }
.progress-track { height: 4px; background: rgba(0, 243, 255, 0.1); border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple)); border-radius: 2px; transition: width 0.3s; box-shadow: 0 0 8px rgba(0, 243, 255, 0.4); }

.question-card { padding: 32px; margin-bottom: 24px; }
.question-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.question-content { font-size: 18px; font-weight: 700; line-height: 1.6; margin-bottom: 24px; }

.diff-dots { display: flex; gap: 3px; }
.diff-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--cyber-border); &.active { background: var(--neon-amber); box-shadow: 0 0 4px rgba(251, 191, 36, 0.5); } }

.options { display: flex; flex-direction: column; gap: 12px; }

.option-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 20px;
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;

  &:hover { border-color: var(--cyber-border-strong); background: rgba(0, 243, 255, 0.03); }
  &.selected {
    border-color: var(--neon-cyan);
    background: rgba(0, 243, 255, 0.06);
    box-shadow: var(--glow-sm);
    .option-label { background: var(--neon-cyan); color: #0a0e1a; }
  }
}

.option-label {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 243, 255, 0.1);
  border: 1px solid var(--cyber-border);
  font-weight: 700;
  font-size: 12px;
  flex-shrink: 0;
  transition: all 0.3s;
  font-family: 'Orbitron', sans-serif;
  color: var(--text-secondary);
}

.action-bar { display: flex; align-items: center; justify-content: space-between; gap: 16px; }

.question-dots { display: flex; gap: 6px; flex-wrap: wrap; justify-content: center; }

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--cyber-border);
  cursor: pointer;
  transition: all 0.2s;
  &.answered { background: rgba(0, 243, 255, 0.4); }
  &.active { background: var(--neon-cyan); transform: scale(1.3); box-shadow: 0 0 6px rgba(0, 243, 255, 0.5); }
}

/* Review */
.result-banner {
  text-align: center;
  padding: 36px;
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--cyber-border);

  &.pass { background: linear-gradient(135deg, rgba(34, 211, 238, 0.1), rgba(34, 211, 238, 0.03)); border-color: rgba(34, 211, 238, 0.3); }
  &.fail { background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(239, 68, 68, 0.03)); border-color: rgba(239, 68, 68, 0.3); }

  h2 { font-family: 'Orbitron', sans-serif; font-size: 22px; margin-bottom: 20px; letter-spacing: 2px; }
}

.result-stats { display: flex; justify-content: center; gap: 48px; }
.result-item {
  text-align: center;
  .val { display: block; font-family: 'Orbitron', sans-serif; font-size: 30px; font-weight: 700; }
  .lbl { font-size: 12px; color: var(--text-muted); }
  .correct { color: var(--neon-green); text-shadow: 0 0 8px rgba(34, 211, 238, 0.4); }
  .wrong { color: var(--neon-red); }
}

.review-controls { display: flex; gap: 12px; margin-bottom: 24px; }

.review-item {
  margin-bottom: 16px;
  &.wrong { border-left: 3px solid var(--neon-red); }
}

.review-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }

.review-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: #0a0e1a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  font-family: 'Orbitron', sans-serif;
}

.review-content { font-size: 16px; font-weight: 600; margin-bottom: 12px; line-height: 1.6; }
.review-options { margin-bottom: 12px; div { padding: 3px 0; color: var(--text-secondary); font-size: 14px; } }

.review-answer {
  padding: 10px 14px;
  background: rgba(0, 243, 255, 0.04);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius);
  font-size: 14px;
  margin-bottom: 8px;
}

.review-correct {
  padding: 10px 14px;
  background: rgba(34, 211, 238, 0.06);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: var(--radius);
  font-size: 14px;
  strong { color: var(--neon-green); }
  .analysis { margin-top: 8px; color: var(--text-secondary); font-size: 13px; }
}
</style>
