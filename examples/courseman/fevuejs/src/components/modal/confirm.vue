<template src="./template/confirm.html"></template>
<script>
export default {
    data() {
        return {
            title: "Confirm",
            text: "Delete this resource?",
        };
    },

    mounted() {},

    methods: {
        confirm() {
            this.$emit("confirm", "ok");
        },
    },
};
</script>
