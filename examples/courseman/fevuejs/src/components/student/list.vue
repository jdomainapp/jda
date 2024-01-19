<template src="./template/list.html"></template>
<script>
import { getAllStudents, deleteStudent } from "../../api/student";
import { getInnerListByOuterId } from "../../api/student";
import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";
import { BPagination, BTable, BFormSelect } from "bootstrap-vue";

export default {
    props: {
        parentData: Object,
    },

    components: {
        "modal-confirm": ModalConfirm,
        BPagination,
        BTable,
        BFormSelect
    },

    data() {
        return {
            students: [],
            studentId: 0,
            parentID: this.parentData ? this.parentData.parentID : 0,
            dataSubForm: {
                student: null,
                parent: "students",
                parentID: this.parentData ? this.parentData.parentID : 0,
                mode: "edit",
            },

            page: {
                perPage: 5,
                currentPage: 1,
                fields: [
                    { key: "#", label: "#" },
                    { key: "id", label: "ID" },
                    { key: "name", label: "Full name" },
                    { key: "gender", label: "Gender" },
                    { key: "dob", label: "Date of birth" },
                    { key: "email", label: "Email" },
                    { key: "address", label: "Current Address" },
                    { key: "studentClass", label: "Student Class" },
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
        this.getStudents();
    },

    computed: {
        rows() {
            return this.students.length;
        },
    },

    methods: {
        emitData(student) {
            console.log("emitData", student);
            this.$emit("data", { student, mode: "edit" });
        },

        getStudentId(id) {
            this.studentId = id;
        },

        getStudents() {
            let result;
            if (this.parentData.parentID) {
                console.log("getByOuterID");
                result = getInnerListByOuterId(
                    this.parentData.parentID,
                    this.parentData.parent
                );
            } else {
                result = getAllStudents();
            }

            result
                .then((response) => {
                    this.students = response.data.content;
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.GET_LIST_STUDENT_ERR + " - " + e.message
                    );
                });
        },

        deleteStudent(id) {
            var result = deleteStudent(id);
            result
                .then((response) => {
                    console.log(response);

                    this.getStudents();

                    this.$toast.success(Message.DELETE_STUDENT_SUC);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.DELETE_STUDENT_ERR + " - " + e.message
                    );
                });
        },
    },
};
</script>
