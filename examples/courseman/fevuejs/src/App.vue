<template src="./layouts/header.html"></template>
<script>
import "bootstrap/dist/js/bootstrap.bundle.js";
import "bootstrap/js/dist/popover.js";
import "bootstrap/dist/css/bootstrap.min.css";

import NestedNav from "./lib/NestedNav.vue";
import { store } from "./constants/store";
// console.log(store.form_tree)
export default {
    name: "App",
    data() {
        return {
            store: store,
            searchQuery: "",
        };
    },

    components: {
        NestedNav
    },
};
</script>
