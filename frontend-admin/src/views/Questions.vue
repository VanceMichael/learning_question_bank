<template>
  <div>
    <div class="page-top">
      <h1 class="page-heading">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        题目管理
      </h1>
      <el-button type="primary" @click="openDialog()">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        &nbsp;新增题目
      </el-button>
    </div>

    <div class="filter-bar">
      <CyberSelect
        v-model="filters.subjectId"
        :options="subjectOptions"
        placeholder="全部科目"
        clearable
        style="width:160px"
        @change="loadData"
      />
      <CyberSelect
        v-model="filters.type"
        :options="typeOptions"
        placeholder="全部题型"
        clearable
        style="width:140px"
        @change="loadData"
      />
      <el-input v-model="filters.keyword" placeholder="搜索题目..." clearable style="width:260px" @clear="loadData" @keyup.enter="loadData">
        <template #prefix>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        </template>
      </el-input>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <div class="sci-card">
      <el-table :data="questions" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="题型" width="90">
          <template #default="{ row }">
            <el-tag size="small">{{ typeNames[row.type] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="subjectName" label="科目" width="100" />
        <el-table-column label="难度" width="80">
          <template #default="{ row }">
            <span class="diff-dots">
              <span v-for="i in 5" :key="i" class="diff-dot" :class="{ active: i <= (row.difficulty || 3) }"></span>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:center;margin-top:20px">
        <el-pagination v-model:current-page="filters.current" :page-size="filters.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑题目' : '新增题目'" width="700px" top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="科目" prop="subjectId">
          <CyberSelect
            v-model="form.subjectId"
            :options="subjectOptions"
            placeholder="选择科目"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="题型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button :value="1">单选</el-radio-button>
            <el-radio-button :value="2">多选</el-radio-button>
            <el-radio-button :value="3">判断</el-radio-button>
            <el-radio-button :value="4">填空</el-radio-button>
            <el-radio-button :value="5">简答</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item v-if="[1,2].includes(form.type)" label="选项">
          <div style="width:100%">
            <div v-for="(_, i) in form.options" :key="i" style="display:flex;gap:8px;margin-bottom:8px;align-items:center">
              <span class="opt-letter">{{ String.fromCharCode(65+i) }}</span>
              <el-input v-model="form.options[i]" />
              <el-button v-if="form.options.length > 2" text type="danger" @click="form.options.splice(i,1)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </el-button>
            </div>
            <el-button v-if="form.options.length < 8" text type="primary" @click="form.options.push('')">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              &nbsp;添加
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answerStr" :placeholder="answerPlaceholder" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="难度">
          <el-rate v-model="form.difficulty" :max="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { getSubjects, getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api'
import CyberSelect from '@/components/CyberSelect.vue'

const subjects = ref<any[]>([])
const questions = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const saving = ref(false)
const formRef = ref<FormInstance>()
const typeNames: Record<number, string> = { 1: '单选', 2: '多选', 3: '判断', 4: '填空', 5: '简答' }

const filters = reactive({ current: 1, size: 20, subjectId: undefined as any, type: undefined as any, keyword: '' })

const subjectOptions = computed(() => subjects.value.map(s => ({ label: s.name, value: s.id })))
const typeOptions = computed(() => Object.entries(typeNames).map(([k, v]) => ({ label: v, value: Number(k) })))

const form = reactive({
  subjectId: undefined as any, type: 1, content: '', options: ['', '', '', ''] as string[],
  answerStr: '', analysis: '', difficulty: 3, status: 1,
})

const rules = {
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  type: [{ required: true }],
  content: [{ required: true, message: '请输入题目', trigger: 'blur' }],
  answerStr: [{ required: true, message: '请输入答案', trigger: 'blur' }],
}

const answerPlaceholder = computed(() => {
  switch (form.type) {
    case 1: return '输入选项字母，如 A'
    case 2: return '输入选项字母用逗号分隔，如 A,B,C'
    case 3: return '输入 true 或 false'
    default: return '输入正确答案'
  }
})

const loadData = async () => {
  const res: any = await getQuestions(filters)
  questions.value = res.data.records
  total.value = res.data.total
}

const openDialog = (row?: any) => {
  if (row) {
    editId.value = row.id
    form.subjectId = row.subjectId; form.type = row.type; form.content = row.content
    form.options = row.options || ['', '', '', '']; form.analysis = row.analysis || ''
    form.difficulty = row.difficulty || 3; form.status = row.status
    const ans = row.answer
    form.answerStr = Array.isArray(ans) ? ans.join(',') : String(ans).replace(/"/g, '')
  } else {
    editId.value = null
    Object.assign(form, { subjectId: undefined, type: 1, content: '', options: ['','','',''], answerStr: '', analysis: '', difficulty: 3, status: 1 })
  }
  dialogVisible.value = true
}

const buildAnswer = () => {
  if (form.type === 2) return form.answerStr.split(',').map(s => s.trim().toUpperCase())
  return form.answerStr.trim()
}

const handleSave = async () => {
  await formRef.value?.validate()
  saving.value = true
  try {
    const data = {
      subjectId: form.subjectId, type: form.type, content: form.content,
      options: [1, 2].includes(form.type) ? form.options.filter(o => o) : undefined,
      answer: buildAnswer(), analysis: form.analysis, difficulty: form.difficulty, status: form.status,
    }
    if (editId.value) { await updateQuestion(editId.value, data); ElMessage.success('修改成功') }
    else { await createQuestion(data); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } finally { saving.value = false }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteQuestion(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  const res: any = await getSubjects(true)
  subjects.value = res.data
  loadData()
})
</script>

<style scoped lang="scss">
.page-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Orbitron', sans-serif;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
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

.diff-dots {
  display: flex;
  gap: 3px;
}

.diff-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--cyber-border);
  &.active { background: var(--neon-amber); box-shadow: 0 0 4px rgba(251, 191, 36, 0.5); }
}

.opt-letter {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(0, 243, 255, 0.1);
  border: 1px solid rgba(0, 243, 255, 0.3);
  color: var(--neon-cyan);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
  flex-shrink: 0;
  font-family: 'Orbitron', sans-serif;
}
</style>
