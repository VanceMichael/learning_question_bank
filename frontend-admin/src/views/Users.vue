<template>
  <div>
    <h1 class="page-heading">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
      用户管理
    </h1>

    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称..." clearable style="width:300px" @clear="loadData" @keyup.enter="loadData">
        <template #prefix>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </template>
      </el-input>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <div class="sci-card">
      <el-table :data="users" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'warning' : 'info'" size="small">
              {{ row.role === 1 ? 'ADMIN' : 'USER' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(val: boolean) => handleStatus(row.id, val ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text type="danger" size="small" @click="handleDelete(row.id)" :disabled="row.role === 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:center;margin-top:20px">
        <el-pagination v-model:current-page="current" :page-size="20" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUserStatus, deleteUser } from '@/api'

const users = ref<any[]>([])
const total = ref(0)
const current = ref(1)
const keyword = ref('')

const loadData = async () => {
  const res: any = await getUsers({ current: current.value, size: 20, keyword: keyword.value })
  users.value = res.data.records
  total.value = res.data.total
}

const handleStatus = async (id: number, status: number) => {
  await updateUserStatus(id, status)
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
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
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  padding: 16px;
  border-radius: var(--radius-lg);
}

.sci-card {
  background: var(--cyber-card);
  border: 1px solid var(--cyber-border);
  border-radius: var(--radius-lg);
  padding: 24px;
}
</style>
