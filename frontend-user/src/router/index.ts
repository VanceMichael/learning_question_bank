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
      redirect: '/home',
      children: [
        { path: 'home', name: 'Home', component: () => import('@/views/Home.vue') },
        { path: 'practice/:subjectId', name: 'Practice', component: () => import('@/views/Practice.vue') },
        { path: 'review/:recordId', name: 'Review', component: () => import('@/views/Review.vue') },
        { path: 'stats', name: 'Stats', component: () => import('@/views/Stats.vue') },
        { path: 'questions', name: 'Questions', component: () => import('@/views/Questions.vue') },
        { path: 'questions/add', name: 'AddQuestion', component: () => import('@/views/QuestionForm.vue') },
        { path: 'questions/edit/:id', name: 'EditQuestion', component: () => import('@/views/QuestionForm.vue') },
        { path: 'records', name: 'Records', component: () => import('@/views/Records.vue') },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  if (to.meta.public) return true
  if (!userStore.token) return '/login'
  if (!userStore.userInfo) {
    await userStore.fetchUser()
  }
  return true
})

export default router
