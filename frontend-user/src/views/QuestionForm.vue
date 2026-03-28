<template>
  <div class="page-container">
    <h1 class="page-title">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
      {{ isEdit ? '编辑题目' : '添加题目' }}
    </h1>

    <div class="card form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="large">
        <el-form-item label="科目" prop="subjectId">
          <CyberSelect
            v-model="form.subjectId"
            :options="subjectOptions"
            placeholder="选择科目"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="题型" prop="type">
          <el-radio-group v-model="form.type" @change="onTypeChange">
            <el-radio-button :value="1">单选题</el-radio-button>
            <el-radio-button :value="2">多选题</el-radio-button>
            <el-radio-button :value="3">判断题</el-radio-button>
            <el-radio-button :value="4">填空题</el-radio-button>
            <el-radio-button :value="5">简答题</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item v-if="[1, 2].includes(form.type)" label="选项">
          <div class="options-editor">
            <div v-for="(opt, i) in form.options" :key="i" class="opt-row">
              <span class="opt-label">{{ String.fromCharCode(65 + i) }}</span>
              <el-input v-model="form.options[i]" placeholder="选项内容" />
              <el-button v-if="form.options.length > 2" text type="danger" @click="form.options.splice(i, 1)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </el-button>
            </div>
            <el-button v-if="form.options.length < 8" text type="primary" @click="form.options.push('')">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              &nbsp;添加选项
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="正确答案" prop="answer">
          <el-radio-group v-if="form.type === 1" v-model="singleAnswer">
            <el-radio v-for="(_, i) in form.options" :key="i" :value="String.fromCharCode(65 + i)">{{ String.fromCharCode(65 + i) }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-else-if="form.type === 2" v-model="multiAnswer">
            <el-checkbox v-for="(_, i) in form.options" :key="i" :value="String.fromCharCode(65 + i)" :label="String.fromCharCode(65 + i)">{{ String.fromCharCode(65 + i) }}</el-checkbox>
          </el-checkbox-group>
          <el-radio-group v-else-if="form.type === 3" v-model="judgeAnswer">
            <el-radio value="true">正确</el-radio>
            <el-radio value="false">错误</el-radio>
          </el-radio-group>
          <el-input v-else v-model="textAnswer" :type="form.type === 5 ? 'textarea' : 'text'" placeholder="请输入正确答案" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="题目解析（选填）" />
        </el-form-item>
        <el-form-item label="难度">
          <el-rate v-model="form.difficulty" :max="5" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">{{ isEdit ? '保存修改' : '添加题目' }}</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { createQuestion, updateQuestion, getQuestion } from '@/api/question'
import { getSubjects } from '@/api/subject'
import CyberSelect from '@/components/CyberSelect.vue'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref<FormInstance>()
const saving = ref(false)
const subjects = ref<any[]>([])
const singleAnswer = ref('')
const multiAnswer = ref<string[]>([])
const judgeAnswer = ref('')
const textAnswer = ref('')

const form = reactive({ subjectId: undefined as number | undefined, type: 1, content: '', options: ['', '', '', ''] as string[], answer: null as any, analysis: '', difficulty: 3, status: 1 })
const rules = { subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }], type: [{ required: true }], content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }] }

const subjectOptions = computed(() => subjects.value.map(s => ({ label: s.name, value: s.id })))

const onTypeChange = () => {
  singleAnswer.value = ''; multiAnswer.value = []; judgeAnswer.value = ''; textAnswer.value = ''
  if (![1, 2].includes(form.type)) form.options = []
  else if (form.options.length < 2) form.options = ['', '', '', '']
}

const buildAnswer = () => { switch (form.type) { case 1: return singleAnswer.value; case 2: return multiAnswer.value; case 3: return judgeAnswer.value; default: return textAnswer.value } }

const handleSave = async () => {
  await formRef.value?.validate()
  const answer = buildAnswer()
  if (!answer || (Array.isArray(answer) && answer.length === 0)) { ElMessage.warning('请设置正确答案'); return }
  saving.value = true
  try {
    const data = { ...form, answer }
    if (isEdit.value) { await updateQuestion(Number(route.params.id), data); ElMessage.success('修改成功') }
    else { await createQuestion(data); ElMessage.success('添加成功') }
    router.push('/questions')
  } finally { saving.value = false }
}

onMounted(async () => {
  const res: any = await getSubjects(); subjects.value = res.data
  if (isEdit.value) {
    const qRes: any = await getQuestion(Number(route.params.id)); const q = qRes.data
    form.subjectId = q.subjectId; form.type = q.type; form.content = q.content; form.options = q.options || []; form.analysis = q.analysis || ''; form.difficulty = q.difficulty || 3; form.status = q.status
    const ans = q.answer
    switch (q.type) { case 1: singleAnswer.value = String(ans); break; case 2: multiAnswer.value = Array.isArray(ans) ? ans : JSON.parse(String(ans)); break; case 3: judgeAnswer.value = String(ans); break; default: textAnswer.value = String(ans) }
  }
})
</script>

<style scoped lang="scss">
.form-card { max-width: 800px; padding: 32px; }
.options-editor { width: 100%; }
.opt-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.opt-label {
  width: 28px; height: 28px; border-radius: 50%;
  background: rgba(0, 243, 255, 0.1); border: 1px solid rgba(0, 243, 255, 0.3);
  color: var(--neon-cyan); display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 12px; flex-shrink: 0; font-family: 'Orbitron', sans-serif;
}
</style>
