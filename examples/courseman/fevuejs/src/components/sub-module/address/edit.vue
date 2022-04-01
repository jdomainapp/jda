<template>
    <form-edit v-bind:data="data"></form-edit>
</template>
<script>
    import Edit from '../../address/edit.vue'
    import Form from '../../../model/form/address';

    export default {
        props: {
            addressId: Object
        },

        components: {
            "form-edit": Edit
        },

        data() {
            return {
                data: {
                    addressId: this.addressId,
                    form: new Form()
                },
            };
        },

        mounted() {
            this.setForm();
        },

        methods: {
            setForm(){
                this.data.form.setHidSubModule(false)
            }
        },
    };
</script>