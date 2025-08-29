import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  //   server: {
  //     host: '0.0.0.0',
  //   port:3000,
  // }
   server: {
    host: true,
    allowedHosts: ["voriq.info"], 
    port: 3000, 
    proxy: {
      '/swagger': {
        target: 'http://172.17.0.8:8084/api/swagger-ui/',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/swagger/, ''),
      },
      '/api/v3/api-docs': {
        target: 'http://172.17.0.8:8084',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
