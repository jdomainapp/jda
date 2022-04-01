<template src="./template/index.html"></template>
<script>
    export default {
        components: {
            "form-add": () => import('./add.vue'),
            "form-edit": () => import('./edit.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                 display: 1,
                 studentId: 0,
            }
        },

        mounted() {

        },

        methods: {
            setData(data){
                this.studentId = data.studentId;
                this.display = 2;
                console.log(this.studentId)
            },
        }
    };
</script>
