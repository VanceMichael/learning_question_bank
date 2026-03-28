import request from './request'

export interface QuestionForm {
  subjectId: number
  type: number
  content: string
  options?: string[]
  answer: any
  analysis?: string
  difficulty?: number
  status?: number
}

export const getQuestions = (params: any) =>
  request.get('/questions', { params })

export const getQuestion = (id: number) =>
  request.get(`/questions/${id}`)

export const createQuestion = (data: QuestionForm) =>
  request.post('/questions', data)

export const updateQuestion = (id: number, data: QuestionForm) =>
  request.put(`/questions/${id}`, data)

export const deleteQuestion = (id: number) =>
  request.delete(`/questions/${id}`)

export const getPracticeQuestions = (subjectId: number, limit = 20) =>
  request.get('/questions/practice', { params: { subjectId, limit } })
