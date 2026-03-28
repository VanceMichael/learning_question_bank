<template>
  <div class="page-container">
    <h1 class="page-title">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
      练习记录
    </h1>

    <div v-if="loading" class="loading-state">
      <div class="loader-ring"></div>
    </div>

    <el-empty v-else-if="records.length === 0" description="暂无练习记录">
      <el-button type="primary" @click="router.push('/home')">去练习</el-button>
    </el-empty>

    <div v-else class="record-list">
      <div v-for="r in records" :key="r.id" class="record-item card card-hover" @click="router.push(`/review/${r.id}`)">
        <div class="record-left">
          <div class="record-subject">{{ r.subjectName }}</div>
          <div class="record-time">{{ formatTime(r.finishedAt || r.startedAt) }}</div>
        </div>
        <div class="record-right">
          <div class="record-score">
            <span class="correct">{{ r.correctCount }}</span>
            <span class="sep">/</span>
            <span>{{ r.totalCount }}</span>
          </div>
          <el-tag :type="Number(r.accuracy) >= 60 ? 'success' : 'danger'" size="small">{{ r.accuracy }}%</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRecords } from '@/api/practice'

const router = useRouter()
const records = ref<any[]>([])
const loading = ref(true)

const formatTime = (t: string) => t ? t.replace('T', ' ').substring(0, 16) : ''

onMounted(async () => { try { const res: any = await getRecords(); records.value = res.data } finally { loading.value = false } })
</script>

<style scoped lang="scss">
.loading-state { text-align: center; padding: 60px; }
.loader-ring { width: 32px; height: 32px; border: 3px solid var(--cyber-border); border-top-color: var(--neon-cyan); border-radius: 50%; margin: 0 auto; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.record-list { display: flex; flex-direction: column; gap: 12px; }

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  cursor: pointer;
}

.record-subject { font-size: 16px; font-weight: 700; margin-bottom: 4px; }
.record-time { font-size: 13px; color: var(--text-muted); }
.record-right { display: flex; align-items: center; gap: 16px; }

.record-score {
  font-family: 'Orbitron', sans-serif;
  font-size: 20px;
  font-weight: 700;
  .correct { color: var(--neon-green); text-shadow: 0 0 6px rgba(34, 211, 238, 0.3); }
  .sep { color: var(--text-muted); margin: 0 2px; }
}
</style>
