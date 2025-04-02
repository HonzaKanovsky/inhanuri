/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],

      },
      backgroundImage: {
        'gradient-background': 'linear-gradient(to bottom-right, #586c62, #138b37)',
          'hero': "url('@assets/assets/img/inha_image.jpg')",
      },
      colors: {
        black_olive: {
          DEFAULT: '#34403a',
          100: '#0b0d0c',
          200: '#151a17',
          300: '#202723',
          400: '#2a342f',
          500: '#34403a',
          600: '#586c62',
          700: '#7d9589',
          800: '#a9b8b0',
          900: '#d4dcd8',
        },
        cal_poly_green: {
          DEFAULT: '#285238',
          100: '#08100b',
          200: '#102117',
          300: '#183122',
          400: '#20422d',
          500: '#285238',
          600: '#42865c',
          700: '#63b281',
          800: '#97ccab',
          900: '#cbe5d5',
        },
        forest_green: {
          DEFAULT: '#138a36',
          100: '#041c0b',
          200: '#083816',
          300: '#0b5321',
          400: '#0f6f2c',
          500: '#138a36',
          600: '#1bc950',
          700: '#45e675',
          800: '#83eea3',
          900: '#c1f7d1',
        },
        sgbus_green: {
          DEFAULT: '#04e824',
          100: '#012e07',
          200: '#015c0e',
          300: '#028b14',
          400: '#03b91b',
          500: '#04e824',
          600: '#26fc42',
          700: '#5cfd72',
          800: '#92fda1',
          900: '#c9fed0',
        },
        spring_green: {
          DEFAULT: '#18ff6d',
          100: '#003815',
          200: '#007029',
          300: '#00a83e',
          400: '#00e052',
          500: '#18ff6d',
          600: '#47ff8b',
          700: '#75ffa8',
          800: '#a3ffc5',
          900: '#d1ffe2',
        },
      },
    },
  },
  variants: {
    extend: {},
  },
  plugins: [require('@tailwindcss/typography')],
}


