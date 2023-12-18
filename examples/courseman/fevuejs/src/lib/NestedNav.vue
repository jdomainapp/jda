<!-- This is for sidebar of App.vue -->
<template>
    <nav class="nav nav-pills flex-column">
        <div v-for="(item, index) in filteredItems" :key="index">
            <!-- click = prevent default for only one has nested nav -->
            <!-- Todo: rewrite this code to more readable -->
            <a
                class="nav-link"
                :class="{
                    'has-children': item.children && item.children.length,
                    'is-open': showNestedNav[index],
                }"
                :href="'#' + item.name.toLowerCase().replace(/ /g, '_')"
                @click="
                    item.children && item.children.length
                        ? toggleNestedNav($event, index)
                        : null
                "
            >
                <span v-if="item.children && item.children.length">
                    <img
                        v-if="showNestedNav[index]"
                        :src="arrow.down"
                        alt="-"
                    />
                    <img v-else :src="arrow.right" alt="+" />
                </span>
                {{ item.name }}
            </a>
            <nested-nav
                v-if="
                    item.children &&
                    item.children.length &&
                    showNestedNav[index]
                "
                :items="item.children"
                class="nav nav-pills flex-column ml-4"
            ></nested-nav>
        </div>
    </nav>
</template>

<script>
import rightArrow from "../assets/img/NestedNav/arrow_right.svg";
import downArrow from "../assets/img/NestedNav/arrow_down.svg";

// The `filterItems` function is a recursive function that filters the items based on a search query.
function filterItems(item, query) {
    if (item.name.toLowerCase().includes(query)) {
        return item;
    }

    if (item.children) {
        const matchingChildren = item.children
            .map((child) => filterItems(child, query))
            .filter((x) => x);
        if (matchingChildren.length > 0) {
            return { ...item, children: matchingChildren };
        }
    }

    return null;
}

export default {
    name: "NestedNav",
    props: {
        items: {
            type: Array,
            required: true,
        },

        searchQuery: {
            type: String,
            default: "",
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
    },
    computed: {
        filteredItems() {
            return this.items
                .map((item) => filterItems(item, this.searchQuery))
                .filter((x) => x);
        },
    },
};
</script>

<style scoped>
/* Future style */
</style>
