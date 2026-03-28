import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMe } from '@/api'

export const useUserStore = defineStore('admin-user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref<any>(null)

  const setToken = (t: string) => {
    token.value = t
    localStorage.setItem('admin_token', t)
  }

  const fetchUser = async () => {
    if (!token.value) return
    try {
      const res: any = await getMe()
      userInfo.value = res.data
      if (res.data.role !== 1) {
        logout()
        throw new Error('非管理员')
      }
    } catch {
      logout()
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
  }

  return { token, userInfo, setToken, fetchUser, logout }
})
