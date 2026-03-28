<template>
  <div class="page-container">
    <div v-if="loading" class="loading-state">
      <div class="loader-ring"></div>
    </div>

    <template v-else>
      <div class="review-header-bar">
        <el-button @click="router.back()">← 返回</el-button>
        <h1 class="page-title" style="margin:0">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          练习回顾
        </h1>
        <el-button type="primary" @click="showAnswers = !showAnswers">{{ showAnswers ? '隐藏答案' : '显示答案' }}</el-button>
      </div>

      <div class="result-summary card">
        <div class="summary-item"><span class="label">总题数</span><span class="value">{{ record?.totalCount }}</span></div>
        <div class="summary-item"><span class="label">正确</span><span class="value correct">{{ record?.correctCount }}</span></div>
        <div class="summary-item"><span class="label">错误</span><span class="value wrong">{{ (record?.totalCount || 0) - (record?.correctCount || 0) }}</span></div>
        <div class="summary-item"><span class="label">正确率</span><span class="value">{{ record?.accuracy }}%</span></div>
      </div>

      <div v-for="(d, i) in details" :key="d.id" class="detail-item card" :class="{ wrong: d.isCorrect === 0 }">
        <div class="detail-header">
          <span class="detail-num">{{ i + 1 }}</span>
          <el-tag :type="d.isCorrect ? 'success' : 'danger'" size="small">{{ d.isCorrect ? '正确' : '错误' }}</el-tag>
        </div>
        <p class="detail-content">{{ d.question?.content }}</p>
        <div v-if="d.question?.options" class="detail-options">
          <div v-for="(opt, j) in d.question.options" :key="j">{{ String.fromCharCode(65 + j) }}. {{ opt }}</div>
        </div>
        <div class="detail-answer">你的答案：{{ d.userAnswer || '未作答' }}</div>
        <div v-if="showAnswers" class="detail-correct">
          正确答案：<strong>{{ formatAnswer(d.question?.answer) }}</strong>
          <p v-if="d.question?.analysis" class="analysis">💡 {{ d.question.analysis }}</p>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRecordDetail, getRecordDetails } from '@/api/practice'

const route = useRoute()
const router = useRouter()
const recordId = Number(route.params.recordId)
const loading = ref(true)
const record = ref<any>(null)
const details = ref<any[]>([])
const showAnswers = ref(false)

const formatAnswer = (answer: any) => {
  if (!answer) return ''
  if (Array.isArray(answer)) return answer.join(', ')
  return String(answer).replace(/"/g, '')
}

onMounted(async () => {
  try {
    const [rRes, dRes]: any[] = await Promise.all([getRecordDetail(recordId), getRecordDetails(recordId)])
    record.value = rRes.data; details.value = dRes.data
  } finally { loading.value = false }
})
</script>

<style scoped lang="scss">
.loading-state { text-align: center; padding: 60px; }
.loader-ring { width: 32px; height: 32px; border: 3px solid var(--cyber-border); border-top-color: var(--neon-cyan); border-radius: 50%; margin: 0 auto; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.review-header-bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }

.result-summary {
  display: flex;
  justify-content: space-around;
  padding: 28px;
  margin-bottom: 24px;
}

.summary-item {
  text-align: center;
  .label { display: block; font-size: 12px; color: var(--text-muted); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 1px; }
  .value { font-family: 'Orbitron', sans-serif; font-size: 28px; font-weight: 700; }
  .correct { color: var(--neon-green); text-shadow: 0 0 8px rgba(34, 211, 238, 0.3); }
  .wrong { color: var(--neon-red); }
}

.detail-item {
  margin-bottom: 16px;
  padding: 20px;
  &.wrong { border-left: 3px solid var(--neon-red); }
}

.detail-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }

.detail-num {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
  color: #0a0e1a; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700; font-family: 'Orbitron', sans-serif;
}

.detail-content { font-size: 15px; font-weight: 600; margin-bottom: 12px; }
.detail-options { margin-bottom: 12px; font-size: 14px; color: var(--text-secondary); div { padding: 2px 0; } }

.detail-answer {
  padding: 10px 14px;
  background: rgba(0, 243, 255, 0.04);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius);
  font-size: 14px;
  margin-bottom: 8px;
}

.detail-correct {
  padding: 10px 14px;
  background: rgba(34, 211, 238, 0.06);
  border: 1px solid rgba(34, 211, 238, 0.2);
  border-radius: var(--radius);
  font-size: 14px;
  strong { color: var(--neon-green); }
  .analysis { margin-top: 8px; color: var(--text-secondary); font-size: 13px; }
}
</style>
