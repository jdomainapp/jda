<template src="./template/add.html"></template>
<script>
import CourseModule from "../../model/course_module";
import Message from "../../constants/message";
import {
    addCourseModule,
    updateCourseModule,
    getCourseModule,
} from "../../api/course_module";

import VueSlider from "vue-slider-component";
import "vue-slider-component/theme/antd.css";

import StarRating from "vue-star-rating";

import { mutations } from "../../constants/store";
// import { store } from "../../constants/store";


export default {
    props: {
        parentData: Object,
        parentID: String,
    },

    components: { VueSlider, StarRating },

    data() {
        return {
            state: 0,
            courseModule: new CourseModule(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            ),

            dataSubForm: {
                mode: "create",
                parent: "course-modules",
                parentID: this.parentData ? this.parentData.parentID : 0,
                hidFields: [],
            },

            cost: {
                value: 100,
                min: 100,
                max: 500,
            },

            tree: {
                parentID: this.parentID ? this.parentID : "",
                observableTree: [],
            },
        };
    },

    computed: {},

    created() {
        const parentID = this.tree.parentID;

        this.tree.observableTree = [
            {
                name: "Type",
                id: "Type",
                display: true,
            },
            {
                name: "Id",
                id: "ID",
                display: this.hidFields("id"),
            },
            {
                name: "Code",
                id: "Code",
                display: this.hidFields('code'),
            },
            {
                name: "Name",
                id: "Name",
                display: this.hidFields("name"),
            },
            {
                name: "Semester",
                id: "Semester",
                display: this.hidFields("semester"),
            },
            {
                name: "Cost",
                id: "Cost",
                display: this.hidFields("description"),
            },
            {
                name: "Rating",
                id: "Rating",
                display: this.hidFields("description"),
            },
            {
                name: "Description",
                id: "Description",
                display: this.hidFields("description"),
            },
            {
                name: "Credits",
                id: "Credits",
                display: this.hidFields("credits"),
            },
            {
                name: "Dept. Name",
                id: "DeptName",
                display: false,
            },
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

    watch: {
        "courseModule.type": function (val) {
            switch (val) {
                case "elective":
                    this.tree.observableTree[9].display = this.hidFields("deptName") && val == 'elective';
                    mutations.addItem(this.tree.observableTree[9]);
                    break;
                default:
                    mutations.deleteItem(this.tree.observableTree[9]);
                    break;
            }
        },
    },

    mounted() {
        if (this.parentData?.mode === "edit") {
            this.courseModule = this.parentData.courseModule;
            this.dataSubForm.mode = "edit";
            this.dataSubForm.courseModule = this.parentData.courseModule;
            this.dataSubForm.parentID = this.parentData.courseModule.id;
        }
        this.getRealType();
    },

    methods: {
        getRealType() {
            if (this.data.mode === "edit") {
                let result = getCourseModule(this.courseModule.id);
                result
                    .then((response) => {
                        this.courseModule = response.data;
                    })
                    .catch((e) => {
                        this.$toast.error(
                            Message.GET_COURSE_MODULE_ERR + " - " + e.message
                        );
                    });
            }
        },

        create() {
            var result = addCourseModule(this.courseModule);
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.ADD_COURSE_MODULE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.ADD_COURSE_MODULE_ERR + " - " + error.message
                    );
                })
                .finally(() => { });
        },

        update() {
            var result = updateCourseModule(
                this.courseModule.id,
                this.courseModule
            );
            result
                .then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_COURSE_MODULE_SUC);
                })
                .catch((error) => {
                    this.$toast.error(
                        Message.UPDATE_COURSE_MODULE_ERR + " - " + error.message
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

<style scoped>
.form-input-span {
    display: block;
    width: 100%;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 10px;
}
</style>
