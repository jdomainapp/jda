<template src="./template/edit.html"></template>
<script>
    import Coursemodule from '../../model/course_module';
    import Message from '../../constants/message';
    import {getCourseModule, updateCourseModule} from '../../api/course_module';

    export default({
        props: {
            subCoursemoduleId:Object
        },

        data() {
            return {
                coursemodule: new Coursemodule(),
                courseModuleId: this.subCoursemoduleId,
            }
        },

        mounted() {
            this.getCourseModuleById()
        },

        methods: {
            getCourseModuleById() {
                console.log(this.courseModuleId);

                var result = getCourseModule(this.courseModuleId)
                result.then((res) => {
                    this.coursemodule = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateCourseModule(this.courseModuleId, this.coursemodule);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_COURSE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        }
    })
</script>
