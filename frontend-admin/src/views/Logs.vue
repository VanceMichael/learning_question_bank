<template>
  <div>
    <h1 class="page-heading">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
      操作日志
    </h1>

    <div class="sci-card">
      <el-table :data="logs" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="detail" label="详情" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <div style="display:flex;justify-content:center;margin-top:20px">
        <el-pagination v-model:current-page="current" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLogs } from '@/api'

const logs = ref<any[]>([])
const total = ref(0)
const current = ref(1)

const loadData = async () => {
  const res: any = await getLogs({ current: current.value, size: 20 })
  logs.value = res.data.records
  total.value = res.data.total
}

onMounted(loadData)
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
  letter-spacing: 1px;
  margin-bottom: 24px;
}

.sci-card {
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius-lg);
  padding: 24px;
}
</style>
