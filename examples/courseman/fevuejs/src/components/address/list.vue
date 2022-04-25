<template src="./template/list.html"></template>
<script>
    import {getAllAddresses, deleteAddress} from '../../api/address';
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';

    export default {
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                addresses: [],
                addressId: 0,
                data: {
                    addressId: 0,
                }
            }
        },

        mounted() {
            this.getAddresses()
        },

        methods: {
            emitData(id) {
                this.data.addressId = id;
                this.$emit("data", this.data);
            },

            getAddressId(id) {
                this.addressId = id;
            },

            getAddresses() {
                var result = getAllAddresses();
                result.then(response => {
                    this.addresses = response.data;
                })
                .catch(e => {
                    this.$toast.error(Message.GET_LIST_ADDRESS_ERR + ' - ' + e.message);
                })
            },

            deleteAddress(id) {
                var result = deleteAddress(id);
                result.then(response => {
                    console.log(response);

                    this.getAddresses();

                    this.$toast.success(Message.DELETE_STUDENT_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_ADDRESS_ERR + ' - ' + e.message);
                })
            },
        },
    };
</script>