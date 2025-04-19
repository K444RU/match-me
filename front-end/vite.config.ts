import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8000/',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/ws': {
        target: 'ws://localhost:8000/ws',
        ws: true,
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: 
    {
      '@': path.resolve(__dirname, './src'),
      '@animations': path.resolve(__dirname, './src/components/animations'),
      '@features': path.resolve(__dirname, './src/features'),
      '@services': path.resolve(__dirname, './src/services'),
      '@assets': path.resolve(__dirname, './src/assets'),
      '@ui': path.resolve(__dirname, './src/components/ui'),
    },
  },
});
