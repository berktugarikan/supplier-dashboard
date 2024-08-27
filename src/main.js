import { createApp } from 'vue';
import App from './App.vue'; // Düzeltildi: AppHeader yerine App import edilmeli
import router from './router';

const app = createApp(App);
app.use(router);
app.mount('#app');
