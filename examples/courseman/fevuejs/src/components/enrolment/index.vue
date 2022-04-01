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
                enrolmentId: 0,
                address: '',
            }
        },

        mounted() {
            
        },

        methods: {
            getAddress(data) {
                this.address = data;
            },

            setData(data){
                this.enrolmentId = data.enrolmentId;
                this.display = 2;
                console.log(this.enrolmentId)
            },
        }
    };
</script>
