<!-- This is for sidebar of App.vue -->
<template>
    <nav class="nav nav-pills flex-column">
        <div v-for="(item, index) in filteredItems" :key="index">
            <a
                class="nav-link"
                :href="'#' + item.name.toLowerCase().replace(/ /g, '_')"
            >
                {{ item.name }}
            </a>
            <nested-nav
                v-if="item.children && item.children.length"
                :items="item.children"
                class="nav nav-pills flex-column ml-4"
            ></nested-nav>
        </div>
    </nav>
</template>

<script>
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
            required: true,
        },
    },

    computed: {
        filteredItems() {
            const query = this.searchQuery.trim().toLowerCase();
            if (!query) {
                return this.items;
            }
            return this.items
                .map((item) => filterItems(item, query))
                .filter((x) => x);
        },
    },
};
</script>

<style>
/* Future style */
</style>
