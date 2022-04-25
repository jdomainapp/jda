<template>
    <form-list v-bind:data="data"></form-list>
</template>
<script>
    import List from '../../student/list.vue'

    export default {
        props: {
            studentClassId: Object
        },

        components: {
            "form-list": List
        },

        data() {
            return {
                data: {
                    studentClassId: this.studentClassId
                },
            };
        },

        mounted() {
            
        },

        methods: {
            
        },
    };
</script>