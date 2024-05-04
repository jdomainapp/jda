<template src="./template/list.html"></template>
<script>
import { getAllStudents, deleteStudent } from "../../api/student";
import { getInnerListByOuterId } from "../../api/student";
import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";

export default {
    props: {
        parentData: Object,

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
            return this.filterResults.length;
        },

        filterResults() {
            const keyword = this.search.keyword.toLowerCase();
            const id = this.search.id.toLowerCase();

            return this.students.filter((s) => {
                let matchesId = true;
                let matchesKeyword = true;

                if (id !== "") {
                    matchesId = s.id.toString().toLowerCase().includes(id);
                }

                if (keyword !== "") {
                    for (const key in s) {
                        if (s[key] && s[key].toString().toLowerCase().includes(keyword)) {
                            matchesKeyword = true;
                            break;
                        } else {
                            matchesKeyword = false;
                        }
                    }
                }

                return matchesId && matchesKeyword;
            });
        }
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
                    console.log(this.students);
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
