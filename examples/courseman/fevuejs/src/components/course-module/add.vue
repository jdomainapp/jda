<template src="./template/add.html"></template>
<script>
    import CourseModule from "../../model/course_module";
    import Message from '../../constants/message';
    import {addCourseModule, updateCourseModule, getCourseModule} from '../../api/course_module';
    
    export default {
        props: {
            parentData:Object
        },

        components: {
            
        },

        data() {
            return {
                state:0,
                courseModule: new CourseModule(),
                
                dataSubForm:
                {
                  mode: "create",
                  parent: "course-modules",
                  parentID: this.parentData ? this.parentData.parentID : 0,
                  hidFields:[]
                },
            };
        },

        computed: {
            
        },

        mounted() {
            if (this.parentData?.mode === "edit") {
              this.courseModule = this.parentData.courseModule;
              this.dataSubForm.mode = "edit";
              this.dataSubForm.courseModule = this.parentData.courseModule;
              this.dataSubForm.parentID = this.parentData.courseModule.id;
              
            }
            this.getRealType();
        },

        methods: {
             getRealType() {
                if (this.data.mode === "edit") {
                    let result = getCourseModule(this.courseModule.id);
                    result.then(response => {
                        this.courseModule = response.data;
                    })
                    .catch(e => {
                        this.$toast.error(Message.GET_COURSE_MODULE_ERR + ' - ' + e.message);
                    })
                }
            },

            create() {
                var result = addCourseModule(this.courseModule);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_COURSE_MODULE_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_COURSE_MODULE_ERR + ' - ' + error.message);
                }).finally(() => {});
            },
            
            update() {
                  var result = updateCourseModule(this.courseModule.id, this.courseModule);
                  result
                    .then((res) => {
                      console.log(res);
                      this.$toast.success(Message.UPDATE_COURSE_MODULE_SUC);
                    })
                    .catch((error) => {
                      this.$toast.error(Message.UPDATE_COURSE_MODULE_ERR + " - " + error.message);
                    })
                    .finally(() => {});
                },

            onSubmit() {
                if (this.parentData.mode == "create") {
                    this.create();
                } else {
                    this.update();
                }
            },
        },
    };
</script>