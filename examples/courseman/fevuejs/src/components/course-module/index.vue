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
                coursemoduleId: 0,
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                this.coursemoduleId = data.coursemoduleId;
                this.display = 2;
                console.log(this.coursemoduleId)
            },
        }
    };
</script>
