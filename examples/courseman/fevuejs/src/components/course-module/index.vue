<template src="./template/index.html"></template>
<script>
import PatternService from '../common/pattern/pattern.service';
import AutoSearchFactory from '../common/patterns/autosearch/autosearch.factory';

export default {
    props: {
        parentData: Object,
        // parentID: String,
    },

    components: {
        "form-add": () => import("./add.vue"),
        "form-list": () => import("./list.vue"),
        "view-region": () => import("../common/pattern/viewregion.vue"),
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

            // Provider-Consumer pattern
            patternService: new PatternService(),

            // Search component's data
            searchKeyword: "",
            searchID: "",
            items: [],
        };
    },

    created() {
        this.patternService.addConsumer(AutoSearchFactory.createProviderConsumer({ host: this }));
    },

    mounted() { },

    watch: {
        searchKeyword: {
            handler(newVal) {
                this.handleSearchChange(newVal, this.searchID);
            },
        },

        searchID: {
            handler(newVal) {
                this.handleSearchChange(this.searchKeyword, newVal);
            },
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

        handleSearchChange(keyword, id) {
            keyword = keyword.trim();
            id = id.trim();
            const hasId = id !== "";
            const hasKeyword = keyword !== "";

            // If id and keyword are empty, return back to last display (create || edit)
            // HOWEVER, when display change, the list will be re-rendered => fetch data again
            if (!(hasId || hasKeyword)) {
                this.display = this.cache_display;
            } else {
                this.cache_display = this.display === 3 ? 1 : this.display;
                this.display = 3;
            }
        },

        updateList(newList) {
            // this.items = newList;
            // Trick (i guess) is to keep the reference of the array
            // The above line will not work because the reference of the array is changed
            this.items.splice(0, this.items.length, ...newList);
        },
    },
};
</script>
