import Vue from 'vue/dist/vue.esm.js';
import App from './App.vue'
import { router } from './router/router'
import VueRouter from 'vue-router'
import VueBootstrapToasts from "vue-bootstrap-toasts";

Vue.use(VueRouter)
Vue.use(VueBootstrapToasts);

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
