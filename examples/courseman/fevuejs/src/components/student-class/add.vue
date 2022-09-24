<template src="./template/add.html"></template>
<script>
    import StudentClass from '../../model/student_class';
    import {addStudentClass, updateStudentClass} from '../../api/student_class';
    import Message from '../../constants/message';

    export default {
        props: {
            data:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/index.vue'),
        },

        data() {
            return {
                studentClass: new StudentClass(),
                formSubModuleStudentSeen: false,
                dataSubForm: {
                    mode: "create",
                    // parent: "student-class",
                    // studentClass: null,
                    studentClassID:'',
                }
            }
        },

        mounted() {
            if (this.data.mode === "edit") {
                this.studentClass = this.data.studentClass;


                this.dataSubForm.mode = "edit";
                this.dataSubForm.studentClassID = this.data.studentClass.id;
                // this.dataSubForm.studentClass = this.data.studentClass;
                // console.log(this.dataSubForm.studentClass);
            }
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
            },

            update() {
                console.log(this.studentClass.id);
                var result = updateStudentClass(this.studentClass.id, this.studentClass);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_STUDENT_CLASS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_STUDENT_CLASS_ERR + ' : ' + error.message);
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
        }
    };
</script>
