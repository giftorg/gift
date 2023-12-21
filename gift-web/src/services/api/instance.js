import axios from 'axios'
import JSONBIG from 'json-bigint'

const instance = axios.create({
  baseURL: '/api',
  timeout: 100000,
  transformResponse: [
    (data) => {
      const json = JSONBIG({
        storeAsString: true
      })
      return json.parse(data)
    }
  ]
})

instance.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default instance
