<template>
    <div>
        <p>This is test from index.vue from accordion</p>
        <accordion-menu :Items="Items" :searchQuery="searchQuery" />
    </div>
</template>

<script>
import AccordionMenu from "./recursivemenu.vue";

const test = [
    {
        name: "Form: Student class",
        id: "FormStudentClass",
        parentID: "",
        children: [
            {
                name: "Id",
                id: "FormStudentClassID",
                parentID: "FormStudentClass",
                children: []
            },
            // ...etc
        ]
    },
    // ...etc
]

export default {
    name: "Accordion",

    components: {
        AccordionMenu,
    },

    data() {
        return {
            Items: test,
            searchQuery: "",
        };
    },
}

</script>