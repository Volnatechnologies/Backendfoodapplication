/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        skyPage: '#DDF3FF',
        skyLight: '#F0F8FF',
        brandBlue: {
          50: '#EBF6FC',
          100: '#D5EDFA',
          500: '#0B6B9F',
          600: '#0A5F8E',
          700: '#084E75',
          800: '#063E5D',
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'soft-card': '0 4px 20px rgba(0, 0, 0, 0.03)',
        'hero-shadow': '0 12px 32px rgba(0, 0, 0, 0.15)',
      }
    },
  },
  plugins: [],
}
