<!-- This is for sidebar of App.vue -->
<!--
! Bug: the href is recursive, so if I have filter,
! it will not include parent name which is
! included in the filter
? To make it as feature: make id of element not in filter display: none
-->
<template>
    <nav class="nav nav-pills flex-column">
        <div v-for="(item, index) in filteredItems" :key="index">
            <a class="nav-link" :class="{
                'has-children': item.children && item.children.length,
                'is-open': showNestedNav[index],
            }" :href="'#' +
    (parentId ? parentId + '_' : '') +
    item.name.toLowerCase().replace(/ /g, '_')
    " @click="
        item.children && item.children.length
            ? toggleNestedNav($event, index)
            : null
        ">
                <span v-if="item.children && item.children.length">
                    <img v-if="showNestedNav[index]" :src="arrow.down" alt="-" />
                    <img v-else :src="arrow.right" alt="+" />
                </span>
                {{ item.name }}
            </a>
            <nested-nav v-if="item.children &&
                item.children.length &&
                showNestedNav[index]
                " :items="item.children" :parentId="(parentId ? parentId + '_' : '') +
        item.name.toLowerCase().replace(/ /g, '_')
        " class="nav nav-pills flex-column ml-4"></nested-nav>
        </div>
    </nav>
</template>

<script>
import rightArrow from "../assets/img/NestedNav/arrow_right.svg";
import downArrow from "../assets/img/NestedNav/arrow_down.svg";

export default {
    name: "NestedNav",
    props: {
        items: {
            type: Array,
        },

        searchQuery: {
            type: String,
            default: "",
        },

        parentId: {
            type: String,
            default: "",
        },

        forModule: {
            type: String,
        },
    },
    data() {
        return {
            // Hide all nested navs by default
            showNestedNav: this.items.map(() => false),
            arrow: {
                right: rightArrow,
                down: downArrow,
            },
        };
    },
    methods: {
        toggleNestedNav(event, index) {
            event.preventDefault();
            this.$set(this.showNestedNav, index, !this.showNestedNav[index]);
        },

        // The `filterItems` function is a recursive function that filters the items based on a search query.
        filterItems(item, query) {
            if (item.name.toLowerCase().includes(query)) {
                return item;
            }

            if (item.children) {
                const matchingChildren = item.children
                    .map((child) => this.filterItems(child, query))
                    .filter((x) => x);
                if (matchingChildren.length > 0) {
                    return { ...item, children: matchingChildren };
                }
            }

            return null;
        },
    },
    computed: {
        filteredItems() {
            return this.items
                .map((item) => this.filterItems(item, this.searchQuery))
                .filter((x) => x);
        },
        attachedToModule() {
            return this.forModule;
        },
    },
    mounted() {
        console.log("Item in nested tree", this.items);
    },
};
</script>

<style scoped>
/* Future style */
</style>
