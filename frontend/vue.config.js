const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081 // Burada istediğiniz port numarasını belirleyebilirsiniz
  }
})
