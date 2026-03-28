import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '仪表盘', icon: 'Odometer' } },
        { path: 'subjects', name: 'Subjects', component: () => import('@/views/Subjects.vue'), meta: { title: '科目管理', icon: 'Collection' } },
        { path: 'questions', name: 'Questions', component: () => import('@/views/Questions.vue'), meta: { title: '题目管理', icon: 'Document' } },
        { path: 'users', name: 'Users', component: () => import('@/views/Users.vue'), meta: { title: '用户管理', icon: 'User' } },
        { path: 'logs', name: 'Logs', component: () => import('@/views/Logs.vue'), meta: { title: '操作日志', icon: 'Notebook' } },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  if (to.meta.public) return true
  if (!userStore.token) return '/login'
  if (!userStore.userInfo) await userStore.fetchUser()
  if (!userStore.userInfo) return '/login'
  return true
})

export default router
