<template src="./template/demo.html"></template>

<script>
import DateRangePicker from "vue2-daterange-picker";
import "vue2-daterange-picker/dist/vue2-daterange-picker.css";

let startDate = new Date();
let endDate = new Date();

export default {
    components: { DateRangePicker },
    data() {
        return {
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
</style>
