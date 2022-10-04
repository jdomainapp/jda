<template src="./template/list.html"></template>
<script>
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';
    import {getAllEnrolments, deleteEnrolment} from '../../api/enrolment';
    // import Student from "../../model/student";
    // import Address from "../../model/address";

    export default ({
        props: {
            data:Object
        },
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                eDisplay: 1,
                enrolments: [],
                enrolmentId: 0,
                // enrolmentIn: '',
                parentID:'',
                dataSubForm: {
                    mode: "create",
                    // enrolmentIn: '',
                    parentID: ''
                }               
            }
        },

        mounted() {
            this.getEnrolments();

            // if(this.data.enrolmentIn != '' && this.data.enrolmentIn != undefined){
            if(this.data.parentID != '' && this.data.parentID != undefined){
                this.eDisplay = 2;
                // this.enrolmentIn = this.data.enrolmentIn;
                this.parentID = this.data.parentID;
            }
            if (this.data.mode === "edit") {
                // this.enrolmentIn = this.data.enrolmentIn;
                this.parentID = this.data.parentID;
                // this.dataSubForm.mode = "edit";
                // this.dataSubForm.student = this.data.address.student;
                console.log("From student enrolment" + this.parentID);
            }
        },

        methods: {
            emitData(enrolment) {
                this.data.enrolment = enrolment;
                this.data.mode = 'edit';
                console.log('Edit' + this.data.mode);
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
