<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';
    import {getAllEnrolments, deleteEnrolment} from '../../api/enrolment';
    
    export default ({
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                enrolments: [],
                enrolmentId: 0,
                data: {
                    enrolmentId: 0,
                }
            }
        },

        mounted() {
            this.getEnrolments()
        },

        methods: {
            emitData(id) {
                this.data.enrolmentId = id;
                this.$emit("data", this.data);
            },

            getEnrolmentId(id) {
                this.enrolmentId = id;
            },

            getEnrolments() {
                var result = getAllEnrolments();
                result.then(response => {
                    this.enrolments = response.data;
                })
                .catch(e => {
                    this.$toast.error(Message.GET_LIST_ENROLMENT_ERR + ' : ' + e.message);
                })
            },

            deleteEnrolment(id) {
                var result = deleteEnrolment(id);             
                result.then(response => {
                    console.log(response);

                    this.getEnrolments();

                    this.$toast.success(Message.DELETE_ENROLMENT_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_ENROLMENT_ERR + ' - ' + e.message);
                })
            },
        }
    })
</script>
