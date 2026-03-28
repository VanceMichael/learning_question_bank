import request from './request'

export interface WrongQuestion {
  id: number
  userId: number
  questionId: number
  subjectId: number
  wrongCount: number
  firstWrongAt: string
  lastWrongAt: string
  question: {
    id: number
    subjectId: number
    creatorId: number
    type: number
    content: string
    options: string[] | null
    answer: string | string[]
    analysis: string | null
    difficulty: number
    status: number
    createdAt: string
    updatedAt: string
  }
  subjectName: string
}

export const getWrongQuestions = (subjectId?: number) =>
  request.get<WrongQuestion[]>('/wrong-questions', { params: { subjectId } })

export const removeWrongQuestion = (questionId: number) =>
  request.delete(`/wrong-questions/${questionId}`)

export const countWrongQuestions = () =>
  request.get<number>('/wrong-questions/count')