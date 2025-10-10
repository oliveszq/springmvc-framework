import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: '登录',
    hidden: true,
    component: () => import('../views/login/Login.vue')
  }
]
/**
 * 创建并返回一个VueRouter实例
 * 使用history模式进行路由管理
 * @returns {VueRouter} 配置好的VueRouter实例
 */
const createRouter = () =>
  new VueRouter({
    mode: 'history', // 使用history模式，URL看起来更规范，没有#号
    routes: routes  // 路由配置数组，定义了各个路由路径对应的组件
  })
const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
