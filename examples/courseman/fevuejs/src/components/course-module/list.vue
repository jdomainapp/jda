<template src="./template/list.html"></template>
<script>
    import {getAllCourseModules, deleteCourseModule} from '../../api/course_module';
    
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';

    export default {

        

        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                courseModules: [],
                courseModuleId: 0,
                dataSubForm: {
                    courseModule: null,
                    parent: "course-modules",
                    parentID: this.parentData ? this.parentData.parentID : 0,
                    mode: "edit"
                }
            }
        },

        mounted() {
            this.getCourseModules()
        },

        methods: {
            emitData(courseModule) {
                console.log("emitData",courseModule);
                this.$emit("data", {courseModule,mode:"edit"});
            },

            getCourseModuleId(id) {
                this.courseModuleId = id;
            },

            getCourseModules() {
                
                var result = getAllCourseModules();
                result.then(response => {
                    this.courseModules = response.data.content;
                })
                .catch(e => {
                    this.$toast.error(Message.GET_LIST_COURSE_MODULE_ERR + ' - ' + e.message);
                })
            },

            deleteCourseModule(id) {
                var result = deleteCourseModule(id);
                result.then(response => {
                    console.log(response);

                    this.getCourseModules();

                    this.$toast.success(Message.DELETE_COURSE_MODULE_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_COURSE_MODULE_ERR + ' - ' + e.message);
                })
            },
        },
    };
</script>