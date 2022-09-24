<template src="./template/add.html"></template>
<script>
    import Enrolment from '../../model/enrolment';
    import EnrolmentForm from "../../model/form/enrolment";
    import Message from '../../constants/message';
    import {addEnrolment, updateEnrolment} from '../../api/enrolment';
    import {getCourseModule} from '../../api/course_module';
    import {getStudent} from '../../api/student';
    import Student from '../../model/student';
    import Coursemodule from '../../model/course_module';

    export default ({
        props: {
            data:Object
        },

        data() {
            return {
                displayStudent: 1,
                enrolment: new Enrolment(undefined, null, null, null, null, new Student(), new Coursemodule()),
                form: new EnrolmentForm(),
                dataSubForm: {
                    mode: "create",
                    enrolment: null,
                    student: null,
                    coursemodule: null,
                    parent: "enrolment"
                }
            }
        },

        mounted() {
            // if (this.data.parent === "student") {
            //     this.enrolment.student = this.data.student;
            // }

            
                if (this.data.mode === "edit" && this.data.enrolment != undefined) {
                    console.log('Edit');
                    this.enrolment = this.data.enrolment;
                }
            

        
            if (this.data.parent === "student") {
                console.log(this.data.student.id);

                if(this.data.student != null) {
                    this.student = this.data.student;
                    this.enrolment.student.id = this.data.student.id;
                    this.enrolment.student.name = this.data.student.name;
                    this.displayStudent = 2;
                }
            }           
        },

        methods: {
            create() {
              
                
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

            getCourseModuleById(event) {

                let courseModuleId =  event.target.value;
                
                var result = getCourseModule(courseModuleId);
                result.then((res) => {
                    this.enrolment.courseModule = res.data;
                    
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_COURSE_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            getStudentById(event) {
                let studentId =  event.target.value;
                
                var result = getStudent(studentId);
                result.then((res) => {
                    this.enrolment.student = res.data;
                    
                })
                .catch((error) => {
                    this.$toast.error(Message.ADD_ADDRESS_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {

                console.log('Update' + this.enrolment.id);

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
