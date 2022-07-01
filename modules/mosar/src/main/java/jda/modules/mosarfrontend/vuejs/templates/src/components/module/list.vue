<template src="./template/list.html"></template>
<script>
    import {getAll@slot{{ModuleNames}}, delete@slot{{ModuleName}}} from '../../api/@slot{{module_name}}';
    import ModalConfirm from '../modal/confirm.vue';
    import Message from '../../constants/message';

    export default {
        components: {
            "modal-confirm": ModalConfirm
        },

        data() {
            return {
                @slot{{moduleNames}}: [],
                @slot{{moduleName}}Id: 0,
                data: {
                    @slot{{moduleName}}Id: 0,
                }
            }
        },

        mounted() {
            this.get@slot{{ModuleNames}}()
        },

        methods: {
            emitData(id) {
                this.data.@slot{{moduleName}}Id = id;
                this.$emit("data", this.data);
            },

            get@slot{{ModuleName}}Id(id) {
                this.@slot{{moduleName}}Id = id;
            },

            get@slot{{ModuleNames}}() {
                var result = getAll@slot{{ModuleNames}}();
                result.then(response => {
                    this.@slot{{moduleNames}} = response.data;
                })
                .catch(e => {
                    this.$toast.error(Message.GET_LIST_ADDRESS_ERR + ' - ' + e.message);
                })
            },

            delete@slot{{ModuleName}}(id) {
                var result = delete@slot{{ModuleName}}(id);
                result.then(response => {
                    console.log(response);

                    this.get@slot{{ModuleNames}}();

                    this.$toast.success(Message.DELETE_STUDENT_SUC);
                })
                .catch(e => {
                    this.$toast.error(Message.DELETE_ADDRESS_ERR + ' - ' + e.message);
                })
            },
        },
    };
</script>