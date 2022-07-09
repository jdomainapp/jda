<template src="./template/edit.html"></template>
<script>
    import Message from '../../constants/message';
    import @slot{{ModuleName}} from '../../model/@slot{{module_name}}';
    import @slot{{ModuleName}}Form from '../../model/form/@slot{{module_name}}';
    import {update@slot{{ModuleName}}, get@slot{{ModuleName}}} from '../../api/@slot{{module_name}}';
@loop{importLinkedDomain}[[
    import @slot{{LinkedDomain}} from '../../model/@slot{{linked_domain}}';
    import {get@slot{{LinkedDomain}}} from '../../api/@slot{{linked_domain}}';]]loop{importLinkedDomain}@


    export default {
        props: {
            sub@slot{{ModuleName}}Id:Object
        },

        components: {
        @loop{subModules}[[
            "form-sub-module-@slot{{moduleJname}}": () => import('../@slot{{module_name}}/edit.vue'),]]loop{subModules}@
        },

        data() {
            return {
                @slot{{module_name}}: new @slot{{ModuleName}}(),               
                @slot{{moduleName}}Id: this.sub@slot{{ModuleName}}Id,
                form:  new @slot{{ModuleName}}Form(),
                @loop{initLinkedDomainValues}[[
                @slot{{fieldName}}: new @slot{{LinkedDomain}}(),
                formSubModule@slot{{LinkedDomain}}Seen: false,]]loop{initLinkedDomainValues}@
            }
        },

        mounted() {
            this.get@slot{{ModuleName}}ById();
            this.setFrom();
        },

        methods: {
            setFrom() {
                if (this.sub@slot{{ModuleName}}Id == undefined) {
                @loop{hideSubForm}[[
                    this.form.setHid@slot{{linkedDomain}}(false)]]loop{hideSubForm}@
                }
            },

            get@slot{{ModuleName}}ById() {
                console.log(this.@slot{{moduleName}}Id);
                var result = get@slot{{ModuleName}}(this.@slot{{moduleName}}Id);

                result.then((res) => {
                    this.@slot{{module_name}} = res.data;

                    if (res.data.student !== undefined) {
                        this.student = res.data.student;
                    }
                })
                .catch((error) => {
                    this.\$toast.error(Message.GET_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

             @loop{getLinkedModuleByID}[[
            get@slot{{LinkedDomain}}ById(event) {
                let @slot{{linked_domain}}Id =  event.target.value;F

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

            update() {
                var result = update@slot{{ModuleName}}(this.@slot{{moduleName}}Id, this.@slot{{module_name}});
                result.then((res) => {
                    console.log(res);
                    this.\$toast.success(Message.UPDATE_@slot{{MODULE_NAME}}_SUC);
                })
                .catch((error) => {
                    this.\$toast.error(Message.UPDATE_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        },
    };
</script>