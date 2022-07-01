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
                @slot{{moduleName}}Id: 0,
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                this.@slot{{moduleName}}Id = data.@slot{{moduleName}}Id;
                this.display = 2;
                console.log(this.@slot{{moduleName}}Id)
            },
        }
    };
</script>
