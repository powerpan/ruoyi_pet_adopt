// 引入 js-cookie 用于操作 Cookie
import Cookies from 'js-cookie'

// 令牌（Token）在 Cookie 中的键名，统一定义方便维护
const TokenKey = 'Admin-Token'

/**
 * 获取存储在 Cookie 中的 Token
 * @returns {string|undefined} 返回 Token 字符串，如果没有则返回 undefined
 */
export function getToken() {
  // 通过 js-cookie 的 get 方法获取 TokenKey 对应的 Cookie 值
  return Cookies.get(TokenKey)
}

/**
 * 在 Cookie 中设置 Token
 * @param {string} token 要存储的 Token 字符串
 * @returns {string|undefined} 返回设置的 Token 字符串
 */
export function setToken(token) {
  // 通过 js-cookie 的 set 方法设置 TokenKey 对应的 Cookie 为指定的 token
  return Cookies.set(TokenKey, token)
}

/**
 * 移除 Cookie 中的 Token
 * @returns {undefined}
 */
export function removeToken() {
  // 通过 js-cookie 的 remove 方法移除 TokenKey 对应的 Cookie
  return Cookies.remove(TokenKey)
}
