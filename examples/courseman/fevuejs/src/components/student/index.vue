<template src="./template/index.html"></template>
<script>
    export default {
        props: {
            subStudentClassId:Object
        },

        components: {
            "form-add": () => import('./add.vue'),
            //"form-edit": () => import('./edit.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                 display: 1,
                 data: {
                    mode: "create",
                    student: null
                }
            }
        },

        mounted() {

        },

        methods: {
            setData(data){
                this.data.student = data.student;
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
