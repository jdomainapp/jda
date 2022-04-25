<template>
    <form-add v-bind:data="data"></form-add>
</template>
<script>
    import Add from '../../student/add.vue'
    import Form from '../../../model/form/student';

    export default {
        components: {
            "form-add": Add
        },

        data() {
            return {
                data: {
                    form: new Form()
                },
            };
        },

        mounted() {
            this.setForm();
        },

        methods: {
            setForm(){
                this.data.form.setHidAddress(false)
            }
        },
    };
</script>