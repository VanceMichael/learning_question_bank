<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-mark">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <path d="M14 2L26 8v12l-12 6L2 20V8l12-6z" stroke="url(#g1)" stroke-width="1.5" fill="none"/>
            <path d="M14 6l8 4v8l-8 4-8-4v-8l8-4z" fill="url(#g2)" opacity="0.3"/>
            <circle cx="14" cy="14" r="3" fill="#00f3ff"/>
            <defs>
              <linearGradient id="g1" x1="2" y1="2" x2="26" y2="26">
                <stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/>
              </linearGradient>
              <linearGradient id="g2" x1="6" y1="6" x2="22" y2="22">
                <stop stop-color="#00f3ff"/><stop offset="1" stop-color="#a855f7"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <span class="sidebar-title">NEXUS</span>
      </div>
      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <span class="nav-icon" v-html="item.svg"></span>
          <span>{{ item.title }}</span>
          <span class="nav-glow"></span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <CyberDropdown>
          <div class="admin-info">
            <div class="avatar-ring">
              <el-avatar :size="32">{{ userStore.userInfo?.nickname?.charAt(0) || 'A' }}</el-avatar>
            </div>
            <span class="admin-name">{{ userStore.userInfo?.nickname || 'Admin' }}</span>
            <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor" style="margin-left:4px;opacity:0.6">
              <path d="M2.5 4.5L6 8L9.5 4.5" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round"/>
            </svg>
          </div>
          <template #dropdown>
            <CyberDropdownItem @click="handleLogout">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
              退出登录
            </CyberDropdownItem>
          </template>
        </CyberDropdown>
      </div>
    </aside>
    <main class="main-content">
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

const menuItems = [
  { path: '/dashboard', title: '数据总览', svg: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>' },
  { path: '/subjects', title: '科目管理', svg: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 016.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/></svg>' },
  { path: '/questions', title: '题目管理', svg: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>' },
  { path: '/users', title: '用户管理', svg: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>' },
  { path: '/logs', title: '操作日志', svg: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>' },
]

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--cyber-bg);
}

.sidebar {
  width: var(--sidebar-width);
  background: var(--cyber-surface);
  border-right: 1px solid var(--cyber-border);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--cyber-border);
}

.logo-mark {
  display: flex;
  align-items: center;
  filter: drop-shadow(0 0 6px rgba(0, 243, 255, 0.4));
}

.sidebar-title {
  font-family: 'Orbitron', sans-serif;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 3px;
  background: linear-gradient(90deg, #00f3ff, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius);
  color: var(--text-muted);
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;

  .nav-icon { display: flex; align-items: center; opacity: 0.7; }
  .nav-glow {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: var(--neon-cyan);
    opacity: 0;
    transition: opacity 0.3s;
    box-shadow: 0 0 10px var(--neon-cyan);
  }

  &:hover {
    color: var(--text-primary);
    background: rgba(0, 243, 255, 0.05);
    .nav-icon { opacity: 1; }
  }

  &.active {
    color: var(--neon-cyan);
    background: rgba(0, 243, 255, 0.08);
    .nav-icon { opacity: 1; }
    .nav-glow { opacity: 1; }
    svg { stroke: var(--neon-cyan); filter: drop-shadow(0 0 4px rgba(0, 243, 255, 0.5)); }
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--cyber-border);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: var(--radius);
  transition: all 0.2s ease;

  &:hover {
    background: rgba(0, 243, 255, 0.05);
  }
}

.avatar-ring {
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
}

.admin-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.main-content {
  flex: 1;
  margin-left: var(--sidebar-width);
  padding: 28px;
  min-height: 100vh;
}
</style>
