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
                <b-card v-if="item.name.toLowerCase().includes(searchQuery.toLowerCase())">
                    <a :href="'#' + item.id" :name="item.name">{{ item.name }}</a>
                </b-card>
            </div>

            <!-- This is for sub-menu that has children -->
            <div v-else>
                <b-button v-b-toggle="'accordion_' + item.id" class="m-1">{{ item.name }}</b-button>
                <b-collapse :id="'accordion_' + item.id">
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