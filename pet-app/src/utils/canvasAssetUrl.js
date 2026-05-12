/**
 * 画布内图片地址：库中常为绝对 URL（127.0.0.1、localhost、旧服务器 IP），
 * 需对齐当前浏览器能访问的 API（开发走 /dev-api 代理，生产走 /prod-api 或完整域名）。
 */

/** 开发环境上传图走同源 /profile（devServer 已代理），避免 /dev-api/profile 未正确 rewrite 时后端按「需登录」返回 200+JSON */
function originProfilePath(profilePath) {
  if (typeof window === 'undefined' || !window.location) {
    return null
  }
  const p = profilePath.startsWith('/') ? profilePath : '/' + profilePath
  return window.location.origin.replace(/\/+$/, '') + p
}

function isDevRelativeApi() {
  const baseApi = (process.env.VUE_APP_BASE_API || '').trim().replace(/\/+$/, '')
  return baseApi === '/dev-api'
}

/**
 * 开发环境：webpack 对 /profile 单独代理，走同源 /profile 才能拿到静态文件；
 * /dev-api/profile 经 API 代理时后端常返回 200+JSON（鉴权/路由），图片与视频会裂。
 * @param {string} pathname 以 / 开头
 */
function devOriginProfileIfUnderApi(pathname) {
  if (!isDevRelativeApi() || typeof window === 'undefined' || !window.location) {
    return null
  }
  const baseApiRel = (process.env.VUE_APP_BASE_API || '').trim().replace(/\/+$/, '')
  if (!baseApiRel || !pathname.startsWith(baseApiRel + '/')) {
    return null
  }
  const rest = pathname.slice(baseApiRel.length)
  if (!/^\/profile\//i.test(rest)) {
    return null
  }
  return window.location.origin.replace(/\/+$/, '') + rest
}

function getApiBaseFull() {
  const baseApi = (process.env.VUE_APP_BASE_API || '').trim().replace(/\/+$/, '')
  if (!baseApi) {
    return ''
  }
  if (/^https?:\/\//i.test(baseApi)) {
    return baseApi
  }
  if (typeof window === 'undefined' || !window.location) {
    return baseApi
  }
  const origin = window.location.origin.replace(/\/+$/, '')
  return origin + (baseApi.startsWith('/') ? baseApi : '/' + baseApi)
}

/** 去掉路径里重复的 VUE_APP_BASE_API 段，如 /dev-api/dev-api/profile → /dev-api/profile */
function collapseDuplicateBaseApiPath(str, baseApiRel) {
  if (!str || !baseApiRel) {
    return str
  }
  const dup = baseApiRel + baseApiRel
  let out = str
  while (out.includes(dup)) {
    out = out.split(dup).join(baseApiRel)
  }
  return out
}

/**
 * @param {string|null|undefined} raw
 * @returns {string}
 */
export function resolveCanvasAssetUrl(raw) {
  if (raw == null || raw === '') {
    return ''
  }
  let s = String(raw).trim()
  if (s.indexOf('data:') === 0) {
    return s
  }

  const baseApiRel = (process.env.VUE_APP_BASE_API || '').trim().replace(/\/+$/, '')
  s = collapseDuplicateBaseApiPath(s, baseApiRel)

  const baseFull = getApiBaseFull()

  if (/^https?:\/\//i.test(s)) {
    if (/^https?:\/\/(?:127\.0\.0\.1|localhost)(?::\d+)?/i.test(s)) {
      const localProfile = /^https?:\/\/(?:127\.0\.0\.1|localhost)(?::\d+)?(\/profile\/.*)$/i.exec(s)
      if (localProfile && localProfile[1] && isDevRelativeApi()) {
        const o = originProfilePath(localProfile[1])
        if (o) {
          return o
        }
      }
      if (baseFull && baseApiRel) {
        try {
          const u = new URL(s)
          const h = u.hostname
          if (h === 'localhost' || h === '127.0.0.1') {
            const direct = devOriginProfileIfUnderApi(u.pathname)
            if (direct) {
              return direct + u.search + u.hash
            }
            if (u.pathname === baseApiRel || u.pathname.startsWith(baseApiRel + '/')) {
              return window.location.origin.replace(/\/+$/, '') + u.pathname + u.search + u.hash
            }
          }
        } catch (e) {
          /* ignore */
        }
        return s.replace(/^https?:\/\/(?:127\.0\.0\.1|localhost)(?::\d+)?/i, baseFull)
      }
      return s
    }
    if (baseFull && /\/profile\//i.test(s)) {
      const m = /^https?:\/\/[^/?#]+(\/.*)$/i.exec(s)
      if (m && m[1]) {
        if (isDevRelativeApi() && /^\/profile\//i.test(m[1])) {
          const o = originProfilePath(m[1])
          if (o) {
            return o
          }
        }
        return baseFull.replace(/\/+$/, '') + m[1]
      }
    }
    return s
  }

  if (!baseFull) {
    return s
  }
  const p = s.charAt(0) === '/' ? s : '/' + s
  if (baseApiRel && (p === baseApiRel || p.startsWith(baseApiRel + '/'))) {
    const rest = p.slice(baseApiRel.length)
    if (isDevRelativeApi() && /^\/profile\//i.test(rest)) {
      const o = originProfilePath(rest)
      if (o) {
        return o
      }
    }
    return baseFull.replace(/\/+$/, '') + rest
  }
  if (isDevRelativeApi() && /^\/profile\//i.test(p)) {
    const o = originProfilePath(p)
    if (o) {
      return o
    }
  }
  return baseFull.replace(/\/+$/, '') + p
}

export function resolveCanvasBackgroundImage(raw) {
  const url = resolveCanvasAssetUrl(raw)
  if (!url) {
    return ''
  }
  const escaped = String(url).replace(/\\/g, '\\\\').replace(/"/g, '\\"')
  return `url("${escaped}")`
}
