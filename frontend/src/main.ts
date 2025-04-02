import './assets/main.css'
import 'quill/dist/quill.snow.css';


import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from '../i18n'
import axios from 'axios';
import '@vueup/vue-quill/dist/vue-quill.snow.css';

axios.defaults.baseURL = import.meta.env.VITE_API_URL;


const app = createApp(App)

app.use(router)
app.use(i18n)

app.mount('#app')
