<template src="./template/index.html"></template>
<script>
import { mutations } from '../../constants/store';

export default {
    props: {
        parentData: Object,
        parentID: String,
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
                parent: this.parentData ? this.parentData.parent : "enrolments",
                parentID: this.parentData ? this.parentData.parentID : 0,
                enrolment: null,
            },

            search: {
                id: "",
                keyword: "",
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
                name: "Form: Enrolment",
                id: "FormEnrolment",
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
        }
    },

    methods: {
        setData(data) {
            console.log("set enrolment", data);
            this.dataSubForm.mode = data.mode;
            this.dataSubForm.enrolment = data.enrolment;
            this.display = 1;
        },

        mainForm() {
            this.display = 2;
            this.dataSubForm.mode = "create";
        },

        // hidFields(field) {
        //     return !this.parentData
        //         || !this.parentData.hidFields
        //         || !this.parentData.hidFields.includes(field);
        // }
        // <- No need as there is no hidFields in index.vue
    },
};
</script>
