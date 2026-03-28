import request from './request'

export interface SubmitData {
  subjectId: number
  answers: Array<{
    questionId: number
    userAnswer: string
    sortOrder: number
  }>
}

export const submitPractice = (data: SubmitData) =>
  request.post('/practices/submit', data)

export const getRecords = () =>
  request.get('/practices/records')

export const getRecordDetail = (id: number) =>
  request.get(`/practices/records/${id}`)

export const getRecordDetails = (id: number) =>
  request.get(`/practices/records/${id}/details`)

export const getStats = () =>
  request.get('/practices/stats')
