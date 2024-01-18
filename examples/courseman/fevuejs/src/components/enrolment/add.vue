<template src="./template/add.html"></template>
<script>
import Enrolment from "../../model/enrolment";
import Message from "../../constants/message";
import { addEnrolment, updateEnrolment } from "../../api/enrolment";

import { getStudent } from "../../api/student";
import { getCourseModule } from "../../api/course_module";

// date range picker
import DateRangePicker from "vue2-daterange-picker";
import "vue2-daterange-picker/dist/vue2-daterange-picker.css";

import moment from "moment";
let startDate = null;
let endDate = null;

export default {
    props: {
        parentData: Object,
    },

    components: {
        "form-sub-module-student": () => import("../student/add.vue"),
        "form-sub-module-course-module": () =>
            import("../course-module/add.vue"),
        DateRangePicker,
    },

    data() {
        return {
            state: 0,
            enrolment: new Enrolment(),
            dateRange: { startDate, endDate },
            formSubModuleStudentSeen: false,
            formSubModuleCourseModuleSeen: false,

            dataSubForm: {
                mode: "create",
                parent: "enrolments",
                parentID: this.parentData ? this.parentData.parentID : 0,
                hidFields: ["enrolments", "id"],
            },
        };
    },

    // watch to bind enrolment.startDate & enrolment.endDate with dateRange
    watch: {
        dateRange() {
            this.enrolment.startDate = this.dateRange.startDate;
            this.enrolment.endDate = this.dateRange.endDate;
        },
    },

    computed: {
        studentId() {
            this.state;
            return this.enrolment?.student?.id || "";
        },
        studentQuickView() {
            this.state;
            return Object.values(this.enrolment?.student || {})
                .filter((e) => typeof e != "object")
                .toString()
                .replaceAll(",", " | ");
        },
        courseModuleId() {
            this.state;
            return this.enrolment?.courseModule?.id || "";
        },
        courseModuleQuickView() {
            this.state;
            return Object.values(this.enrolment?.courseModule || {})
                .filter((e) => typeof e != "object")
                .toString()
                .replaceAll(",", " | ");
        },
    },

    mounted() {
        if (this.parentData?.mode === "edit") {
            this.enrolment = this.parentData.enrolment;
            this.dataSubForm.mode = "edit";
            this.dataSubForm.enrolment = this.parentData.enrolment;
            this.dataSubForm.parentID = this.parentData.enrolment.id;

            this.dataSubForm.student = this.parentData.enrolment.student;
            this.dataSubForm.courseModule =
                this.parentData.enrolment.courseModule;
        }

        // set this.dateRange if this.enrolment.startDate & this.enrolment.endDate has true value
        if (this.enrolment.startDate && this.enrolment.endDate) {
            this.dateRange.startDate = this.enrolment.startDate;
            this.dateRange.endDate = this.enrolment.endDate;
        }
    },

    methods: {
        create() {
            var result = addEnrolment(this.enrolment);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_ENROLMENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.ADD_ENROLMENT_ERR + " - " + error.message
                    );
                })
                .finally(() => {});
        },

        unlinkStudent() {
            this.enrolment.student = null;
        },

        getStudentById(event) {
            let studentId = event.target.value;

            var result = getStudent(studentId);
            result
                .then((res) => {
                    this.enrolment.student = res.data;
                    this.dataSubForm.student = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_STUDENT_ERR + " - " + error.message
                    );
                })
                .finally(() => {});
        },

        unlinkCourseModule() {
            this.enrolment.courseModule = null;
        },

        getCourseModuleById(event) {
            let courseModuleId = event.target.value;

            var result = getCourseModule(courseModuleId);
            result
                .then((res) => {
                    this.enrolment.courseModule = res.data;
                    this.dataSubForm.courseModule = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_COURSE_MODULE_ERR + " - " + error.message
                    );
                })
                .finally(() => {});
        },

        update() {
            var result = updateEnrolment(this.enrolment.id, this.enrolment);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_ENROLMENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.UPDATE_ENROLMENT_ERR + " - " + error.message
                    );
                })
                .finally(() => {});
        },

        formatDate(value, format) {
            // using moment.js third party lib to format date
            return moment(value).format(format);
        },

        onSubmit() {
            if (this.parentData.mode == "create") {
                this.create();
            } else {
                console.log(this.enrolment);
                this.update();
            }
        },
    },

    filters: {
        formatDate(value, format) {
            // using moment.js third party lib to format date
            return moment(value).format(format);
        },
    },
};
</script>
