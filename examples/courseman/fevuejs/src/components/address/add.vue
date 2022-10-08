<template src="./template/add.html"></template>
<script>
    import Address from "../../model/address";
    import AddressForm from '../../model/form/address';
    import Message from '../../constants/message';
    import {updateAddress,addAddress} from '../../api/address';
    import {getStudent} from '../../api/student';
    import Student from '../../model/student';
    
    
    export default {
        props: {
            data:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/add.vue'),
        },

        data() {
            return {
                address: new Address(undefined, null, new Student()),
                formSubModuleStudentSeen: false,
                form: new AddressForm(),
                dataSubForm: {
                    mode: "create",
                    address: null,
                    student: null,
                    parent: "address"
                }
            };
        },

        mounted() {
            this.setFrom();
            if (this.data.mode === "edit") {
                this.address = this.data.address;
                this.dataSubForm.mode = "edit";
                this.dataSubForm.student = this.data.address.student;
                console.log("From student Address:" + this.data.address.student.id);
            }
            
        },

        methods: {
            setFrom() {
                if (this.data.parent !== undefined && this.data.parent === "student") {
                    this.form.setHidStudent(false)
                }
            },

            create() {
                // this.address.student.address = null;
                console.log(this.address.student);

                if(this.address.student.id === undefined){
                    this.address.student = null;
                }
        
                var result = addAddress(this.address);    
                
                
                
                result.then((res) => {
                    console.log(res);
                    
                    this.$toast.success(Message.ADD_ADDRESS_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                    console.log('Hahaaa' + error);
                }).finally(() => {});
            },

            getStudentById(event) {
                let studentId =  event.target.value;
                
                var result = getStudent(studentId);
                result.then((res) => {
                    this.address.student = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateAddress(this.address.id, this.address);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_ADDRESS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            onSubmit() {
                if (this.data.mode == "create") {
                    this.create();
                } else {
                    this.update();
                }
            }
        },
    }
</script>
