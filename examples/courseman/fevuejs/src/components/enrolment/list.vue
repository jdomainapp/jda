<template src="./template/list.html"></template>
<script>
import { getAllEnrolments, deleteEnrolment } from "../../api/enrolment";
import { getInnerListByOuterId } from "../../api/enrolment";
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
            enrolments: [],
            enrolmentId: 0,
            parentID: this.parentData ? this.parentData.parentID : 0,
            dataSubForm: {
                enrolment: null,
                parent: "enrolments",
                parentID: this.parentData ? this.parentData.parentID : 0,
                mode: "edit",
            },

            page: {
                perPage: 5,
                currentPage: 1,
                // #	Id	Student	Course module	Internal Mark	Exam Mark	Final Grade	Start Date	End Date	Action
                fields: [
                    { key: "#", label: "#" },
                    { key: "id", label: "ID" },
                    { key: "student", label: "Student" },
                    { key: "courseModule", label: "Course module" },
                    { key: "internalMark", label: "Internal Mark" },
                    { key: "examMark", label: "Exam Mark" },
                    { key: "finalGrade", label: "Final Grade" },
                    { key: "startDate", label: "Start Date" },
                    { key: "endDate", label: "End Date" },
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
        this.getEnrolments();
    },

    computed: {
        rows() {
            return this.enrolments.length;
        },
    },

    methods: {
        emitData(enrolment) {
            console.log("emitData", enrolment);
            this.$emit("data", { enrolment, mode: "edit" });
        },

        getEnrolmentId(id) {
            this.enrolmentId = id;
        },

        getEnrolments() {
            let result;
            if (this.parentData.parentID) {
                console.log("getByOuterID");
                result = getInnerListByOuterId(
                    this.parentData.parentID,
                    this.parentData.parent
                );
            } else {
                result = getAllEnrolments();
            }

            result
                .then((response) => {
                    this.enrolments = response.data.content;
                    console.log(response.data.content);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.GET_LIST_ENROLMENT_ERR + " - " + e.message
                    );
                });
        },

        deleteEnrolment(id) {
            var result = deleteEnrolment(id);
            result
                .then((response) => {
                    console.log(response);

                    this.getEnrolments();

                    this.$toast.success(Message.DELETE_ENROLMENT_SUC);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.DELETE_ENROLMENT_ERR + " - " + e.message
                    );
                });
        },
    },
};
</script>
