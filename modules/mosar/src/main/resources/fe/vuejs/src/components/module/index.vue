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
                    parent: this.parentData ? this.parentData.parent: "@slot{{moduleJnames}}",
                    parentID: this.parentData ? this.parentData.parentID : 0,
                    @slot{{moduleName}}: null
                }
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                console.log("set @slot{{moduleName}}",data)
                this.dataSubForm.mode = data.mode;
                this.dataSubForm.@slot{{moduleName}} = data.@slot{{moduleName}};
                this.display = 1;
            },

            mainForm() {
                this.display = 2;
                this.dataSubForm.mode = "create";
            }
        }
    };
</script>
