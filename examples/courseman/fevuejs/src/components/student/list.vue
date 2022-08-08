<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Student from '../../model/student';
    import Message from '../../constants/message';
    import {getAllStudents, deleteStudent} from '../../api/student';
    import {getInnerListByOuterId} from '../../api/student_class';

    export default {
        props: {
            subStudentClassId:Object
        },

        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                students: [],
                studentId: 0,
                studentClassId: this.subStudentClassId === undefined ? 0 : this.subStudentClassId,
                data: {
                    studentId: 0,
                    mode: "edit"
                }
            }
        },
        mounted() {
            this.getStudents()
        },

        methods: {
            emitData(student) {
                this.data.student = student;
                this.$emit("data", this.data);
            },

            getStudentId(id) {
                this.studentId = id;
            },

            getStudents() {
                var result = getAllStudents();
                
                if (this.studentClassId != 0) {
                    result = getInnerListByOuterId(this.studentClassId)
                }

                this.students = []

                result.then(response => {
                    for(let i = 0; i < response.data.content.length; i++) {
                        let item = response.data.content[i]

                        let student = new Student(item.id, item.name, item.gender, item.dob, item.email)
                        student.setAddress(item.address)
                        student.setStudentClass(item.studentClass)

                        this.students.push(student)
                    }
                })
                .catch(e => {
                    this.$toast.error(Message.GET_STUDENT_ERR + ' - ' + e.message);
                })
            },

            deleteStudent(id) {
                var result = deleteStudent(id);
                result.then(response => {
                    console.log(response);

                    this.getStudents();

                    this.$toast.success(Message.DELETE_STUDENT_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_STUDENT_ERR + ' : ' + e.message);
                })
            },
        }
    };
</script>