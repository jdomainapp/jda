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
        <div v-for="(item, index) in Items" :key="index">
            <!-- This is for item -->
            <div v-if="item.children.length === 0">
                <a :href="'#' + item.id" :name="item.name" v-if="linkShouldShow(item)">
                    📄 {{ item.name }}
                </a>
            </div>

            <!-- This is for sub-menu that has children -->
            <div v-else>
                <div class="d-flex justify-content-between align-items-center">
                    <a :href="'#' + item.id" :name="item.name">📁 {{ item.name }}</a>
                    <b-button v-b-toggle="'accordion_' + item.id" class="accordion_menu_button" variant="dark">+</b-button>
                </div>
                <b-collapse :id="'accordion_' + item.id" :visible="searchQuery.trim() !== ''">
                    <b-card>
                        <accordion-menu :Items="item.children" :searchQuery="searchQuery" />
                    </b-card>
                </b-collapse>
            </div>
        </div>
    </nav>
</template>

<script>

export default {
    name: "AccordionMenu",

    props: {
        Items: Array,
        searchQuery: String,
    },

    methods: {
        linkShouldShow(item) {
            const searchQuery = this.searchQuery.trim().toLowerCase();
            const name = item.name.toLowerCase();

            if (searchQuery === "") {
                return true;
            }

            return name.includes(searchQuery);
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

.accordion_menu_button {
    padding: 0;
    aspect-ratio: 1;
    height: 1.25rem;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>