<template src="./template/index.html"></template>
<script>
    export default {
        props: {
            parentData:Object
        },

        components: {
            "form-add": () => import('./add.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                display: 1,
                dataSubForm: {
                    mode: "create",
                    parent: this.parentData ? this.parentData.parent: "enrolments",
                    parentID: this.parentData ? this.parentData.parentID : 0,
                    enrolment: null
                }
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                console.log("set enrolment",data)
                this.dataSubForm.mode = data.mode;
                this.dataSubForm.enrolment = data.enrolment;
                this.display = 1;
            },

            mainForm() {
                this.display = 2;
                this.dataSubForm.mode = "create";
            }
        }
    };
</script>
