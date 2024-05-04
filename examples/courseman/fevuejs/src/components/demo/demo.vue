<template src="./template/demo.html"></template>

<script>
// date range picker
import DateRangePicker from "vue2-daterange-picker";
import "vue2-daterange-picker/dist/vue2-daterange-picker.css";

// vue slider
import VueSlider from "vue-slider-component";
import "vue-slider-component/theme/antd.css";

// rating
import StarRating from "vue-star-rating";

import ViewRegion from '../common/pattern/viewregion.vue';
import PatternService from '../common/pattern/pattern.service';

import AutoSearchFactory from '../common/patterns/autosearch/autosearch.factory';

import AccordionMenuFactory from '../common/patterns/accordionmenu/accordion.factory';
import AccordionMenu from '../common/patterns/accordionmenu/template/index.vue';

// let startDate = new Date();
// let endDate = new Date();

export default {
    components: { DateRangePicker, VueSlider, StarRating, ViewRegion, AccordionMenu },

    data() {
        return {
            id: null,
            patternService: new PatternService(),

            // Date range picker
            // dateRange: { startDate, endDate },
            // Vue slider
            // range: [0, 30],
            // perPage: 3,
            // currentPage: 1,

            // begin search
            items: [
                { id: 1, first_name: "Fred", last_name: "Flintstone" },
                { id: 2, first_name: "Wilma", last_name: "Flintstone" },
                { id: 3, first_name: "Barney", last_name: "Rubble" },
                { id: 4, first_name: "Betty", last_name: "Rubble" },
                { id: 5, first_name: "Pebbles", last_name: "Flintstone" },
                { id: 6, first_name: "Bamm Bamm", last_name: "Rubble" },
                { id: 7, first_name: "The Great", last_name: "Gazzoo" },
                { id: 8, first_name: "Rockhead", last_name: "Slate" },
                { id: 9, first_name: "Pearl", last_name: "Slaghoople" },
            ],

            // pattern service need "items" as wrote in autosearch.pattern.js
            searchKeyword: "",
            searchID: "",
            // end search

        };
    },

    created() {
        this.patternService.addConsumer(AutoSearchFactory.createProviderConsumer({ host: this }));
        this.patternService.addConsumer(AccordionMenuFactory.createProviderConsumer({ host: this }));
    },

    mounted() {
        this.id = this._uid
    }

    // watch: {
    //     // Check if the searchKeyword is changed
    //     searchKeyword: {
    //         handler(newVal) {
    //             console.log(newVal);
    //         },
    //         deep: true,
    //     },
    // },

    // computed: {
    //     rows() {
    //         return this.items.length;
    //     },
    // },

    // filters: {
    //     date(val) {
    //         return val ? val.toLocaleString() : "";
    //     },
    // },
};
</script>

<style scoped>
.input_date {
    min-width: 350px;
}

span.input {
    display: block;
    width: 100%;
    border: 1px solid black;
    resize: none;
    padding: 4px;
}
</style>
