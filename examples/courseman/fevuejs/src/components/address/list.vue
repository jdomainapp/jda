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
                dataSubForm: {
                    address: null,
                    parent: "addresses",
                    parentID: this.parentData ? this.parentData.parentID : 0,
                    mode: "edit"
                }
            }
        },

        mounted() {
            this.getAddresses()
        },

        methods: {
            emitData(address) {
                console.log("emitData",address);
                this.$emit("data", {address,mode:"edit"});
            },

            getAddressId(id) {
                this.addressId = id;
            },

            getAddresses() {
                
                var result = getAllAddresses();
                result.then(response => {
                    this.addresses = response.data.content;
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

                    this.$toast.success(Message.DELETE_ADDRESS_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_ADDRESS_ERR + ' - ' + e.message);
                })
            },
        },
    };
</script>