<template src="./template/add.html"></template>
<script>
    import Coursemodule from '../../model/course_module';
    import Message from '../../constants/message';
    import {addCourseModule, updateCourseModule} from '../../api/course_module';

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
                this.coursemodule = this.data.coursemodule;
                console.log(this.data.coursemodule.deptName);
                if(this.data.coursemodule.deptName == undefined){
                    this.coursemodule.type = 'compulsory';
                }else{
                    this.coursemodule.type = 'elective';
                }
                
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
