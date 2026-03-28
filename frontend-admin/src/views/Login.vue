<template>
  <div class="login-page">
    <div class="grid-bg"></div>
    <div class="particles">
      <span v-for="i in 20" :key="i" class="particle" :style="particleStyle(i)"></span>
    </div>
    <div class="login-card">
      <div class="card-border"></div>
      <div class="login-inner">
        <div class="login-header">
          <svg class="logo-svg" width="48" height="48" viewBox="0 0 28 28" fill="none">
            <path d="M14 2L26 8v12l-12 6L2 20V8l12-6z" stroke="url(#lg1)" stroke-width="1.5" fill="none"/>
            <circle cx="14" cy="14" r="3" fill="#00f3ff"/>
            <defs><linearGradient id="lg1" x1="2" y1="2" x2="26" y2="26"><stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/></linearGradient></defs>
          </svg>
          <h1>NEXUS ADMIN</h1>
          <p>管理中枢 · 身份验证</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="管理员账号" size="large">
              <template #prefix>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="访问密钥" size="large" show-password>
              <template #prefix>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0110 0v4"/></svg>
              </template>
            </el-input>
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
            <span v-if="!loading">进 入 系 统</span>
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const particleStyle = (i: number) => ({
  left: `${Math.random() * 100}%`,
  top: `${Math.random() * 100}%`,
  animationDelay: `${i * 0.3}s`,
  animationDuration: `${3 + Math.random() * 4}s`,
  width: `${2 + Math.random() * 3}px`,
  height: `${2 + Math.random() * 3}px`,
})

const handleLogin = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res: any = await login(form)
    if (res.data.user.role !== 1) {
      ElMessage.error('该账号不是管理员')
      return
    }
    userStore.setToken(res.data.token)
    userStore.userInfo = res.data.user
    ElMessage.success('身份验证通过')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
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

.particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  background: var(--neon-cyan);
  border-radius: 50%;
  opacity: 0;
  animation: particleFade 4s ease-in-out infinite;
}

@keyframes particleFade {
  0%, 100% { opacity: 0; transform: translateY(0); }
  50% { opacity: 0.6; transform: translateY(-30px); }
}

.login-card {
  width: 420px;
  position: relative;
  z-index: 1;
  animation: cardEnter 0.8s ease-out;
}

@keyframes cardEnter {
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
  margin-bottom: 40px;

  .logo-svg {
    filter: drop-shadow(0 0 12px rgba(0, 243, 255, 0.5));
    margin-bottom: 16px;
  }

  h1 {
    font-family: 'Orbitron', sans-serif;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 4px;
    background: linear-gradient(90deg, #00f3ff, #a855f7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  p {
    color: var(--text-muted);
    font-size: 13px;
    margin-top: 8px;
    letter-spacing: 2px;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-family: 'Rajdhani', sans-serif;
  font-size: 16px;
  letter-spacing: 4px;
  margin-top: 8px;
}
</style>
