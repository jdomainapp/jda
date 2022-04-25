<template src="./template/add.html"></template>
<script>
    import Coursemodule from '../../model/course_module';
    import Message from '../../constants/message';
    import {addCourseModule} from '../../api/course_module';

    const DEFAULT_TYPE = "compulsory";

    export default({
        data() {
            return {
                coursemodule: new Coursemodule()
            }
        },

        mounted() {
            this.setDefaultType()
        },

        methods: {
            setDefaultType() {
                this.coursemodule.type = DEFAULT_TYPE;
            },

            create() {
                var result = addCourseModule(this.coursemodule);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_COURSE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        }
    })
</script>
