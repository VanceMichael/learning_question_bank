<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <div class="header-left">
          <router-link to="/home" class="brand">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <path d="M14 2L26 8v12l-12 6L2 20V8l12-6z" stroke="url(#hg1)" stroke-width="1.5" fill="none"/>
              <circle cx="14" cy="14" r="3" fill="#00f3ff"/>
              <defs><linearGradient id="hg1" x1="2" y1="2" x2="26" y2="26"><stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/></linearGradient></defs>
            </svg>
            <span class="brand-text">QUANTUM</span>
          </router-link>
          <nav class="nav">
            <router-link to="/home" class="nav-item" active-class="active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>
              首页
            </router-link>
            <router-link to="/questions" class="nav-item" active-class="active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
              题库
            </router-link>
            <router-link to="/records" class="nav-item" active-class="active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
              记录
            </router-link>
            <router-link to="/stats" class="nav-item" active-class="active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
              统计
            </router-link>
            <router-link to="/wrong-questions" class="nav-item" active-class="active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
              错题本
            </router-link>
          </nav>
        </div>
        <div class="header-right">
          <CyberDropdown>
            <template #trigger>
              <div class="user-info">
                <div class="avatar-ring">
                  <el-avatar :size="30">{{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}</el-avatar>
                </div>
                <span class="username">{{ userStore.userInfo?.nickname || '用户' }}</span>
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
              </div>
            </template>
            <CyberDropdownItem danger @click="handleLogout">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
              退出登录
            </CyberDropdownItem>
          </CyberDropdown>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import CyberDropdown from '@/components/CyberDropdown.vue'
import CyberDropdownItem from '@/components/CyberDropdownItem.vue'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: var(--cyber-surface);
  border-bottom: 1px solid var(--cyber-border);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(12px);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 36px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;

  svg { filter: drop-shadow(0 0 6px rgba(0, 243, 255, 0.4)); }

  .brand-text {
    font-family: 'Orbitron', sans-serif;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 3px;
    background: linear-gradient(90deg, #00f3ff, #a855f7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.nav {
  display: flex;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius);
  text-decoration: none;
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;

  &:hover { color: var(--text-primary); background: rgba(0, 243, 255, 0.05); }
  &.active {
    color: var(--neon-cyan);
    background: rgba(0, 243, 255, 0.08);
    svg { stroke: var(--neon-cyan); filter: drop-shadow(0 0 4px rgba(0, 243, 255, 0.5)); }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius);
  transition: background 0.2s;
  color: var(--text-secondary);
  &:hover { background: rgba(0, 243, 255, 0.05); }
}

.avatar-ring {
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.main {
  flex: 1;
  padding: 24px;
}
</style>
