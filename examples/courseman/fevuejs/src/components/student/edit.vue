<template src="./template/edit.html"></template>
<script>
import Student from '../../model/student';
import StudentForm from '../../model/form/student';
import Message from '../../constants/message';
import {updateStudent, getStudent} from '../../api/student';

export default {
    props: {
        subStudentId:Object
    },

    components: {
        "form-sub-module-enrolment": () => import('../enrolment/index.vue'),
        "form-sub-module-address": () => import('../address/edit.vue')
    },

    data() {
        return {
            student: new Student(),
            studentId: this.subStudentId,
            form: new StudentForm(),
            formSubModuleAddressSeen: false,
            formSubModuleEnrolmentSeen: false,
        }
    },

    mounted() {
        this.getById();
        this.setFrom();
    },

    methods: {
        setFrom() {
            if (this.subStudentId == undefined) {
                this.form.setHidAddress(false)
            }
        },

        getById() {
            if (this.studentId === undefined) {
                return;
            }
            
            var result = getStudent(this.studentId);
            result.then((res) => {
                let item = res.data;
                this.student = new Student(item.id, item.name, item.gender, item.dob, item.email)
                this.student.setAddress(item.address)
                this.student.setStudentClass(item.studentClass)

                console.log(this.student)
            })
            .catch((error) => {
                this.$toast.error(Message.GET_STUDENT_ERR + ' - ' + error.message);
            }).finally(() => {

            });
        },

        update() {
            var result = updateStudent(this.studentId, this.student);
            result.then((res) => {
                console.log(res);
                this.$toast.success(Message.UPDATE_STUDENT_SUC);
            })
            .catch((error) => {
                this.$toast.error(Message.UPDATE_STUDENT_ERR + ' - ' + error.message);
            }).finally(() => {

            });
        }
    }
};
</script>