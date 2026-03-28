import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMe } from '@/api/auth'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  role: number
  status: number
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const setToken = (t: string) => {
    token.value = t
    localStorage.setItem('token', t)
  }

  const fetchUser = async () => {
    if (!token.value) return
    try {
      const res: any = await getMe()
      userInfo.value = res.data
    } catch {
      logout()
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, setToken, fetchUser, logout }
})
