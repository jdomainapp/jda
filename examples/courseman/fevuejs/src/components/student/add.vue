<template src="./template/add.html"></template>
<script>
import Student from "../../model/student";
import Message from "../../constants/message";
import { addStudent, updateStudent } from "../../api/student";

import { getAddress } from "../../api/address";
import { getStudentClass } from "../../api/student_class";
import { getEnrolment } from "../../api/enrolment";

import { mutations } from "../../constants/store";

export default {
    props: {
        parentData: Object,
        parentID: String,
    },

    components: {
        "form-sub-module-address": () => import("../address/add.vue"),
        "form-sub-module-student-class": () =>
            import("../student-class/add.vue"),

        "form-sub-module-enrolment": () => import("../enrolment/index.vue"),
    },

    data() {
        return {
            state: 0,
            student: new Student(),
            formSubModuleAddressSeen: false,
            formSubModuleStudentClassSeen: false,

            formSubModuleEnrolmentSeen: false,
            dataSubForm: {
                mode: "create",
                parent: "students",
                parentID: this.parentData ? this.parentData.parentID : 0,
                hidFields: ["student", "id", "students", "id", "student", "id"],
            },

            tree: {
                parentID: this.parentID ? this.parentID : "",
                observableTree: []
            },
        };
    },

    created() {
        const parentID = this.tree.parentID;

        this.tree.observableTree = [
            {
                name: "Student ID",
                id: "StudentID",
                display: this.hidFields('id'),
            },
            {
                name: "Full Name",
                id: "FullName",
                display: this.hidFields('name'),
            },
            {
                name: "Date of birth",
                id: "DateOfBirth",
                display: this.hidFields('dob'),
            },
            {
                name: "Email",
                id: "Email",
                display: this.hidFields('email'),
            },
            {
                name: "Gender",
                id: "Gender",
                display: this.hidFields('gender'),
            },
            {
                name: "Address ID",
                id: "AddressAdd",
                display: this.hidFields('address'),
            },
            {
                name: "Address",
                id: "Address",
                display: this.hidFields('address'),
            },
            {
                name: "Student class ID",
                id: "StudentClassAdd",
                display: this.hidFields('studentClass'),
            },
            {
                name: "Student class",
                id: "StudentClass",
                display: this.hidFields('studentClass'),
            },
            {
                name: "Form: Enrolment",
                id: "Enrolment",
                display: this.hidFields('enrolments'),
            }
        ].map((item) => {
            item.parentID = parentID;
            item.id = parentID + item.id;
            return item;
        });

        this.tree.observableTree.forEach((item) => {
            mutations.addItem(item);
        });
    },

    destroyed() {
        // mutations.deleteItem(this.observableTree);
        this.tree.observableTree.forEach((item) => {
            mutations.deleteItem(item);
        });
    },

    computed: {
        addressId() {
            this.state;
            return this.student?.address?.id || "";
        },
        addressQuickView() {
            this.state;
            return Object.values(this.student?.address || {})
                .filter((e) => typeof e != "object")
                .toString()
                .replaceAll(",", " | ");
        },
        studentClassId() {
            this.state;
            return this.student?.studentClass?.id || "";
        },
        studentClassQuickView() {
            this.state;
            return Object.values(this.student?.studentClass || {})
                .filter((e) => typeof e != "object")
                .toString()
                .replaceAll(",", " | ");
        },
    },

    mounted() {
        if (this.parentData?.mode === "edit") {
            this.student = this.parentData.student;
            this.dataSubForm.mode = "edit";
            this.dataSubForm.student = this.parentData.student;
            this.dataSubForm.parentID = this.parentData.student.id;

            this.dataSubForm.address = this.parentData.student.address;
            this.dataSubForm.studentClass =
                this.parentData.student.studentClass;
        }
    },

    methods: {
        create() {
            var result = addStudent(this.student);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_STUDENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.ADD_STUDENT_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        unlinkAddress() {
            this.student.address = null;
        },

        getAddressById(event) {
            let addressId = event.target.value;

            var result = getAddress(addressId);
            result
                .then((res) => {
                    this.student.address = res.data;
                    this.dataSubForm.address = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_ADDRESS_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        unlinkStudentClass() {
            this.student.studentClass = null;
        },

        getStudentClassById(event) {
            let studentClassId = event.target.value;

            var result = getStudentClass(studentClassId);
            result
                .then((res) => {
                    this.student.studentClass = res.data;
                    this.dataSubForm.studentClass = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_STUDENT_CLASS_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        unlinkEnrolment() {
            this.student.enrolment = null;
        },

        getEnrolmentById(event) {
            let enrolmentId = event.target.value;

            var result = getEnrolment(enrolmentId);
            result
                .then((res) => {
                    this.student.enrolment = res.data;
                    this.dataSubForm.enrolment = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_ENROLMENT_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        update() {
            var result = updateStudent(this.student.id, this.student);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_STUDENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.UPDATE_STUDENT_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        onSubmit() {
            if (this.parentData.mode == "create") {
                this.create();
            } else {
                this.update();
            }
        },

        hidFields(field) {
            return !this.parentData
                || !this.parentData.hidFields
                || !this.parentData.hidFields.includes(field);
        }
    },
};
</script>
