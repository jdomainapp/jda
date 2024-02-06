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

    computed: {
        processedItems() {
            const deepClone = JSON.parse(JSON.stringify(this.items));
            return this.arrayToTree(deepClone);
        }
    },

    // watch: {
    //     items: {
    //         handler(val) {
    //             console.log(JSON.stringify(val, null, 2));
    //         },
    //         deep: true,
    //     },
    // },

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
