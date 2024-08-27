<template>
  <div id="app">
    <AppHeader />
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader.vue'; // Dosya yolunu kontrol edin

export default {
  name: 'App',
  components: {
    AppHeader
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 0;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content {
  margin-top: 60px;
  padding: 20px;
}
</style>
