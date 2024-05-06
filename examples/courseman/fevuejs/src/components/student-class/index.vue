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
                parent: this.parentData
                    ? this.parentData.parent
                    : "student-classes",
                parentID: this.parentData ? this.parentData.parentID : 0,
                studentClass: null,
            },
        };
    },

    mounted() {},

    methods: {
        setData(data) {
            console.log("set studentClass", data);
            this.dataSubForm.mode = data.mode;
            this.dataSubForm.studentClass = data.studentClass;
            this.display = 1;
            // mutations.setDisplay(1);
        },

        mainForm() {
            this.display = 2;
            // mutations.setDisplay(2);
            this.dataSubForm.mode = "create";
        },
    },
};
</script>
