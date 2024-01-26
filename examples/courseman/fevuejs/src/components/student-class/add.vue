<template src="./template/add.html"></template>
<script>
import StudentClass from "../../model/student_class";
import Message from "../../constants/message";
import { addStudentClass, updateStudentClass } from "../../api/student_class";

import { getStudent } from "../../api/student";
import { mutations } from "../../constants/store";

export default {
    props: {
        parentData: Object,
        parentID: String
    },

    components: {
        "form-sub-module-student": () => import("../student/index.vue"),
    },

    data() {
        return {
            state: 0,
            studentClass: new StudentClass(),

            formSubModuleStudentSeen: false,
            dataSubForm: {
                mode: "create",
                parent: "student-classes",
                parentID: this.parentData ? this.parentData.parentID : 0,
                hidFields: ["studentClass", "id"], // hidFields should be hashset
            },

            tree: {
                parentID: this.parentID ? this.parentID : "",
                observableTree: []
            },
        };
    },

    computed: {},

    created() {
        const parentID = this.tree.parentID;

        this.tree.observableTree = [
            {
                name: "Id",
                id: "ID",
                display: this.hidFields('id'),
            },
            {
                name: "Name",
                id: "Name",
                display: this.hidFields('name'),
            },
            {
                name: "Form: Student",
                id: "FormStudent",
                display: this.hidFields('students'),
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
        this.tree.observableTree.forEach((item) => {
            mutations.deleteItem(item);
        });
    },

    mounted() {
        if (this.parentData?.mode === "edit") {
            this.studentClass = this.parentData.studentClass;
            this.dataSubForm.mode = "edit";
            this.dataSubForm.studentClass = this.parentData.studentClass;
            this.dataSubForm.parentID = this.parentData.studentClass.id;
        }
    },

    methods: {
        create() {
            var result = addStudentClass(this.studentClass);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_STUDENT_CLASS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.ADD_STUDENT_CLASS_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        unlinkStudent() {
            this.studentClass.student = null;
        },

        getStudentById(event) {
            let studentId = event.target.value;

            var result = getStudent(studentId);
            result
                .then((res) => {
                    this.student_class.student = res.data;
                    this.dataSubForm.student = res.data;
                    this.state = (this.state + 1) % 2;
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.GET_STUDENT_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        update() {
            var result = updateStudentClass(
                this.studentClass.id,
                this.studentClass
            );
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_STUDENT_CLASS_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.UPDATE_STUDENT_CLASS_ERR + " - " + error.message
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
        },
    },
};
</script>
