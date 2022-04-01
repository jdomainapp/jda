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
                studentClassId: 0,
            }
        },

        mounted() {

        },

        methods: {
            setData(data){
                this.studentClassId = data.studentClassId;
                this.display = 2;
                console.log(this.studentClassId)
            },
        }
    };
</script>
