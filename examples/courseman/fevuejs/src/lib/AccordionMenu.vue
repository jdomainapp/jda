<!--
    Example:
    [
        {
            "name": "Form: Student class",
            "id": "FormStudentClass",
            "parentID": "",
            "depth": 0
        },
        {
            "name": "Id",
            "id": "FormStudentClassID",
            "parentID": "FormStudentClass",
            "depth": 1
        },
        {
            "name": "Name",
            "id": "FormStudentClassName",
            "parentID": "FormStudentClass",
            "depth": 1
        },
        {
            "name": "Form: Student",
            "id": "FormStudentClassFormStudent",
            "parentID": "FormStudentClass",
            "depth": 1
        }
    ]
-->
<template>
    <div>
        <!-- Todo: using nested recursive component -->
        <p>Todo: AccordionMenu</p>
    </div>
</template>

<script>
export default {
    name: "AccordionMenu",
    props: {
        Items: Array,
        query: String,
    },
}
</script>