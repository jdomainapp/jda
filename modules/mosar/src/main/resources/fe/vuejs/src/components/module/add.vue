<template src="./template/add.html"></template>
<script>
    import @slot{{ModuleName}} from "../../model/@slot{{module_name}}";
    import @slot{{ModuleName}}Form from '../../model/form/@slot{{module_name}}';
    import Message from '../../constants/message';
    import {add@slot{{ModuleName}}, update@slot{{ModuleName}}} from '../../api/@slot{{module_name}}';
    @loop{importLinkedDomains}[[
    import {get@slot{{LinkedDomain}}} from '../../api/@slot{{linked_domain}}';]]loop{importLinkedDomains}@

    export default {
        props: {
            data:Object
        },

        components: {@loop{linkedComponents}[[
            "form-sub-module-@slot{{linkedJdomain}}": () => import('../@slot{{linked_domain}}/add.vue'),]]loop{linkedComponents}@
        },

        data() {
            return {
                @slot{{module_name}}: new @slot{{ModuleName}}(),
                form: new @slot{{ModuleName}}Form(),@loop{initLinkedModules}[[
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
            this.setFrom();

            if (this.data.mode === "edit") {
              this.student = this.data.student;
              this.dataSubForm.mode = "edit";
              this.dataSubForm.student = this.data.student;
              this.dataSubForm.address = this.data.student.address;
              // this.dataSubForm.enrolmentIn = this.data.student.name;
              this.dataSubForm.parentID = this.data.student.id;
            }
        },

        methods: {
            setFrom() {
                if (this.data !== undefined) {@loop{hideSubForm}[[
                    this.form.setHid@slot{{LinkedDomain}}(false)]]loop{hideSubForm}@
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