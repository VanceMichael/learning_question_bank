import request from './request'

export const login = (data: { username: string; password: string }) =>
  request.post('/auth/login', data)

export const register = (data: { username: string; password: string; nickname?: string }) =>
  request.post('/auth/register', data)

export const getMe = () => request.get('/auth/me')
