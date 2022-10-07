<template src="./template/add.html"></template>
<script>
    import @slot{{ModuleName}} from "../../model/@slot{{module_name}}";
    import Message from '../../constants/message';
    import {add@slot{{ModuleName}}, update@slot{{ModuleName}}} from '../../api/@slot{{module_name}}';
    @loop{importLinkedDomains}[[
    import {get@slot{{LinkedDomain}}} from '../../api/@slot{{linked_domain}}';]]loop{importLinkedDomains}@
    @if{typedModule}(( const DEFAULT_TYPE = "@slot{{defaultType}}" ))if{typedModule}@
    export default {
        props: {
            data:Object
        },

        components: {@loop{linkedComponents}[[
            "form-sub-module-@slot{{linkedJdomain}}": () => import('../@slot{{linked_domain}}/add.vue'),]]loop{linkedComponents}@
        },

        data() {
            return {
                @slot{{moduleName}}: new @slot{{ModuleName}}(),
                form: new @slot{{ModuleName}}(),@loop{initLinkedModules}[[
                formSubModule@slot{{LinkedDomain}}Seen: false,]]loop{initLinkedModules}@
                dataSubForm:
                {
                  mode: "create",
                  parent: "@slot{{moduleName}}",
                  parentID:'',
                },
            };
        },

        mounted() {
            @if{setDefaultType}((this.setDefaultType()))if{setDefaultType}@
            if (this.data.mode === "edit") {
              this.@slot{{moduleName}} = this.data.@slot{{moduleName}};
              this.dataSubForm.mode = "edit";
              this.dataSubForm.@slot{{moduleName}} = this.data.@slot{{moduleName}};
              this.dataSubForm.address = this.data.student.address;
              this.dataSubForm.parentID = this.data.@slot{{moduleName}}.@slot{{idField}};
            }
        },

        methods: {
            @if{setDefaultTypeGen}((setDefaultType() {
                this.@slot{{moduleName}}.type = DEFAULT_TYPE;
            },))if{setDefaultTypeGen}@

            create() {
                var result = add@slot{{ModuleName}}(this.@slot{{moduleName}});
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
                    this.@slot{{module_name}}.@slot{{linked_domain}} = res.data;
                    this.dataSubForm.@slot{{linked_domain}} = res.data;
                })
                .catch((error) => {
                    this.\$toast.error(Message.ADD_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
            ]]loop{getLinkedModuleByID}@
            update() {
                  var result = update@slot{{ModuleName}}(this.@slot{{moduleName}}.@slot{{idField}}, this.@slot{{moduleName}});
                  result
                    .then((res) => {
                      console.log(res);
                      this.$toast.success(Message.UPDATE_@slot{{MODULE_NAME}}_SUC);
                    })
                    .catch((error) => {
                      this.$toast.error(Message.UPDATE_@slot{{MODULE_NAME}}_ERR + " - " + error.message);
                    })
                    .finally(() => {});
                },

            onSubmit() {
                if (this.data.mode == "create") {
                    this.create();
                } else {
                    this.update();
                }
            },
        },
    };
</script>