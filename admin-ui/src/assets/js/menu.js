import Layout from '@/layout/index.vue'
import router from '@/router'
import store from '@/store'
import axios from 'axios'
import Vue from 'vue'

export function generaMenu() {
  axios.get('/api/admin/user/menus').then(({ data }) => {
    if (data.flag) {
      let userMenus = data.data
      userMenus.forEach((item) => {
        if (item.icon != null) {
          item.icon = 'iconfont ' + item.icon
        }
        if (item.component == 'Layout') {
          item.component = Layout
        }
        if (item.children && item.children.length > 0) {
          item.children.forEach((route) => {
            route.icon = 'iconfont ' + route.icon
            route.component = loadView(route.component)
          })
        }
      })
      store.commit('saveUserMenus', userMenus)
      userMenus.forEach((item) => {
        router.addRoute(item)
      })
    } else {
      Vue.prototype.$message.error(data.message)
      router.push({ path: '/login' })
    }
  })
}

/**
 * 动态加载视图组件的函数
 * @param {string} view - 需要加载的视图组件路径
 * @returns {function} 返回一个接收resolve函数的高阶函数，用于异步加载组件
 */
export const loadView = (view) => {
  return (resolve) => require([`@/views${view}`], resolve) // 使用require.ensure实现组件的异步加载
}
