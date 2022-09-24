<template src="./template/index.html"></template>
<script>
    export default {
        components: {
            "form-add": () => import('./add.vue'),
            //"form-edit": () => import('./edit.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                display: 1,
                studentClassId: 0,
                data: {
                    mode: "create",
                    studentClass: null
                }
                
            }
        },

        mounted() {

        },

        methods: {
            setData(data){
                this.data.studentClass = data.studentClass;
                this.display = 1;
                this.data.mode = data.mode;
            },

            mainForm() {
                this.display = 2;
                this.data.mode = "create";
            }
        }
    };
</script>
