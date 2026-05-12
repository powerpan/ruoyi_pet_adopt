/**
 * 上传与静态资源 URL：开发环境走同源 /profile 代理拿二进制，避免 /dev-api/profile 返回 200+JSON 导致裂图。
 * 与 ruoyi-app 同源逻辑，便于两端行为一致。
 */

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

/**
 * 管理端：相对路径或已拼 base 的地址统一成浏览器可请求的 URL（开发下 /profile 等）。
 */
export function resolveAdminMediaUrl(raw) {
  if (raw == null || raw === '') {
    return ''
  }
  const s = String(raw).trim()
  if (s.indexOf('data:') === 0) {
    return s
  }
  if (/^https?:\/\//i.test(s)) {
    return resolveCanvasAssetUrl(s)
  }
  const baseApi = (process.env.VUE_APP_BASE_API || '').trim().replace(/\/+$/, '')
  const p = s.startsWith('/') ? s : '/' + s
  if (!baseApi) {
    return resolveCanvasAssetUrl(p)
  }
  const esc = baseApi.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const pathNorm = p.replace(new RegExp('^(?:' + esc + ')+'), baseApi)
  const joined = pathNorm === baseApi || pathNorm.startsWith(baseApi + '/') ? pathNorm : baseApi + p
  return resolveCanvasAssetUrl(joined)
}
