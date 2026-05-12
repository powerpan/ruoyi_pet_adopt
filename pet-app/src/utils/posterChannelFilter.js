import { resolveCanvasAssetUrl } from '@/utils/canvasAssetUrl'

/**
 * 拼接接口前缀与资源 path；path 已是 http(s) 时不再重复拼接（避免裂图）。
 * @param {string} baseApi 如 /prod-api、/dev-api
 * @param {string} path 相对或绝对地址
 * @returns {string}
 */
export function joinBaseApiMediaUrl(baseApi, path) {
  const p = path == null ? '' : String(path).trim()
  if (!p) {
    return ''
  }
  if (/^https?:\/\//i.test(p)) {
    return p
  }
  const b = String(baseApi || '').replace(/\/+$/, '')
  if (!b) {
    return p.startsWith('/') ? p : '/' + p
  }
  let pathNorm = p.startsWith('/') ? p : '/' + p
  // 库中若已存 /dev-api/profile/... 或历史多拼成 /dev-api/dev-api/...，勿再拼一次 base
  const esc = b.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  pathNorm = pathNorm.replace(new RegExp('^(?:' + esc + ')+'), b)
  if (pathNorm === b || pathNorm.startsWith(b + '/')) {
    return pathNorm
  }
  return b + pathNorm
}

/**
 * 按轮播素材「标题」关键字筛选：优先匹配渠道标签，无结果时用 backup 标签兜底。
 * 匹配不区分大小写。
 *
 * @param {Array<{ title?: string }>} rows 接口返回的 rows
 * @param {string} channelMarker 如 ruoyiapp、miniapp
 * @param {string} backupMarker 兜底关键字，如 backup
 * @returns {Array}
 */
export function filterPosterRowsByChannelTitle(rows, channelMarker, backupMarker) {
  const list = Array.isArray(rows) ? rows : [];
  const ch = String(channelMarker || "").toLowerCase();
  const bk = String(backupMarker || "").toLowerCase();
  const norm = (t) => String(t || "").toLowerCase();
  const hasSub = (row, sub) => sub && norm(row.title).includes(sub);
  if (ch) {
    const primary = list.filter((r) => hasSub(r, ch));
    if (primary.length) {
      return primary;
    }
  }
  if (bk) {
    return list.filter((r) => hasSub(r, bk));
  }
  return [];
}

const BACKUP_IMAGE_EXT = /\.(jpe?g|png|gif|svg|webp)(\?.*)?$/i;

/**
 * 从原始 rows 中取「标题含 backup」且 URL 为图片的首条，拼上前缀，供轮播视频未就绪时的保底图。
 * @param {Array<{ title?: string, url?: string }>} rows
 * @param {string} baseUrlPrefix 如 process.env.VUE_APP_BASE_API
 */
export function resolveHeroBackupPosterUrl(rows, baseUrlPrefix) {
  const list = Array.isArray(rows) ? rows : [];
  const prefix = String(baseUrlPrefix || "").replace(/\/+$/, "");
  const norm = (t) => String(t || "").toLowerCase();
  for (const r of list) {
    if (!r || !r.url || !norm(r.title).includes("backup")) {
      continue;
    }
    const path = String(r.url);
    const joined = joinBaseApiMediaUrl(prefix, path);
    const full = resolveCanvasAssetUrl(joined);
    if (BACKUP_IMAGE_EXT.test(full)) {
      return full;
    }
  }
  return "";
}
