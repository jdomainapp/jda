<template src="./template/edit.html"></template>
<script>
    import Enrolment from '../../model/enrolment';
    import Message from '../../constants/message';
    import {getEnrolment, updateEnrolment} from '../../api/enrolment';

    export default({
        props: {
            subEnrolmentId:Object
        },

        data() {
            return {
                enrolment: new Enrolment(),
                enrolmentId: this.subEnrolmentId,
            }
        },

        mounted() {
            this.getEnrolmentById()
        },

        methods: {
            getEnrolmentById() {
                var result = getEnrolment(this.enrolmentId);
                result.then((res) => {
                    this.enrolment = res.data;
                })
                .catch((error) => {
                    this.$toast.error(Message.GET_ENROLMENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateEnrolment(this.enrolmentId, this.enrolment);
                result.then((res) => {
                    console.log(res);
                    this.$toast.success(Message.UPDATE_ENROLMENT_SUC);
                })
                .catch((error) => {
                    this.$toast.error(Message.UPDATE_ENROLMENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        }
    })
</script>
