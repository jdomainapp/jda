<template src="./template/list.html"></template>
<script>
import { getAllAddresses, deleteAddress } from "../../api/address";

import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";
import { BPagination, BTable, BFormSelect } from "bootstrap-vue";

export default {
    components: {
        "modal-confirm": ModalConfirm,
        BPagination,
        BTable,
        BFormSelect
    },

    data() {
        return {
            addresses: [],
            addressId: 0,
            dataSubForm: {
                address: null,
                parent: "addresses",
                parentID: this.parentData ? this.parentData.parentID : 0,
                mode: "edit",
            },

            page: {
                perPage: 5,
                currentPage: 1,
                fields: [
                    { key: "#", label: "#" },
                    { key: "id", label: "ID" },
                    { key: "name", label: "City name" },
                    { key: "student", label: "Student" },
                    { key: "action", label: "Action" },
                ],

                options: [
                    { value: 5, text: '5 rows / page' },
                    { value: 10, text: '10 rows / page' },
                    { value: 20, text: '20 rows / page' },
                ]
            },
        };
    },

    mounted() {
        this.getAddresses();
    },

    computed: {
        rows() {
            return this.addresses.length;
        },
    },

    methods: {
        emitData(address) {
            console.log("emitData", address);
            this.$emit("data", { address, mode: "edit" });
        },

        getAddressId(id) {
            this.addressId = id;
        },

        getAddresses() {
            var result = getAllAddresses();
            result
                .then((response) => {
                    this.addresses = response.data.content;
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.GET_LIST_ADDRESS_ERR + " - " + e.message
                    );
                });
        },

        deleteAddress(id) {
            var result = deleteAddress(id);
            result
                .then((response) => {
                    console.log(response);

                    this.getAddresses();

                    this.$toast.success(Message.DELETE_ADDRESS_SUC);
                })
                .catch((e) => {
                    this.$toast.error(
                        Message.DELETE_ADDRESS_ERR + " - " + e.message
                    );
                });
        },
    },
};
</script>
