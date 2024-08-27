<template>
    <header class="app-header">
      <img src="@/assets/ArikanMarket.jpg" alt="Logo" class="logo" />
      <div class="title" @click="goHome">
        Ana Sayfa
      </div>
    </header>
  </template>
  
  <script>
  export default {
    name: 'AppHeader',
    methods: {
      goHome() {
        this.$router.push('/suppliers');
      }
    }
  }
  </script>
  
  <style scoped>
  .app-header {
    display: flex;
    align-items: center;
    padding: 15px;
    background-color: #e37474;
    color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
  }
  
  .logo {
    height: 40px;
    margin-right: 20px;

  }
  
  .title {
    font-size: 1.5em;
    cursor: pointer;
    transition: opacity 0.3s ease;
  }
  
  .title:hover {
    opacity: 0.8;
  }
  </style>
  