<template src="./template/add.html"></template>
<script>
    import Coursemodule from '../../model/course_module';
    import Message from '../../constants/message';
    import {addCourseModule, updateCourseModule} from '../../api/course_module';
    import {getCourseModule} from '../../api/course_module';

    const DEFAULT_TYPE = "compulsory";

    export default({
        props: {
            data:Object
        },

        data() {
            return {
                coursemodule: new Coursemodule()
            }
        },

        mounted() {
            this.setDefaultType()

            if (this.data.mode === "edit") {
               
                let result = getCourseModule(this.data.coursemodule.id);

                result.then(response => {
                    this.coursemodule = response.data;
                  
                })
                .catch(e => {
                    this.$toast.error(Message.GET_COURSE_ERR + ' - ' + e.message);
                })
                
            }
        },

        methods: {
            setDefaultType() {
                this.coursemodule.type = DEFAULT_TYPE;
            },

            create() {
                console.log('Add' + this.coursemodule.type);
                var result = addCourseModule(this.coursemodule);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_COURSE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                
                var result = updateCourseModule(this.coursemodule.id, this.coursemodule);
                

                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_COURSE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_COURSE_ERR + ' - ' + error.message);
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
