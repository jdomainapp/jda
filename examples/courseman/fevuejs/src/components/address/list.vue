<template src="./template/list.html"></template>
<script>
import { getAllAddresses, deleteAddress } from "../../api/address";

import ModalConfirm from "../modal/confirm.vue";
import Message from "../../constants/message";

export default {
    props: {
        search: {
            type: Object,
            default: () => {
                return {
                    id: "",
                    keyword: "",
                };
            },
            required: true,
        }
    },

    components: {
        "modal-confirm": ModalConfirm,
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
            return this.filterResults.length;
        },

        filterResults() {
            const keyword = this.search.keyword.toLowerCase();
            const id = this.search.id.toLowerCase();

            return this.addresses.filter((s) => {
                let matchesId = true;
                let matchesKeyword = true;

                if (id !== "") {
                    matchesId = s.id.toString().toLowerCase().includes(id);
                }

                if (keyword !== "") {
                    matchesKeyword = s.name.toLowerCase().includes(keyword);
                }

                return matchesId && matchesKeyword;
            });
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
