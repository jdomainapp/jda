<template src="./template/list.html"></template>
<script>
import {
    getAllCourseModules,
    deleteCourseModule,
} from "../../api/course_module";

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
            courseModules: [],
            courseModuleId: 0,
            dataSubForm: {
                courseModule: null,
                parent: "course-modules",
                parentID: this.parentData ? this.parentData.parentID : 0,
                mode: "edit",
            },

            page: {
                perPage: 5,
                currentPage: 1,
                fields: [
                    { key: "#", label: "#" },
                    { key: "id", label: "ID" },
                    { key: "code", label: "Code" },
                    { key: "name", label: "Name" },
                    { key: "semester", label: "Semester" },
                    { key: "credits", label: "Credits" },
                    { key: "description", label: "Description" },
                    { key: "rating", label: "Rating" },
                    { key: "cost", label: "Cost" },
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
        this.getCourseModules();
    },

    computed: {
        rows() {
            return this.courseModules.length;
        },
    },

    methods: {
        emitData(courseModule) {
            console.log("emitData", courseModule);
            this.$emit("data", { courseModule, mode: "edit" });
        },

        getCourseModuleId(id) {
            this.courseModuleId = id;
        },

        getCourseModules() {
            var result = getAllCourseModules();
            result
                .then((response) => {
                    this.courseModules = response.data.content;
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.GET_LIST_COURSE_MODULE_ERR + " - " + e.message
                    );
                });
        },

        deleteCourseModule(id) {
            var result = deleteCourseModule(id);
            result
                .then((response) => {
                    console.log(response);

                    this.getCourseModules();

                    this.$toast.success(Message.DELETE_COURSE_MODULE_SUC);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.DELETE_COURSE_MODULE_ERR + " - " + e.message
                    );
                });
        },
    },
};
</script>
