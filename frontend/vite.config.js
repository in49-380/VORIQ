/* eslint-env node */
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  // Default swagger URL
  let swaggerUrl = 'http://172.17.0.8:8084';

  if (env.VITE_SWAGGER_DOMAIN && env.VITE_SWAGGER_PORT) {
    swaggerUrl = `${env.VITE_SWAGGER_DOMAIN}:${env.VITE_SWAGGER_PORT}`;
  }

  return {
    plugins: [react()],
    server: {
      host: true,
      allowedHosts: ["voriq.info"],
      port: 3000,
      proxy: {
        '/swagger': {
          target: swaggerUrl + '/api/swagger-ui/',
          secure: false,
          rewrite: (path) => path.replace(/^\/swagger/, ''),
        },
        '/api/v3/api-docs': {
          target: swaggerUrl,
          changeOrigin: true,
          secure: false,
        },
      },
    },
  }
})
