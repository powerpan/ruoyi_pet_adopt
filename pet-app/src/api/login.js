import request from '@/utils/request' // 引入自定义的请求工具

/**
 * 将验证码接口响应整理为 { captchaEnabled, img, uuid, ... }。
 * 若 Nginx 把 /prod-api 错配成 SPA，会返回 HTML，此时 axios 仍可能 200 + 字符串，导致无 img。
 */
function normalizeCaptchaPayload(data) {
  let body = data
  if (typeof body === 'string') {
    const t = body.trim()
    if (t.startsWith('<') || t.startsWith('<!')) {
      return Promise.reject(
        new Error('当前请求返回了网页而不是接口数据，请检查 Nginx：/prod-api/ 是否反代到 Java（8091），且须写在 location / 之前')
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
// 参数：username - 用户名, password - 密码, code - 验证码, uuid - 验证码唯一标识
// 返回值：Promise对象，包含接口返回的登录结果
export function login(username, password, code, uuid) {
  // 构造请求体数据，将参数组装为对象
  const data = {
    username,    // 用户名
    password,    // 密码
    code,        // 验证码
    uuid         // 验证码唯一标识
  }
  // 发起POST请求，调用后端登录接口
  return request({
    url: '/login',                      // 接口地址
    headers: {
      isToken: false,                   // 不携带token访问
      repeatSubmit: false               // 禁止重复提交
    },
    method: 'post',                     // 请求方法
    data: data,                          // 请求体参数
    timeout: 60000                      // 非管理员拉权限可能较慢，避免 10s 默认误报超时
  })
}

// 注册方法
// 参数：data - 用户注册相关数据对象
// 返回值：Promise对象，包含接口返回的注册结果
export function register(data) {
  // 发起POST请求，提交注册信息
  return request({
    url: '/register',                   // 注册接口地址
    headers: {
      isToken: false                    // 不携带token访问
    },
    method: 'post',                     // 请求方法
    data: data,                          // 注册的具体参数
    timeout: 60000
  })
}

// 获取用户详细信息方法
// 无需参数
// 返回值：Promise对象，包含后端返回的用户详细信息
export function getInfo() {
  // 发起GET请求，获取用户详情
  return request({
    url: '/getInfo',                    // 用户信息接口地址
    method: 'get',                     // 请求方法
    timeout: 60000
  })
}

// 退出方法
// 无需参数
// 返回值：Promise对象，通知后端安全退出
export function logout() {
  // 发起POST请求，执行用户退出操作
  return request({
    url: '/logout',                     // 退出接口地址
    method: 'post'                      // 请求方法
  })
}

// 获取验证码图片方法
// 无需参数
// 返回值：Promise对象，包含验证码图片数据
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