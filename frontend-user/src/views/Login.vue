<template>
  <div class="login-page">
    <div class="grid-bg"></div>
    <div class="particles">
      <span v-for="i in 25" :key="i" class="particle" :style="particleStyle(i)"></span>
    </div>
    <div class="login-card">
      <div class="card-border"></div>
      <div class="login-inner">
        <div class="login-header">
          <svg width="52" height="52" viewBox="0 0 28 28" fill="none">
            <path d="M14 2L26 8v12l-12 6L2 20V8l12-6z" stroke="url(#ulg)" stroke-width="1.5" fill="none"/>
            <path d="M14 6l8 4v8l-8 4-8-4v-8l8-4z" fill="url(#ulg2)" opacity="0.2"/>
            <circle cx="14" cy="14" r="3" fill="#00f3ff"/>
            <defs>
              <linearGradient id="ulg" x1="2" y1="2" x2="26" y2="26"><stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/></linearGradient>
              <linearGradient id="ulg2" x1="6" y1="6" x2="22" y2="22"><stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/></linearGradient>
            </defs>
          </svg>
          <h1>QUANTUM</h1>
          <p>学习终端 · 知识引擎</p>
        </div>
        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" placeholder="用户名" size="large">
                  <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password>
                  <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg></template>
                </el-input>
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="submit-btn">
                <span v-if="!loading">进 入 系 统</span>
              </el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form ref="regFormRef" :model="regForm" :rules="regRules" @submit.prevent="handleRegister">
              <el-form-item prop="username">
                <el-input v-model="regForm.username" placeholder="用户名" size="large">
                  <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="nickname">
                <el-input v-model="regForm.nickname" placeholder="昵称（选填）" size="large">
                  <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="regForm.password" type="password" placeholder="密码" size="large" show-password>
                  <template #prefix><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg></template>
                </el-input>
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="submit-btn">
                <span v-if="!loading">创 建 账 号</span>
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')
const loading = ref(false)
const loginFormRef = ref<FormInstance>()
const regFormRef = ref<FormInstance>()

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '', nickname: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}
const regRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '长度3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 50, message: '长度6-50位', trigger: 'blur' }],
}

const particleStyle = (i: number) => ({
  left: `${Math.random() * 100}%`,
  top: `${Math.random() * 100}%`,
  animationDelay: `${i * 0.25}s`,
  animationDuration: `${3 + Math.random() * 4}s`,
  width: `${2 + Math.random() * 3}px`,
  height: `${2 + Math.random() * 3}px`,
})

const handleLogin = async () => {
  await loginFormRef.value?.validate()
  loading.value = true
  try {
    const res: any = await login(loginForm)
    userStore.setToken(res.data.token)
    userStore.userInfo = res.data.user
    ElMessage.success('身份验证通过')
    router.push('/home')
  } finally { loading.value = false }
}

const handleRegister = async () => {
  await regFormRef.value?.validate()
  loading.value = true
  try {
    const res: any = await register(regForm)
    userStore.setToken(res.data.token)
    userStore.userInfo = res.data.user
    ElMessage.success('注册成功')
    router.push('/home')
  } finally { loading.value = false }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #060a14;
  position: relative;
  overflow: hidden;
}

.grid-bg {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 243, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(60px, 60px); }
}

.particles { position: absolute; inset: 0; }

.particle {
  position: absolute;
  background: var(--neon-cyan);
  border-radius: 50%;
  opacity: 0;
  animation: pFade 4s ease-in-out infinite;
}

@keyframes pFade {
  0%, 100% { opacity: 0; transform: translateY(0); }
  50% { opacity: 0.5; transform: translateY(-30px); }
}

.login-card {
  width: 440px;
  position: relative;
  z-index: 1;
  animation: cardIn 0.8s ease-out;
}

@keyframes cardIn {
  from { opacity: 0; transform: translateY(30px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.card-border {
  position: absolute;
  inset: -1px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--neon-cyan), transparent 40%, transparent 60%, var(--neon-purple));
  z-index: -1;
}

.login-inner {
  background: var(--cyber-surface);
  border-radius: 16px;
  padding: 48px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;

  svg { filter: drop-shadow(0 0 12px rgba(0, 243, 255, 0.5)); margin-bottom: 16px; }

  h1 {
    font-family: 'Orbitron', sans-serif;
    font-size: 24px;
    font-weight: 700;
    letter-spacing: 5px;
    background: linear-gradient(90deg, #00f3ff, #a855f7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  p { color: var(--text-muted); font-size: 13px; margin-top: 8px; letter-spacing: 2px; }
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-family: 'Rajdhani', sans-serif;
  font-size: 16px;
  letter-spacing: 4px;
  margin-top: 8px;
}
</style>
