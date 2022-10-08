<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';
    import {getAllEnrolments, deleteEnrolment} from '../../api/enrolment';
    // import Student from "../../model/student";
    // import Address from "../../model/address";

    export default ({
        props: {
            parentData:Object,
        },
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                // eDisplay: 1,
                enrolments: [],
                enrolmentId: 0,
                parentID:'',
                dataSubForm: {
                    mode: "create",
                    parentID: ''
                }               
            }
        },

        mounted() {
            this.getEnrolments();

            if(this.parentData.parentID != '' && this.parentData.parentID != undefined){
                this.eDisplay = 2;
                this.parentID = this.parentData.parentID;
            }
            if (this.parentData.mode === "edit") {
                this.parentID = this.parentData.parentID;
                console.log("From student enrolment" + this.parentID);
            }
        },

        methods: {
            emitData(enrolment) {
                this.parentData.enrolment = enrolment;
                this.parentData.mode = 'edit';
                console.log('Edit' + this.parentData.mode);
                this.$emit("data", this.parentData);
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
