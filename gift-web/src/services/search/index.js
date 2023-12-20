import { get } from '@/services/api/index.js'

export async function getSearchProject(query) {
  return get('search/project', {
    query
  })
}

export async function getSearchCode(query) {
  return get('search/code', {
    query
  })
}