<template src="./template/add.html"></template>
<script>
    import StudentClass from '../../model/student_class';
    import {addStudentClass} from '../../api/student_class';
    import Message from '../../constants/message';

    export default {
        components: {
            "form-sub-module-student": () => import('../student/index.vue'),
        },

        data() {
            return {
                studentClass: new StudentClass(),
                formSubModuleStudentSeen: false,
            }
        },

        mounted() {

        },

        methods: {
            create() {
                var result = addStudentClass(this.studentClass);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_STUDENT_CLASS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_STUDENT_CLASS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        }
    };
</script>