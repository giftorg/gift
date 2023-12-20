import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default ({ mode }) => {
  const env = { ...loadEnv(mode, process.cwd()) }

  return defineConfig({
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve('./src')
      }
    },
    server: {
      host: '0.0.0.0',
      port: '3000',
      proxy: {
        '/api': {
          target: env.VITE_BASE_HOST + env.VITE_API_URI,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    }
  })
}