import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      reportsDirectory: './coverage',
      include: ['src/api/**', 'src/stores/**', 'src/components/**', 'src/views/**'],
      exclude: ['src/main.ts', 'src/vite-env.d.ts'],
      thresholds: {
        api: 70,
        store: 80,
        components: 60
      }
    },
    setupFiles: ['./src/tests/setup.ts'],
    deps: {
      inline: ['element-plus']
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src')
    }
  }
})
