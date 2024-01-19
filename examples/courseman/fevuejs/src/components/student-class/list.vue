<template src="./template/list.html"></template>
<script>
import {
    getAllStudentClasses,
    deleteStudentClass,
} from "../../api/student_class";

import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";
import { BPagination, BTable, BFormSelect } from "bootstrap-vue";

export default {
    components: {
        "modal-confirm": ModalConfirm,
        BPagination,
        BTable,
        BFormSelect
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

            page: {
                perPage: 5,
                currentPage: 1,
                fields: [
                    { key: "#", label: "#" },
                    { key: "id", label: "ID" },
                    { key: "name", label: "Name" },
                    { key: "action", label: "Action" },
                ],

                options: [
                    { value: 5, text: '5 rows / page' },
                    { value: 10, text: '10 rows / page' },
                    { value: 20, text: '20 rows / page' },
                ]
            },
        };
    },

    mounted() {
        this.getStudentClasses();
    },

    computed: {
        rows() {
            return this.studentClasses.length;
        },
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
