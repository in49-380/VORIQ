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
  },
})
