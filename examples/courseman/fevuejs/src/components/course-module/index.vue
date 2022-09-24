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
                data: {
                    mode: "create",
                    coursemodule: null
                }
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                this.data.coursemodule = data.coursemodule;
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
