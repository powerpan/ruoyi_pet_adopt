/*
 * Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/components/layout/Layout'
import { getToken } from '@/utils/auth'

Vue.use(VueRouter)

// 客户端路由入口，项目版权地址：https://github.com/powerpan/ruoyi_pet_adopt.git
const routes = [
  { path: '/index', redirect: '/' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register') },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home') },
      { path: 'adoptions', name: 'Adoptions', component: () => import('@/views/Adoptions') },
      { path: 'adoptions/:id', name: 'AdoptionDetail', component: () => import('@/views/AdoptionDetail') },
      { path: 'adoption-manage', name: 'AdoptionManage', component: () => import('@/views/AdoptionManage'), meta: { auth: true } },
      { path: 'topics', name: 'Topics', component: () => import('@/views/Topics') },
      { path: 'posts/:id', name: 'PostDetail', component: () => import('@/views/PostDetail') },
      { path: 'publish', name: 'PostPublish', component: () => import('@/views/PostPublish'), meta: { auth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/Favorites'), meta: { auth: true } },
      { path: 'pets', name: 'Pets', component: () => import('@/views/Pets'), meta: { auth: true } },
      { path: 'services', name: 'Services', component: () => import('@/views/Services') },
      { path: 'merchant', name: 'Merchant', component: () => import('@/views/Merchant'), meta: { auth: true } },
      { path: 'notifications', name: 'Notifications', component: () => import('@/views/Notifications'), meta: { auth: true } },
      { path: 'health', name: 'Health', component: () => import('@/views/Health'), meta: { auth: true } },
      { path: 'me', name: 'Me', component: () => import('@/views/Me'), meta: { auth: true } },
      { path: 'pet-profile', redirect: '/pets' },
      { path: 'user-center', redirect: '/me' },
      { path: 'profile', redirect: '/me' }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior: () => ({ x: 0, y: 0 })
})

router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.auth && !getToken()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})

export default router
