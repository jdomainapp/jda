<template src="./template/add.html"></template>
<script>
    import Enrolment from '../../model/enrolment';
    import Message from '../../constants/message';
    import {addEnrolment} from '../../api/enrolment';
    import {getCourseModule} from '../../api/course_module';

    export default ({
        data() {
            return {
                enrolment: new Enrolment()
            }
        },

        mounted() {

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
        }
    })
</script>
