<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        我的题库
      </h1>
      <el-button type="primary" @click="router.push('/questions/add')">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        &nbsp;添加题目
      </el-button>
    </div>

    <div class="filter-bar card">
      <CyberSelect
        v-model="filters.subjectId"
        :options="subjectOptions"
        placeholder="全部科目"
        clearable
        @change="loadData"
      />
      <CyberSelect
        v-model="filters.type"
        :options="typeOptions"
        placeholder="全部题型"
        clearable
        @change="loadData"
      />
      <el-input v-model="filters.keyword" placeholder="搜索题目..." clearable style="width:260px" @clear="loadData" @keyup.enter="loadData">
        <template #prefix><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg></template>
      </el-input>
    </div>

    <div class="question-list">
      <div v-for="q in questions" :key="q.id" class="question-item card">
        <div class="q-header">
          <div class="q-tags">
            <el-tag size="small">{{ typeNames[q.type] || '未知' }}</el-tag>
            <el-tag size="small" type="info">{{ q.subjectName }}</el-tag>
            <el-tag size="small" :type="q.status === 1 ? 'success' : 'warning'">{{ q.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </div>
          <div class="q-actions">
            <el-button text type="primary" @click="router.push(`/questions/edit/${q.id}`)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              &nbsp;编辑
            </el-button>
            <el-button text type="danger" @click="handleDelete(q.id)">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
              &nbsp;删除
            </el-button>
          </div>
        </div>
        <p class="q-content">{{ q.content }}</p>
        <div v-if="q.options" class="q-options">
          <span v-for="(opt, i) in q.options" :key="i">{{ String.fromCharCode(65 + i) }}. {{ opt }}</span>
        </div>
      </div>
    </div>

    <div class="pagination-bar" v-if="total > 0">
      <el-pagination v-model:current-page="filters.current" :page-size="filters.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestions, deleteQuestion } from '@/api/question'
import { getSubjects } from '@/api/subject'
import CyberSelect from '@/components/CyberSelect.vue'

const router = useRouter()
const questions = ref<any[]>([])
const subjects = ref<any[]>([])
const total = ref(0)
const typeNames: Record<number, string> = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }

const filters = reactive({ current: 1, size: 20, subjectId: undefined as number | undefined, type: undefined as number | undefined, keyword: '' })

const subjectOptions = computed(() => subjects.value.map(s => ({ label: s.name, value: s.id })))
const typeOptions = computed(() => Object.entries(typeNames).map(([k, v]) => ({ label: v, value: Number(k) })))

const loadData = async () => {
  try { const res: any = await getQuestions(filters); questions.value = res.data.records; total.value = res.data.total } catch {}
}

const handleDelete = async (id: number) => {
  try { await ElMessageBox.confirm('确定删除该题目吗？', '提示', { type: 'warning' }); await deleteQuestion(id); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(async () => { const res: any = await getSubjects(); subjects.value = res.data; loadData() })
</script>

<style scoped lang="scss">
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 20px; padding: 16px; }
.question-list { display: flex; flex-direction: column; gap: 12px; }
.question-item { padding: 20px; }
.q-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.q-tags { display: flex; gap: 8px; }
.q-content { font-size: 15px; font-weight: 600; line-height: 1.6; margin-bottom: 8px; }
.q-options { display: flex; flex-wrap: wrap; gap: 16px; span { font-size: 13px; color: var(--text-secondary); } }
.pagination-bar { display: flex; justify-content: center; margin-top: 24px; }
</style>
