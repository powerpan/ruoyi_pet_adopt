/*
 * Copyright: https://github.com/powerpan/ruoyi_pet_adopt.git
 */
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Element from 'element-ui'
import './assets/styles/element-variables.scss'
import Cookies from 'js-cookie'
import '@/assets/styles/ruoyi.scss' // ruoyi css
import '@/assets/styles/pet-theme.scss'
import store from './store'
import './permission'
import './assets/css/scrollbar.css'; //
import { download } from '@/utils/request'
import './assets/icons' // icon
import '@/assets/iconfont/iconfont.css'

// 客户端公共组件注册，项目版权地址：https://github.com/powerpan/ruoyi_pet_adopt.git
// 分页组件
import Pagination from "@/components/Pagination";
import PetUpload from "@/components/PetUpload";
Vue.component('Pagination', Pagination);
Vue.component('PetUpload', PetUpload);
Vue.config.productionTip = false
Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.prototype.download = download

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
