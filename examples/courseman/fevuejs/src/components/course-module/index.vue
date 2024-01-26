<template src="./template/index.html"></template>
<script>
import { mutations } from '../../constants/store';

export default {
    props: {
        parentData: Object,
        parentID: String,
        // Checkpoint: Làm tiếp phần này
    },

    components: {
        "form-add": () => import("./add.vue"),
        "form-list": () => import("./list.vue"),
        "auto-search": () => import("../common/patterns/autosearch/index.vue"),
    },

    data() {
        return {
            display: 1,
            cache_display: 1,
            dataSubForm: {
                mode: "create",
                parent: this.parentData
                    ? this.parentData.parent
                    : "course-modules",
                parentID: this.parentData ? this.parentData.parentID : 0,
                courseModule: null,
            },

            search: {
                id: "",
                keyword: "",
            },

            tree: {
                parentID: this.parentID ? this.parentID : "",
                observableTree: [],
            }
        };
    },

    created() {
        const parentID = this.tree.parentID;

        this.tree.observableTree = [
            {
                name: "Form: Course module",
                id: "FormCourseModule",
                display: true, // no hidfields so automatically = true
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

    mounted() { },

    watch: {
        search: {
            handler: function (val) {
                val.id = val.id.trim();
                val.keyword = val.keyword.trim();
                const id = val.id !== "";
                const keyword = val.keyword !== "";

                // If id and keyword are empty, return back to last display (create || edit)
                // HOWEVER, when display change, the list will be re-rendered => fetch data again
                if (!(id || keyword)) {
                    this.display = this.cache_display;
                } else {
                    this.cache_display = this.display === 3 ? 1 : this.display;
                    this.display = 3;
                }
            },
            deep: true,
        },
    },

    methods: {
        setData(data) {
            console.log("set courseModule", data);
            this.dataSubForm.mode = data.mode;
            this.dataSubForm.courseModule = data.courseModule;
            this.display = 1;
        },

        mainForm() {
            this.display = 2;
            this.dataSubForm.mode = "create";
        },
    },
};
</script>
