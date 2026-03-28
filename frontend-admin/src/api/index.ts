import request from './request'

// Auth
export const login = (data: { username: string; password: string }) =>
  request.post('/auth/login', data)
export const getMe = () => request.get('/auth/me')

// Subjects
export const getSubjects = (all = true) => request.get('/subjects', { params: { all } })
export const createSubject = (data: any) => request.post('/admin/subjects', data)
export const updateSubject = (id: number, data: any) => request.put(`/admin/subjects/${id}`, data)
export const deleteSubject = (id: number) => request.delete(`/admin/subjects/${id}`)

// Questions
export const getQuestions = (params: any) => request.get('/admin/questions', { params })
export const createQuestion = (data: any) => request.post('/admin/questions', data)
export const updateQuestion = (id: number, data: any) => request.put(`/admin/questions/${id}`, data)
export const deleteQuestion = (id: number) => request.delete(`/admin/questions/${id}`)

// Users
export const getUsers = (params: any) => request.get('/admin/users', { params })
export const updateUserStatus = (id: number, status: number) =>
  request.put(`/admin/users/${id}/status`, null, { params: { status } })
export const deleteUser = (id: number) => request.delete(`/admin/users/${id}`)

// Logs
export const getLogs = (params: any) => request.get('/admin/logs', { params })
