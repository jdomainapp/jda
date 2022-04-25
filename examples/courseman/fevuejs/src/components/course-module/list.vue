<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';
    import {getAllCourseModules, deleteCourseModule} from '../../api/course_module';

    export default ({
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                coursemodules: [],
                coursemoduleId: 0,
                data: {
                    coursemoduleId: 0,
                }
            }
        },

        mounted() {
            this.getCoursemodules()
        },

        methods: {
            emitData(id) {
                this.data.coursemoduleId = id;
                this.$emit("data", this.data);
            },

            getCoursemoduleId(id) {
                this.coursemoduleId = id;
            },

            getCoursemodules() {
                var result = getAllCourseModules();
                result.then(response => {
                    this.coursemodules = response.data;
                })
                .catch(e => {
                    this.$toast.error(Message.GET_LIST_COURSE_ERR + ' - ' + e.message);
                })
            },

            deleteCoursemodule(id) {
                var result = deleteCourseModule(id);              
                result.then(response => {
                    console.log(response);

                    this.getCoursemodules();

                    this.$toast.success(Message.DELETE_COURSE_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_COURSE_ERR + ' - ' + e.message);
                })
            },
        }
    })
</script>
