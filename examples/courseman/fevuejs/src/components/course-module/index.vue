<template src="./template/index.html"></template>
<script>
export default {
    props: {
        parentData: Object,
    },

    components: {
        "form-add": () => import("./add.vue"),
        "form-list": () => import("./list.vue"),
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
        };
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
