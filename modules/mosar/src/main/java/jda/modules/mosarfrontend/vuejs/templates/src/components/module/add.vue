<template src="./template/add.html"></template>
<script>
    import @slot{{ModuleName}} from "../../model/@slot{{module_name}}";
    import @slot{{ModuleName}}Form from '../../model/form/@slot{{module_name}}';
    import {add@slot{{ModuleName}}} from '../../api/@slot{{module_name}}';
    import Message from '../../constants/message';
    @loop{importLinkedDomains}[[
    import @slot{{LinkedDomain}} from '../../model/@slot{{linked_domain}}';
    import {get@slot{{LinkedDomain}}} from '../../api/@slot{{linked_domain}}';]]loop{importLinkedDomains}@

    export default {
        props: {
            data:Object
        },

        components: {@loop{linkedComponents}[[
            "form-sub-module-@slot{{linked_domain}}": () => import('../@slot{{linked_domain}}/add.vue'),]]loop{linkedComponents}@
        },

        data() {
            return {
                @slot{{module_name}}: new @slot{{ModuleName}}(),
                form: new @slot{{ModuleName}}Form(),@loop{initLinkedModules}[[
                formSubModule@slot{{LinkedDomain}}Seen: false,
                @slot{{linked_domain}}: new @slot{{LinkedDomain}}(),]]loop{initLinkedModules}@
            };
        },

        mounted() {
            this.setFrom();
        },

        methods: {
            setFrom() {
                if (this.data !== undefined) {
                    this.form.setHidSubModule(false)
                }
            },

            create() {
                var result = add@slot{{ModuleName}}(this.@slot{{module_name}});
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_@slot{{MODULE_NAME}}_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {});
            },
            @loop{getLinkedModuleByID}[[
            get@slot{{LinkedDomain}}ById(event) {
                let @slot{{linked_domain}}Id =  event.target.value;

                var result = get@slot{{LinkedDomain}}(@slot{{linked_domain}}Id);
                result.then((res) => {
                    this.@slot{{linked_domain}} = res.data;
                })
                .catch((error) => {
                    this.\$toast.error(Message.ADD_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
            ]]loop{getLinkedModuleByID}@
        },
    };
</script>