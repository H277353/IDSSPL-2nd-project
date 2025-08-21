import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    tailwindcss(),
    react()],
   server: {
    //host:'',
    port: 5175,   // 👈 change this to your desired port
    open: true,   // optional: auto-open browser
  },
})
