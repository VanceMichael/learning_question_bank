<template>
  <div class="page-container">
    <h1 class="page-title">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
      学习统计
    </h1>

    <div class="stats-overview">
      <div v-for="item in overviewCards" :key="item.label" class="stat-card card" :style="{ '--accent': item.color }">
        <div class="stat-icon-box"><span v-html="item.svg"></span></div>
        <div>
          <div class="stat-val">{{ item.value }}</div>
          <div class="stat-lbl">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <div class="card chart-card">
        <h3>各科目正确率</h3>
        <v-chart :option="barOption" autoresize style="height: 320px" />
      </div>
      <div class="card chart-card">
        <h3>答题分布</h3>
        <v-chart :option="pieOption" autoresize style="height: 320px" />
      </div>
    </div>

    <div class="card" style="margin-top: 24px" v-if="stats?.recentRecords?.length">
      <h3 style="margin-bottom: 16px; font-family: 'Rajdhani', sans-serif; font-weight: 700;">最近练习</h3>
      <el-table :data="stats.recentRecords" stripe>
        <el-table-column prop="subjectName" label="科目" />
        <el-table-column prop="totalCount" label="题数" width="80" />
        <el-table-column prop="correctCount" label="正确" width="80" />
        <el-table-column label="正确率" width="120">
          <template #default="{ row }">
            <el-tag :type="Number(row.accuracy) >= 60 ? 'success' : 'danger'" size="small">{{ row.accuracy }}%</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="finishedAt" label="时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getStats } from '@/api/practice'

use([BarChart, PieChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

const stats = ref<any>(null)

const overviewCards = computed(() => {
  if (!stats.value) return []
  return [
    { svg: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>', label: '练习次数', value: stats.value.totalPractices, color: '#00f3ff' },
    { svg: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>', label: '答题总数', value: stats.value.totalQuestions, color: '#a855f7' },
    { svg: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>', label: '正确数', value: stats.value.totalCorrect, color: '#22d3ee' },
    { svg: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>', label: '总正确率', value: `${stats.value.overallAccuracy}%`, color: '#f472b6' },
  ]
})

const barOption = computed(() => {
  const ss = stats.value?.subjectStats || []
  return {
    tooltip: { trigger: 'axis', backgroundColor: '#1a2035', borderColor: 'rgba(0,243,255,0.3)', textStyle: { color: '#e2e8f0', fontFamily: 'Rajdhani' } },
    grid: { left: 60, right: 20, bottom: 40, top: 20 },
    xAxis: { type: 'category', data: ss.map((s: any) => s.subjectName), axisLine: { lineStyle: { color: 'rgba(0,243,255,0.15)' } }, axisLabel: { color: '#94a3b8', fontFamily: 'Rajdhani' } },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%', color: '#94a3b8', fontFamily: 'Rajdhani' }, splitLine: { lineStyle: { color: 'rgba(0,243,255,0.06)' } } },
    series: [{
      type: 'bar',
      data: ss.map((s: any) => ({
        value: Number(s.accuracy),
        itemStyle: {
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: Number(s.accuracy) >= 60 ? '#22d3ee' : '#ef4444' }, { offset: 1, color: Number(s.accuracy) >= 60 ? 'rgba(34,211,238,0.2)' : 'rgba(239,68,68,0.2)' }] },
          borderRadius: [4, 4, 0, 0],
        },
      })),
      barWidth: 36,
    }],
  }
})

const pieOption = computed(() => {
  if (!stats.value) return {}
  const correct = stats.value.totalCorrect
  const wrong = stats.value.totalQuestions - correct
  return {
    tooltip: { trigger: 'item', backgroundColor: '#1a2035', borderColor: 'rgba(0,243,255,0.3)', textStyle: { color: '#e2e8f0', fontFamily: 'Rajdhani' } },
    legend: { bottom: 0, textStyle: { color: '#94a3b8', fontFamily: 'Rajdhani' } },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#1a2035', borderWidth: 3 },
      label: { show: true, formatter: '{b}: {c}题', color: '#94a3b8', fontFamily: 'Rajdhani' },
      data: [
        { value: correct, name: '正确', itemStyle: { color: '#22d3ee' } },
        { value: wrong, name: '错误', itemStyle: { color: '#ef4444' } },
      ],
    }],
  }
})

onMounted(async () => { try { const res: any = await getStats(); stats.value = res.data } catch {} })
</script>

<style scoped lang="scss">
.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon-box {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--accent) 10%, transparent);
  border: 1px solid color-mix(in srgb, var(--accent) 25%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent);
  flex-shrink: 0;
}

.stat-val {
  font-family: 'Orbitron', sans-serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-lbl { font-size: 13px; color: var(--text-muted); }

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-card {
  h3 {
    font-family: 'Rajdhani', sans-serif;
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 16px;
  }
}
</style>
