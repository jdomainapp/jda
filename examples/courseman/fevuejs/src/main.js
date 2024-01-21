import Vue from "vue/dist/vue.esm.js";
import App from "./App.vue";
import { router } from "./router/router";
import VueRouter from "vue-router";
import VueBootstrapToasts from "vue-bootstrap-toasts";
import BootstrapVue from "bootstrap-vue";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

Vue.use(VueRouter);
Vue.use(VueBootstrapToasts);
Vue.use(BootstrapVue);

Vue.config.productionTip = false;

new Vue({
    el: "#app",
    router,
    components: { App },
    template: "<App/>",
});
