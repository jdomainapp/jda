<template src="./template/index.html"></template>
<script>

export default {
    props: {
        parentData: Object,
    },

    components: {
        "form-add": () => import("./add.vue"),
        "form-list": () => import("./list.vue"),
        "auto-search": () => import("../common/patterns/autosearch/index.vue"),
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

            // tree: {
            //     parentID: this.parentID ? this.parentID : "",
            //     observableTree: []
            // },
        };
    },


    // created() {
    //     const parentID = this.tree.parentID;

    //     this.tree.observableTree = [
    //         {
    //             name: "Form: Student Class",
    //             id: "FormStudentClass",
    //             display: true, // no hidfields so automatically = true
    //         },
    //     ].map((item) => {
    //         item.parentID = parentID;
    //         item.id = parentID + item.id;
    //         return item;
    //     });

    //     // this.tree.observableTree.forEach((item) => {
    //     //     mutations.addItem(item);
    //     // });
    // },

    mounted() { },

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
