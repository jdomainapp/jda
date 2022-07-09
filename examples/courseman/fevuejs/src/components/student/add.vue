<template src="./template/add.html"></template>
<script>
import Student from '../../model/student';
import StudentForm from '../../model/form/student';
import Message from '../../constants/message';
import {addStudent} from '../../api/student';
import {getAddress, getAllAddresses} from '../../api/address';
import {getAllStudentClasses} from '../../api/student_class';

export default {
    props: {
        data:Object
    },

    components: {
        "form-sub-module-enrolment": () => import('../enrolment/index.vue'),
        "form-sub-module-address": () => import('../address/add.vue')
    },

    data() {
        return {
            addresses: [],
            address: '',
            studentClasses: [],
            student: new Student(),
            form:  new StudentForm(),
            formSubModuleAddressSeen: false,
            formSubModuleEnrolmentSeen: false,
        };
    },

    mounted() {
        this.getAddresses();
        this.getStudentClasses();
        this.setFrom();
    },

    methods: {
        setFrom() {
            if (this.data !== undefined) {
                this.form.setHidAddress(false)
                this.student.setAddress(this.data.address)
            }
        },

        getAddresses() {
            var result = getAllAddresses();
            result.then((response) => {
                this.addresses = response.data;
            }).catch((e) => {
                this.$toast.error(Message.GET_LIST_ADDRESS_ERR + ' - ' + e.message);
            });
        },

        getAddressById(event) {
            let addressId =  event.target.value;

            var result = getAddress(addressId);
            result.then((res) => {
                this.address = res.data;
            })
            .catch((error) => {
                this.address = '';
                this.$toast.error(Message.GET_ADDRESS_ERR + ' - ' + error.message);
            }).finally(() => {

            });
        },

        getStudentClasses() {
            var result = getAllStudentClasses();
            result.then((response) => {
                this.studentClasses = response.data;
                console.log(response.data);
            }).catch((e) => {
                this.$toast.error(Message.GET_LIST_STUDENT_CLASS_ERR + ' - ' + e.message);
            });
        },

        create() {
            this.studentClasses = null;
            this.student.setAddress(this.data.address)

            var result = addStudent(this.student);
            result.then((res) => {
                console.log(res);
                this.$toast.success(Message.ADD_STUDENT_SUC);
            }).catch((e) => {
                this.$toast.error(Message.ADD_STUDENT_ERR + ' - ' + e.message);
            }).finally(() => {});
        },
    },
};
</script>