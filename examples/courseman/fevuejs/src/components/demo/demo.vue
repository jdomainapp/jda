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

let startDate = new Date();
let endDate = new Date();

export default {
    components: { DateRangePicker, VueSlider, StarRating },

    data() {
        return {
            // Date range picker
            dateRange: { startDate, endDate },
            options: {
                format: "DD/MM/YYYY",
                useCurrent: false,
                showDropdowns: true,
                showWeekNumbers: true,
                showISOWeekNumbers: true,
                timePicker: true,
                timePicker24Hour: true,
                timePickerSeconds: true,
                autoApply: true,
                locale: {
                    cancelLabel: "Clear",
                },
            },

            // Vue slider
            range: [0, 30],
        };
    },

    filters: {
        date(val) {
            return val ? val.toLocaleString() : "";
        },
    },
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
