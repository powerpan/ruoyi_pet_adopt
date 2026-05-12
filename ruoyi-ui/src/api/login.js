import request from '@/utils/request'

function normalizeCaptchaPayload(data) {
  let body = data
  if (typeof body === 'string') {
    const t = body.trim()
    if (t.startsWith('<') || t.startsWith('<!')) {
      return Promise.reject(
        new Error('当前请求返回了网页而不是接口数据，请检查 Nginx：/prod-api/ 是否反代到 Java，且须写在 location / 之前')
      )
    }
    try {
      body = JSON.parse(t)
    } catch (e) {
      return Promise.reject(new Error('验证码接口返回无法解析为 JSON'))
    }
  }
  if (body == null || typeof body !== 'object') {
    return Promise.reject(new Error('验证码接口响应格式异常'))
  }
  if (body.img == null && body.data != null && typeof body.data === 'object' && body.data.img != null) {
    return {
      ...body,
      img: body.data.img,
      uuid: body.uuid != null ? body.uuid : body.data.uuid
    }
  }
  return body
}

// 登录方法
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  }).then(normalizeCaptchaPayload)
}