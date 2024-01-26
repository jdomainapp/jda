<template src="./template/list.html"></template>
<script>
import {
    getAllStudentClasses,
    deleteStudentClass,
} from "../../api/student_class";

import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";

export default {
    props: {
        search: {
            type: Object,
            default: () => {
                return {
                    id: "",
                    keyword: "",
                };
            },
            required: true,
        }
    },

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
            return this.filterResults.length;
        },
        // Todo: sửa lại => trim keyword khi nhập input
        filterResults() {
            const keyword = this.search.keyword.toLowerCase();
            const id = this.search.id.toLowerCase();

            return this.studentClasses.filter(s => {
                let matchesId = true;
                let matchesKeyword = true;

                // ignore id & keyword if they are empty

                if (id !== "") {
                    matchesId = s.id.toString().toLowerCase().includes(id);
                }

                if (keyword !== "") {
                    matchesKeyword = s.name.toLowerCase().includes(keyword);
                }

                return matchesId && matchesKeyword;
            });
        }
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
