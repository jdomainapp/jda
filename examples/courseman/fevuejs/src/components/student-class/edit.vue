<template src="./template/edit.html"></template>
<script>
    import StudentClass from '../../model/student_class';
    import {getStudentClass, updateStudentClass} from '../../api/student_class';
    import Message from '../../constants/message';

    export default {
        props: {
            subStudentClassId:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/index.vue'),
        },

        data() {
            return {
                studentClass: new StudentClass(),
                studentClassId: this.subStudentClassId,
                formSubModuleStudentSeen: false,
            }
        },

        mounted() {
            this.getById();
        },

        methods: {
            getById() {
                console.log(this.studentClassId);

                var result = getStudentClass(this.studentClassId);
                result.then((res) => {
                    this.studentClass = res.data;
                    console.log(this.studentClass);
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_STUDENT_CLASS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateStudentClass(this.studentClassId, this.studentClass);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_STUDENT_CLASS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_STUDENT_CLASS_ERR + ' : ' + error.message);
                }).finally(() => {

                });
            }
        },
    };
</script>