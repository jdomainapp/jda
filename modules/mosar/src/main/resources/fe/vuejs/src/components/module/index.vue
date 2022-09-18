<template src="./template/index.html"></template>
<script>
    export default {
        props: {@loop{One2ManyIds}[[
            sub@slot{{LinkedDomain}}Id:Object,]]loop{One2ManyIds}@
        },

        components: {
            "form-add": () => import('./add.vue'),
            "form-list": () => import('./list.vue'),
        },

        data() {
            return {
                display: 1,
                data: {
                    mode: "create",
                    @slot{{moduleName}}: null
                }
            }
        },

        mounted() {
            
        },

        methods: {
            setData(data){
                this.data.@slot{{moduleName}} = data.@slot{{moduleName}};
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
