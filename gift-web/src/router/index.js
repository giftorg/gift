import { createRouter, createWebHistory } from 'vue-router'

const Index = () => import('@/views/index/Index.vue')
const NotFound = () => import('@/views/error/404.vue')
const ResultProject = () => import('@/views/search/ResultProject.vue')
const ResultCode = () => import('@/views/search/ResultCode.vue')

const routes = [
  {
    path: '/',
    component: Index
  },
  {
    path: '/search/project',
    component: ResultProject
  },
  {
    path: '/search/code',
    component: ResultCode
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

export default router
