<template src="./template/add.html"></template>
<script>
    import Address from "../../model/address";
    import Message from '../../constants/message';
    import {addAddress, updateAddress, } from '../../api/address';
    
    import {getStudent} from '../../api/student';
    export default {
        props: {
            parentData:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/add.vue'),
            
        },

        data() {
            return {
                state:0,
                address: new Address(),
                formSubModuleStudentSeen: false,
                
                dataSubForm:
                {
                  mode: "create",
                  parent: "addresses",
                  parentID: this.parentData ? this.parentData.parentID : 0,
                  hidFields:["address","id",]
                },
            };
        },

        computed: {
            
            studentId(){
                this.state
                return this.address?.student?.id || ''
            },
            studentQuickView() {
              this.state
              return Object.values(this.address?.student||{}).filter(e => typeof(e) != 'object').toString().replaceAll(',',' | ')
            },
        },

        mounted() {
            if (this.parentData?.mode === "edit") {
              this.address = this.parentData.address;
              this.dataSubForm.mode = "edit";
              this.dataSubForm.address = this.parentData.address;
              this.dataSubForm.parentID = this.parentData.address.id;
              
              this.dataSubForm.student = this.parentData.address.student;
            }
            
        },

        methods: {
            

            create() {
                var result = addAddress(this.address);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_ADDRESS_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {});
            },
            
            unlinkStudent(){
                this.address.student = null;
            },

            getStudentById(event) {
                let studentId =  event.target.value;

                var result = getStudent(studentId);
                result.then((res) => {
                    this.address.student = res.data;
                    this.dataSubForm.student = res.data;
                    this.state = (this.state+1)%2
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_STUDENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
            
            update() {
                  var result = updateAddress(this.address.id, this.address);
                  result
                    .then((res) => {
                      console.log(res);
                      this.$toast.success(Message.UPDATE_ADDRESS_SUC);
                    })
                    .catch((error) => {
                      this.$toast.error(Message.UPDATE_ADDRESS_ERR + " - " + error.message);
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