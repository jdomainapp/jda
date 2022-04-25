<template>
    <form-edit v-bind:data="data"></form-edit>
</template>
<script>
    import Edit from '../../student/edit.vue'
    import Form from '../../../model/form/student';

    export default {
        props: {
            studentId: Object
        },

        components: {
            "form-edit": Edit
        },

        data() {
            return {
                data: {
                    studentId: this.studentId,
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