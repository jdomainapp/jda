<template src="./template/add.html"></template>
<script>
    import Address from "../../model/address";
    import AddressForm from '../../model/form/address';
    import Student from '../../model/student';
    import Message from '../../constants/message';
    import {addAddress} from '../../api/address';
    import {getStudent} from '../../api/student';

    export default {
        props: {
            data:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/add.vue'),
        },

        data() {
            return {
                address: new Address(),
                formSubModuleStudentSeen: false,
                form: new AddressForm(),
                student: new Student(),
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
                var result = addAddress(this.address);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_ADDRESS_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {});
            },

            getStudentById(event) {
                let studentId =  event.target.value;
                
                var result = getStudent(studentId);
                result.then((res) => {
                    this.student = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
        },
    };
</script>