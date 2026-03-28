<template>
  <div class="page-container">
    <div class="welcome-banner">
      <div class="banner-bg">
        <div class="hex hex-1"></div>
        <div class="hex hex-2"></div>
      </div>
      <div class="welcome-text">
        <h1>你好，{{ userStore.userInfo?.nickname || '同学' }}</h1>
        <p>选择一个科目开始你的训练</p>
      </div>
      <div class="welcome-stats" v-if="stats">
        <div class="stat-item" v-for="s in statItems" :key="s.label">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </div>

    <h2 class="section-title">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 016.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/></svg>
      选择科目
    </h2>
    <div class="subject-grid">
      <div v-for="(subject, idx) in subjects" :key="subject.id" class="subject-card card card-hover" @click="startPractice(subject)" :style="{ animationDelay: `${idx * 0.08}s` }">
        <div class="subject-icon-box" :style="{ '--c': colors[idx % colors.length] }">
          <span v-html="subjectSvg(subject.icon)"></span>
        </div>
        <h3>{{ subject.name }}</h3>
        <p>{{ subject.description || '点击开始练习' }}</p>
        <button class="start-btn">开始训练 →</button>
      </div>
    </div>

    <h2 class="section-title" style="margin-top: 36px">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
      快捷操作
    </h2>
    <div class="quick-actions">
      <div class="action-card card card-hover" @click="router.push('/questions/add')">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--neon-cyan)" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        <span>添加题目</span>
      </div>
      <div class="action-card card card-hover" @click="router.push('/questions')">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--neon-purple)" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
        <span>管理题库</span>
      </div>
      <div class="action-card card card-hover" @click="router.push('/stats')">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--neon-green)" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
        <span>查看统计</span>
      </div>
      <div class="action-card card card-hover" @click="router.push('/records')">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--neon-amber)" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
        <span>练习记录</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getSubjects } from '@/api/subject'
import { getStats } from '@/api/practice'

const router = useRouter()
const userStore = useUserStore()
const subjects = ref<any[]>([])
const stats = ref<any>(null)
const colors = ['#00f3ff', '#a855f7', '#22d3ee', '#f472b6', '#fbbf24']

const statItems = computed(() => {
  if (!stats.value) return []
  return [
    { label: '练习次数', value: stats.value.totalPractices },
    { label: '答题总数', value: stats.value.totalQuestions },
    { label: '总正确率', value: `${stats.value.overallAccuracy}%` },
  ]
})

const iconSvgMap: Record<string, string> = {
  coffee: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8h1a4 4 0 010 8h-1"/><path d="M2 8h16v9a4 4 0 01-4 4H6a4 4 0 01-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>',
  terminal: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/></svg>',
  'share-2': '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>',
  database: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/></svg>',
  globe: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z"/></svg>',
}

const subjectSvg = (icon: string) => iconSvgMap[icon] || '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 014 4v14a3 3 0 00-3-3H2z"/><path d="M22 3h-6a4 4 0 00-4 4v14a3 3 0 013-3h7z"/></svg>'

const startPractice = (subject: any) => router.push(`/practice/${subject.id}`)

onMounted(async () => {
  try {
    const [subRes, statRes]: any[] = await Promise.all([getSubjects(), getStats()])
    subjects.value = subRes.data
    stats.value = statRes.data
  } catch {}
})
</script>

<style scoped lang="scss">
.welcome-banner {
  background: linear-gradient(135deg, rgba(0, 243, 255, 0.12), rgba(168, 85, 247, 0.08));
  border: 1px solid var(--cyber-border-strong);
  border-radius: var(--radius-lg);
  padding: 36px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 36px;
  position: relative;
  overflow: hidden;

  h1 { font-family: 'Orbitron', sans-serif; font-size: 22px; font-weight: 700; color: var(--text-primary); }
  p { color: var(--text-secondary); margin-top: 6px; font-size: 14px; }
}

.banner-bg {
  position: absolute;
  inset: 0;
  .hex {
    position: absolute;
    width: 120px;
    height: 120px;
    border: 1px solid rgba(0, 243, 255, 0.08);
    border-radius: 12px;
    transform: rotate(45deg);
  }
  .hex-1 { right: 60px; top: -30px; }
  .hex-2 { right: 20px; bottom: -40px; width: 80px; height: 80px; }
}

.welcome-stats {
  display: flex;
  gap: 40px;
  position: relative;
}

.stat-item {
  text-align: center;
  .stat-value {
    display: block;
    font-family: 'Orbitron', sans-serif;
    font-size: 26px;
    font-weight: 700;
    color: var(--neon-cyan);
    text-shadow: 0 0 12px rgba(0, 243, 255, 0.3);
  }
  .stat-label { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Rajdhani', sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.subject-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.subject-card {
  text-align: center;
  cursor: pointer;
  padding: 28px 20px;
  animation: fadeUp 0.5s ease-out both;

  h3 { font-size: 16px; font-weight: 700; margin-bottom: 4px; color: var(--text-primary); }
  p { font-size: 13px; color: var(--text-muted); margin-bottom: 16px; }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.subject-icon-box {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: color-mix(in srgb, var(--c) 10%, transparent);
  border: 1px solid color-mix(in srgb, var(--c) 25%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: var(--c);
}

.start-btn {
  background: none;
  border: 1px solid var(--cyber-border);
  color: var(--neon-cyan);
  padding: 6px 20px;
  border-radius: var(--radius);
  font-family: 'Rajdhani', sans-serif;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: var(--neon-cyan);
    box-shadow: var(--glow-sm);
    background: rgba(0, 243, 255, 0.05);
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 28px;
  cursor: pointer;
  text-align: center;

  span { font-size: 14px; font-weight: 600; color: var(--text-primary); }
}
</style>
