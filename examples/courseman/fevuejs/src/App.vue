<template src="./layouts/header.html"></template>

<script>
import "bootstrap/dist/js/bootstrap.bundle.js";
import "bootstrap/js/dist/popover.js";
import "bootstrap/dist/css/bootstrap.min.css";

import { getters } from "./constants/store";

export default {
    name: "App",
    data() {
        return {
            items: getters.formTree,
            searchQuery: "",
        };
    },

    computed: {
        processedItems() {
            const deepClone = JSON.parse(JSON.stringify(this.items));
            return this.arrayToTree(deepClone);
        }
    },

    methods: {
        arrayToTree(items, parentID = "") {
            const map = {}, roots = [];

            for (let i = 0; i < items.length; i += 1) {
                const node = items[i];
                map[node.id] = node; // initialize the map
                node.children = []; // initialize the children

                if (node.parentID !== parentID) {
                    // if the node is not a root,
                    // add it to its parent's children array
                    if (map[node.parentID]) {
                        map[node.parentID].children.push(node);
                    }
                } else {
                    roots.push(node);
                }
            }

            return roots;
        },
    },

    components: {
        "accordion-menu": () => import("./lib/AccordionMenu.vue"),
    },
};
</script>

<style scoped>
.accordion_menu_div,
.main_content {
    overflow: hidden;
    overflow-y: auto;
    /* 
        56px is the height of navbar. 24px is the margin
        between this div and nav bar... 18px is padding bottom
    */
    height: calc(100vh - 56px - 24px - 18px);
    scroll-behavior: smooth;
}

/* Make height = auto when width = 768px like bootstrap */
@media (max-width: 768px) {

    .accordion_menu_div,
    .main_content {
        height: auto;
    }
}
</style>