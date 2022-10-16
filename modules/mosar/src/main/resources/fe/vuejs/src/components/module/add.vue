<template src="./template/add.html"></template>
<script>
    import @slot{{ModuleName}} from "../../model/@slot{{module_name}}";
    import Message from '../../constants/message';
    import {add@slot{{ModuleName}}, update@slot{{ModuleName}}, @if{hasSubType}((get@slot{{ModuleName}}))if{hasSubType}@} from '../../api/@slot{{module_name}}';
    @loop{importLinkedDomains}[[
    import {get@slot{{LinkedDomain}}} from '../../api/@slot{{linked_domain}}';]]loop{importLinkedDomains}@
    export default {
        props: {
            parentData:Object
        },

        components: {@loop{linkedComponents}[[
            "form-sub-module-@slot{{linkedJdomain}}": () => import('../@slot{{linkedJdomain}}/add.vue'),]]loop{linkedComponents}@
            @loop{linkedComponentsForOne2Many}[[
            "form-sub-module-@slot{{linkedJdomain}}": () => import('../@slot{{linkedJdomain}}/index.vue'),]]loop{linkedComponentsForOne2Many}@
        },

        data() {
            return {
                state:0,
                @slot{{moduleName}}: new @slot{{ModuleName}}(),@loop{initLinkedModules}[[
                formSubModule@slot{{LinkedDomain}}Seen: false,]]loop{initLinkedModules}@
                @loop{initLinkedModulesForOne2Many}[[
                formSubModule@slot{{LinkedDomain}}Seen: false,]]loop{initLinkedModulesForOne2Many}@
                dataSubForm:
                {
                  mode: "create",
                  parent: "@slot{{moduleJnames}}",
                  parentID: this.parentData ? this.parentData.parentID : 0,
                  hidFields:[@slot{{hideFields}}]
                },
            };
        },

        computed: {
            @loop{genQuickView}[[
            @slot{{linkedDomain}}Id(){
                this.state
                return this.@slot{{moduleName}}?.@slot{{linkedDomain}}?.@slot{{linkedIdField}} || ''
            },
            @slot{{linkedDomain}}QuickView() {
              this.state
              return Object.values(this.@slot{{moduleName}}?.@slot{{linkedDomain}}||{}).filter(e => typeof(e) != 'object').toString().replaceAll(',',' | ')
            },]]loop{genQuickView}@
        },

        mounted() {
            if (this.parentData?.mode === "edit") {
              this.@slot{{moduleName}} = this.parentData.@slot{{moduleName}};
              this.dataSubForm.mode = "edit";
              this.dataSubForm.@slot{{moduleName}} = this.parentData.@slot{{moduleName}};
              this.dataSubForm.parentID = this.parentData.@slot{{moduleName}}.@slot{{idField}};
              @loop{initDataForSubForm}[[
              this.dataSubForm.@slot{{linkedDomain}} = this.parentData.@slot{{moduleName}}.@slot{{linkedDomain}};]]loop{initDataForSubForm}@
            }
            @if{setDefaultType}((this.getRealType();))if{setDefaultType}@
        },

        methods: {
            @if{setDefaultTypeGen}(( getRealType() {
                if (this.data.mode === "edit") {
                    let result = get@slot{{ModuleName}}(this.@slot{{moduleName}}.@slot{{idField}});
                    result.then(response => {
                        this.@slot{{moduleName}} = response.data;
                    })
                    .catch(e => {
                        this.\$toast.error(Message.GET_@slot{{MODULE_NAME}}_ERR + ' - ' + e.message);
                    })
                }
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
            unlink@slot{{LinkedDomain}}(){
                this.@slot{{moduleName}}.@slot{{linkedDomain}} = null;
            },

            get@slot{{LinkedDomain}}ById(event) {
                let @slot{{linkedDomain}}Id =  event.target.value;

                var result = get@slot{{LinkedDomain}}(@slot{{linkedDomain}}Id);
                result.then((res) => {
                    this.@slot{{module_name}}.@slot{{linkedDomain}} = res.data;
                    this.dataSubForm.@slot{{linkedDomain}} = res.data;
                    this.state = (this.state+1)%2
                })
                .catch((error) => {
                    this.\$toast.error(Message.GET_@slot{{LINKED_DOMAIN}}_ERR + ' - ' + error.message);
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
                if (this.parentData.mode == "create") {
                    this.create();
                } else {
                    this.update();
                }
            },
        },
    };
</script>