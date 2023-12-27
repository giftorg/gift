import { createRouter, createWebHistory } from 'vue-router'

const Index = () => import('@/views/index/Index.vue')
const NotFound = () => import('@/views/error/404.vue')
const ResultProject = () => import('@/views/search/ResultProject.vue')
const ResultCode = () => import('@/views/search/ResultCode.vue')

const routes = [
  {
    path: '/',
    component: Index,
    meta: {
      title: 'Gift - 开源项目搜索平台'
    }
  },
  {
    path: '/search/project',
    component: ResultProject,
    meta: {
      title: 'Github 项目搜索 - Gift'
    }
  },
  {
    path: '/search/code',
    component: ResultCode,
    meta: {
      title: '源码检索 - Gift'
    }
  },
  {
    path: '/:pathMatch(.*)',
    component: NotFound
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  /* 路由发生变化修改页面title */
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});

export default router
