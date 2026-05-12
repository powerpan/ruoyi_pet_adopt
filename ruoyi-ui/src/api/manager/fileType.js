import request from '@/utils/request'

export function listTreeFileType(query) {
  return request({
    url: '/manager/pet/topics/list',
    method: 'get',
    params: query
  })
}

export function countLeafFileType() {
  return Promise.resolve({ code: 200, data: 0 })
}
