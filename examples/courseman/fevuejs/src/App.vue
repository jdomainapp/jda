<template src="./layouts/header.html"></template>
<script>
import "bootstrap/dist/js/bootstrap.bundle.js";
import "bootstrap/js/dist/popover.js";
import "bootstrap/dist/css/bootstrap.min.css";

import NestedNav2 from "./lib/NestedNav2.vue";
import { store } from "./constants/store";

export default {
    name: "App",
    data() {
        return {
            store: store.form_trees2,
            searchQuery: "",
        };
    },

    components: {
        NestedNav2,
    },

    methods: {},
    computed: {},
};
</script>

<style scoped>
.root_nav {
    margin-bottom: 2rem;
}

.root_sidebar_search {
    z-index: 0;
    margin-top: 4px;
    margin-bottom: -4px;
}

.overflow_content {
    overflow: hidden;
    overflow-y: auto;
    max-height: calc(100vh - 2rem - 56px);
}
</style>
