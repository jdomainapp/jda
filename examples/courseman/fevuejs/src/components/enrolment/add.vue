<template src="./template/add.html"></template>
<script>
    import Enrolment from '../../model/enrolment';
    import Message from '../../constants/message';
    import {addEnrolment, updateEnrolment} from '../../api/enrolment';
    import {getCourseModule} from '../../api/course_module';
    import Student from '../../model/student';
    import Coursemodule from '../../model//course_module';

    export default ({
        props: {
            data:Object
        },

        data() {
            return {
                enrolment: new Enrolment(undefined, null, null, null, null, new Student(), new Coursemodule())
            }
        },

        mounted() {
            if (this.data.parent === "student") {
                this.enrolment.student = this.data.student;
            }

            if (this.data.mode === "edit") {
                this.enrolment = this.data.enrolment;
            }
        },

        methods: {
            create() {
                this.getCourseModuleById();
                
                var result = addEnrolment(this.enrolment);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_ENROLMENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_ENROLMENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            getCourseModuleById() {
                var result = getCourseModule(this.enrolment.courseModule.id);
                result.then((res) => {
                    this.enrolment.courseModule = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateEnrolment(this.enrolment.id, this.enrolment);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_ENROLMENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_ENROLMENT_ERR + ' - ' + error.message);
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
    })
</script>
