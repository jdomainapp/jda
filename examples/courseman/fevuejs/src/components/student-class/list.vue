<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';
    import {getAllStudentClasses, deleteStudentClass} from '../../api/student_class';

    export default {
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                studentClasses: [],
                studentClassId: 0,
                data: {
                    studentClass: null,
                    mode: "edit"
                }
            }
        },

        mounted() {
            this.getStudentClasses()
        },

        methods: {
            emitData(studentClass) {

                this.data.studentClass = studentClass;
                this.$emit("data", this.data);
            },

            getStudentClassId(id) {
                this.studentClassId = id
            },

            getStudentClasses() {
                var result = getAllStudentClasses();
                result.then(response => {
                    this.studentClasses = response.data
                    console.log(response.data);
                })
                .catch(e => {
                    this.$toast.error(Message.GET_STUDENT_CLASS_ERR + ' - ' + e.message);
                })
            },

            deleteStudentClasses(id) {
                var result = deleteStudentClass(id);
                result.then(response => {
                    console.log(response);

                    this.getStudentClasses();

                    this.$toast.success(Message.DELETE_STUDENT_CLASS_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_STUDENT_CLASS_ERR + ' : ' + e.message);
                })
            },
        }
    };
</script>
