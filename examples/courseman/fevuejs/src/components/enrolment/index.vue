<template src="./template/index.html"></template>
<script>

    export default {
        props: {
            data:Object
        },

        components: {
            "form-add": () => import('./add.vue'),
            //"form-edit": () => import('./edit.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                display: 1,
                enrolmentId: 0,
                address: '',
                dataSubForm: {
                    mode: "create",
                    enrolment: null
                }
            }
        },

        mounted() {           
            if (this.data.parent !== null) {
                this.dataSubForm = this.data;
            }
        },

        methods: {
            setData(data){
                this.dataSubForm.enrolment = data.enrolment;
                this.display = 1;
                this.dataSubForm.mode = data.mode;
            },

            mainForm() {
                this.display = 2;
                this.dataSubForm.mode = "create";
            }
        }
    };
</script>
