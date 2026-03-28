import request from './request'

export interface WrongQuestion {
  id: number
  userId: number
  questionId: number
  subjectId: number
  wrongCount: number
  lastWrongAt: string
  createdAt: string
  updatedAt: string
  question: Question
  subjectName: string
}

export interface Question {
  id: number
  subjectId: number
  creatorId: number
  type: number
  content: string
  options: string[]
  answer: any
  analysis: string
  difficulty: number
  status: number
  createdAt: string
  updatedAt: string
}

export function getWrongQuestions(subjectId?: number) {
  return request.get<WrongQuestion[]>('/wrong-questions', { params: { subjectId } })
}

export function getWrongQuestionList(subjectId?: number) {
  return request.get<Question[]>('/wrong-questions/questions', { params: { subjectId } })
}

export function removeWrongQuestion(questionId: number) {
  return request.delete(`/wrong-questions/${questionId}`)
}
