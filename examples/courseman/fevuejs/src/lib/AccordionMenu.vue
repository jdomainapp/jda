<!--
    Example:
    [
        {
            "name": "Form: Student class",
            "id": "FormStudentClass",
            "parentID": "",
            "children": [
            {
                "name": "Id",
                "id": "FormStudentClassID",
                "parentID": "FormStudentClass",
                "children": []
            },
            ... etc
            ]
        },
        ... etc
    ]
-->
<template>
    <nav>
        <div v-for="(item, index) in displayTree" :key="index">
            <!-- This is for item -->
            <div v-if="item.children.length === 0">
                <b-card>
                    <a :href="'#' + item.id" :name="item.name">{{ item.name }}</a>
                </b-card>
            </div>

            <!-- This is for sub-menu that has children -->
            <div v-else>
                <b-button v-b-toggle="'accordion_' + item.id" class="m-1">{{ item.name }}</b-button>
                <b-collapse :id="'accordion_' + item.id" :visible="isSearching">
                    <b-card>
                        <accordion-menu :Items="item.children" :query="searchQuery" :isRoot="false" />
                    </b-card>
                </b-collapse>
            </div>
        </div>
    </nav>
</template>

<script>
function arrayToTree(items, parentID) {
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
}

export default {
    name: "AccordionMenu",

    props: {
        Items: Array,
        query: String,
        isRoot: {
            type: Boolean,
            default: true // and the rest of the children will be :isRoot="false"
        }
    },

    data() {
        return {
            searchQuery: this.query.trim().toLowerCase(),
            isSearching: false
        };
    },

    watch: {
        query(newVal) {
            this.searchQuery = newVal.trim().toLowerCase();
            this.isSearching = this.searchQuery.length > 0;
        }
    },

    computed: {
        displayTree() {
            const items = this.isRoot ? arrayToTree(this.Items, "") : this.Items;
            return items.filter(item => item.children.length > 0 || item.name.toLowerCase().includes(this.searchQuery));
        },
    }
}
</script>

<style scoped>
.card-body {
    padding: 0;
    padding-left: 0.5rem;
}

.card {
    border: none;
    border-radius: 0;
    border-left: 1px solid #e0e0e0;
}
</style>