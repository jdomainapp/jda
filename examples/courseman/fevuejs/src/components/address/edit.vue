<template src="./template/edit.html"></template>
<script>
    import Address from '../../model/address';
    import Student from '../../model/student';
    import Message from '../../constants/message';
    import AddressForm from '../../model/form/address';
    import {updateAddress, getAddress} from '../../api/address';
    import {getStudent} from '../../api/student';

    export default {
        props: {
            subAddressId:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/edit.vue'),
        },

        data() {
            return {
                address: new Address(),               
                student: new Student(),
                addressId: this.subAddressId,
                form:  new AddressForm(),
                formSubModuleStudentSeen: false,
            }
        },

        mounted() {
            this.getAddressById();
            this.setFrom();
        },

        methods: {
            setFrom() {
                if (this.subAddressId == undefined) {
                    this.form.setHidSubModule(false)
                    console.log(this.form.hidSubModule)
                }
            },

            getAddressById() {
                console.log(this.addressId);
                var result = getAddress(this.addressId);

                result.then((res) => {
                    this.address = res.data;

                    if (res.data.student !== undefined) {
                        this.student = res.data.student;
                    }
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            getStudentById(event) {
                let studentId =  event.target.value;

                var result = getStudent(studentId)
                result.then((res) => {
                    this.student = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_STUDENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateAddress(this.addressId, this.address);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_ADDRESS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        },
    };
</script>