<template src="./template/list.html"></template>
<script>
import {
    getAllStudentClasses,
    deleteStudentClass,
} from "../../api/student_class";

import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";

export default {
    components: {
        "modal-confirm": ModalConfirm,
    },

    data() {
        return {
            studentClasses: [],
            studentClassId: 0,
            dataSubForm: {
                studentClass: null,
                parent: "student-classes",
                parentID: this.parentData ? this.parentData.parentID : 0,
                mode: "edit",
            },
        };
    },

    mounted() {
        this.getStudentClasses();
    },

    methods: {
        emitData(studentClass) {
            console.log("emitData", studentClass);
            this.$emit("data", { studentClass, mode: "edit" });
        },

        getStudentClassId(id) {
            this.studentClassId = id;
        },

        getStudentClasses() {
            var result = getAllStudentClasses();
            result
                .then((response) => {
                    this.studentClasses = response.data.content;
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.GET_LIST_STUDENT_CLASS_ERR + " - " + e.message
                    );
                });
        },

        deleteStudentClass(id) {
            var result = deleteStudentClass(id);
            result
                .then((response) => {
                    console.log(response);

                    this.getStudentClasses();

                    this.$toast.success(Message.DELETE_STUDENT_CLASS_SUC);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.DELETE_STUDENT_CLASS_ERR + " - " + e.message
                    );
                });
        },
    },
};
</script>
