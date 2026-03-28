<template>
  <div>
    <h1 class="page-heading">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
      数据总览
    </h1>

    <div class="stat-cards">
      <div v-for="card in statCards" :key="card.label" class="stat-card" :style="{ '--accent': card.color }">
        <div class="stat-icon-box">
          <span v-html="card.svg"></span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
        <div class="stat-glow"></div>
      </div>
    </div>

    <div class="chart-section">
      <div class="chart-card">
        <div class="chart-header">
          <h3>各科目题目分布</h3>
          <span class="chart-badge">REALTIME</span>
        </div>
        <v-chart :option="chartOption" autoresize style="height:320px" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getSubjects, getQuestions, getUsers } from '@/api'

use([BarChart, TitleComponent, TooltipComponent, GridComponent, CanvasRenderer])

const subjects = ref<any[]>([])
const totalQuestions = ref(0)
const totalUsers = ref(0)
const questionsBySubject = ref<any[]>([])

const statCards = computed(() => [
  { svg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 016.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/></svg>', label: '科目数', value: subjects.value.length, color: '#00f3ff' },
  { svg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>', label: '题目总数', value: totalQuestions.value, color: '#a855f7' },
  { svg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>', label: '用户数', value: totalUsers.value, color: '#22d3ee' },
])

const chartOption = computed(() => ({
  tooltip: { trigger: 'axis', backgroundColor: '#1a2035', borderColor: 'rgba(0,243,255,0.3)', textStyle: { color: '#e2e8f0', fontFamily: 'Rajdhani' } },
  grid: { left: 60, right: 20, bottom: 40, top: 20 },
  xAxis: { type: 'category', data: questionsBySubject.value.map(s => s.name), axisLine: { lineStyle: { color: 'rgba(0,243,255,0.15)' } }, axisLabel: { color: '#94a3b8', fontFamily: 'Rajdhani' } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: 'rgba(0,243,255,0.06)' } }, axisLabel: { color: '#94a3b8', fontFamily: 'Rajdhani' } },
  series: [{
    type: 'bar',
    data: questionsBySubject.value.map(s => ({
      value: s.count,
      itemStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#00f3ff' }, { offset: 1, color: 'rgba(0,243,255,0.2)' }] },
        borderRadius: [4, 4, 0, 0],
      },
    })),
    barWidth: 36,
  }],
}))

onMounted(async () => {
  try {
    const [sRes, qRes, uRes]: any[] = await Promise.all([
      getSubjects(true),
      getQuestions({ current: 1, size: 1 }),
      getUsers({ current: 1, size: 1 }),
    ])
    subjects.value = sRes.data
    totalQuestions.value = qRes.data.total
    totalUsers.value = uRes.data.total

    const counts: any[] = []
    for (const s of sRes.data) {
      const r: any = await getQuestions({ current: 1, size: 1, subjectId: s.id })
      counts.push({ name: s.name, count: r.data.total })
    }
    questionsBySubject.value = counts
  } catch {}
})
</script>

<style scoped lang="scss">
.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Orbitron', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 28px;
  letter-spacing: 1px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    border-color: var(--accent);
    box-shadow: 0 0 20px color-mix(in srgb, var(--accent) 20%, transparent);
  }
}

.stat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--accent) 10%, transparent);
  border: 1px solid color-mix(in srgb, var(--accent) 25%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent);
  flex-shrink: 0;
}

.stat-value {
  font-family: 'Orbitron', sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 2px;
}

.stat-glow {
  position: absolute;
  right: -20px;
  top: -20px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--accent);
  opacity: 0.03;
  filter: blur(20px);
}

.chart-section {
  display: flex;
  gap: 24px;
}

.chart-card {
  flex: 1;
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius-lg);
  padding: 24px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  h3 {
    font-family: 'Rajdhani', sans-serif;
    font-size: 16px;
    font-weight: 700;
    color: var(--text-primary);
  }
}

.chart-badge {
  font-family: 'Orbitron', sans-serif;
  font-size: 10px;
  letter-spacing: 2px;
  color: var(--neon-green);
  background: rgba(34, 211, 238, 0.1);
  border: 1px solid rgba(34, 211, 238, 0.3);
  padding: 4px 10px;
  border-radius: 4px;
}
</style>
