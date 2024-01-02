<template src="./template/index.html"></template>
<script>
import { store } from "../../constants/store";

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
            store: store,
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

    // for reactive data
    // computed: {
    //     display: {
    //         get() {
    //             return store.display;
    //         },
    //         set(value) {
    //             mutations.setDisplay(value);
    //         },
    //     },
    // },

    // when start import component, change display to 2
    // created() {
    //     mutations.setDisplay(2);
    // },

    // when about to destroy component, change display to null
    // beforeDestroy() {
    //     mutations.setDisplay(null);
    // },

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
