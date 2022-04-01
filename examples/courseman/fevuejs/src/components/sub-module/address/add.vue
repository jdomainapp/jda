<template>
    <form-add v-bind:data="form"></form-add>
</template>
<script>
    import Add from '../../address/add.vue'
    import Form from '../../../model/form/address';

    export default {
        components: {
            "form-add": Add
        },

        data() {
            return {
                form: new Form()
            };
        },

        mounted() {
            this.setForm();
        },

        methods: {
            setForm(){
                this.form.setHidSubModule(false)
            }
        },
    };
</script>