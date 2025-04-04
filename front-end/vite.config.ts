import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8000/',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      }}
  },
  resolve: {
    alias: [
      { find: '@', replacement: '/src' },
      { find: '@animations', replacement: '/src/components/animations' },
      { find: '@features', replacement: '/src/features' },
      { find: '@services', replacement: '/src/services' },
      { find: '@assets', replacement: '/src/assets' },
      { find: '@ui', replacement: '/src/components/ui' },
    ],
   },
});
