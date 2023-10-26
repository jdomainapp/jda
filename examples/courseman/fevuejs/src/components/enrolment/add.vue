<template src="./template/add.html"></template>
<script>
    import Enrolment from "../../model/enrolment";
    import Message from '../../constants/message';
    import {addEnrolment, updateEnrolment, } from '../../api/enrolment';
    
    import {getStudent} from '../../api/student';
    import {getCourseModule} from '../../api/course_module';
    export default {
        props: {
            parentData:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/add.vue'),
            "form-sub-module-course-module": () => import('../course-module/add.vue'),
            
        },

        data() {
            return {
                state:0,
                enrolment: new Enrolment(),
                formSubModuleStudentSeen: false,
                formSubModuleCourseModuleSeen: false,
                
                dataSubForm:
                {
                  mode: "create",
                  parent: "enrolments",
                  parentID: this.parentData ? this.parentData.parentID : 0,
                  hidFields:["enrolments","id",]
                },
            };
        },

        computed: {
            
            studentId(){
                this.state
                return this.enrolment?.student?.id || ''
            },
            studentQuickView() {
              this.state
              return Object.values(this.enrolment?.student||{}).filter(e => typeof(e) != 'object').toString().replaceAll(',',' | ')
            },
            courseModuleId(){
                this.state
                return this.enrolment?.courseModule?.id || ''
            },
            courseModuleQuickView() {
              this.state
              return Object.values(this.enrolment?.courseModule||{}).filter(e => typeof(e) != 'object').toString().replaceAll(',',' | ')
            },
        },

        mounted() {
            if (this.parentData?.mode === "edit") {
              this.enrolment = this.parentData.enrolment;
              this.dataSubForm.mode = "edit";
              this.dataSubForm.enrolment = this.parentData.enrolment;
              this.dataSubForm.parentID = this.parentData.enrolment.id;
              
              this.dataSubForm.student = this.parentData.enrolment.student;
              this.dataSubForm.courseModule = this.parentData.enrolment.courseModule;
            }
            
        },

        methods: {
            

            create() {
                var result = addEnrolment(this.enrolment);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_ENROLMENT_SUC);
                }).catch((error) => {
                    this.$toast.error(Message.ADD_ENROLMENT_ERR + ' - ' + error.message);
                }).finally(() => {});
            },
            
            unlinkStudent(){
                this.enrolment.student = null;
            },

            getStudentById(event) {
                let studentId =  event.target.value;

                var result = getStudent(studentId);
                result.then((res) => {
                    this.enrolment.student = res.data;
                    this.dataSubForm.student = res.data;
                    this.state = (this.state+1)%2
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_STUDENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
            
            unlinkCourseModule(){
                this.enrolment.courseModule = null;
            },

            getCourseModuleById(event) {
                let courseModuleId =  event.target.value;

                var result = getCourseModule(courseModuleId);
                result.then((res) => {
                    this.enrolment.courseModule = res.data;
                    this.dataSubForm.courseModule = res.data;
                    this.state = (this.state+1)%2
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_COURSE_MODULE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },
            
            update() {
                  var result = updateEnrolment(this.enrolment.id, this.enrolment);
                  result
                    .then((res) => {
                      console.log(res);
                      this.$toast.success(Message.UPDATE_ENROLMENT_SUC);
                    })
                    .catch((error) => {
                      this.$toast.error(Message.UPDATE_ENROLMENT_ERR + " - " + error.message);
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