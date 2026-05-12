import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/information', '/home', '/contact', '/', '/service', '/services', '/topics']

function requiresAuth(to) {
  return to.matched.some(record => record.meta && record.meta.auth)
}

/** 已带 token 但尚未写入 userId（含刷新后 Vuex 重置）时需拉取 /getInfo，避免仅依赖 roles 导致跳过、Header 头像一直空 */
function needFetchUserProfile() {
  const id = store.state.user.id
  return id === '' || id === undefined || id === null
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (whiteList.indexOf(to.path) !== -1) {
      // 首页、数据库、服务等在白名单内：必须已拉过用户信息再 next，否则 Header 仅 token 无头像
      if (needFetchUserProfile()) {
        isRelogin.show = true
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          next()
        }).catch(err => {
          isRelogin.show = false
          store.dispatch('LogOut').then(() => {
            Message.error(err)
            next({ path: '/' })
          })
        })
      } else {
        next()
      }
    } else {
      if (needFetchUserProfile()) {
        isRelogin.show = true
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1 || !requiresAuth(to)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
