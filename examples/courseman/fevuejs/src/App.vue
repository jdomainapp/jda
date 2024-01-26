<template src="./layouts/header.html"></template>
<script>
import "bootstrap/dist/js/bootstrap.bundle.js";
import "bootstrap/js/dist/popover.js";
import "bootstrap/dist/css/bootstrap.min.css";

import { store } from "./constants/store";

export default {
    name: "App",
    data() {
        return {
            items: store.formTree,
            searchQuery: "",
        };
    },

    components: {
        "accordion-menu": () => import("./lib/AccordionMenu.vue"),
    },
};
</script>
