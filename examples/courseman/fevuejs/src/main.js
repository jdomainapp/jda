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

// function simpleHash(value) {
//     let hash = 0;
//     for (let i = 0; i < value.length; i++) {
//         let character = value.charCodeAt(i);
//         hash = (hash << 5) - hash + character;
//         hash = hash & hash; // Convert to 32bit integer
//     }
//     return "modelRegion-" + Math.abs(hash);
// }
// Todo: use nanoid instead

Vue.directive("modelRegion", {
    // bind: function (el) {
    //     // el.id = simpleHash(JSON.stringify(el));
    //     console.log("bind", el);
    //     console.log("parent", el.parentNode);
    // },
    inserted: function (el) {
        console.log("inserted", el);
        console.log("parent", el.parentNode);

        let depth = 0;
        let currentElement = el;
        while (currentElement.parentNode) {
            depth++;
            currentElement = currentElement.parentNode;
        }
        console.log("depth", depth);
    },
    // update: function (el) {
    //     console.log("update", el);
    // },
    // componentUpdated: function (el) {
    //     console.log("componentUpdated", el);
    // },
    // unbind: function (el) {
    //     console.log("unbind", el);
    // },
});

Vue.config.productionTip = false;

new Vue({
    el: "#app",
    router,
    components: { App },
    template: "<App/>",
});
