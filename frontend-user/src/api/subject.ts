import request from './request'

export const getSubjects = (all = false) =>
  request.get('/subjects', { params: { all } })
