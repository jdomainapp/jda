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
            dataSubForm: {
                mode: "create",
                parent: this.parentData ? this.parentData.parent : "addresses",
                parentID: this.parentData ? this.parentData.parentID : 0,
                address: null,
            },

            searchName: "",
        };
    },

    mounted() {},

    // watch for changes searchName
    watch: {
        searchName: function (val) {
            // if val is not empty then go to display 2
            if (val) {
                this.display = 3;
            }
        },
    },

    methods: {
        setData(data) {
            console.log("set address", data);
            this.dataSubForm.mode = data.mode;
            this.dataSubForm.address = data.address;
            this.display = 1;
        },

        mainForm() {
            this.display = 2;
            this.dataSubForm.mode = "create";
        },
    },
};
</script>
