<template src="./template/edit.html"></template>
<script>
    import @slot{{ModuleName}} from '../../model/@slot{{module_name}}';
    import Student from '../../model/student';
    import Message from '../../constants/message';
    import AddressForm from '../../model/form/@slot{{module_name}}';
    import {updateAddress, getAddress} from '../../api/@slot{{module_name}}';
    import {getStudent} from '../../api/student';

    export default {
        props: {
            subAddressId:Object
        },

        components: {
            "form-sub-module-student": () => import('../student/edit.vue'),
        },

        data() {
            return {
                @slot{{module_name}}: new @slot{{ModuleName}}(),               
                student: new Student(),
                addressId: this.subAddressId,
                form:  new AddressForm(),
                formSubModuleStudentSeen: false,
            }
        },

        mounted() {
            this.getAddressById();
            this.setFrom();
        },

        methods: {
            setFrom() {
                if (this.subAddressId == undefined) {
                    this.form.setHidSubModule(false)
                    console.log(this.form.hidSubModule)
                }
            },

            getAddressById() {
                console.log(this.addressId);
                var result = getAddress(this.addressId);

                result.then((res) => {
                    this.@slot{{module_name}} = res.data;

                    if (res.data.student !== undefined) {
                        this.student = res.data.student;
                    }
                })
                .catch((error) => {
                    this.\$toast.error(Message.GET_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            getStudentById(event) {
                let studentId =  event.target.value;

                var result = getStudent(studentId)
                result.then((res) => {
                    this.student = res.data;
                })
                .catch((error) => {
                    this.\$toast.error(Message.GET_STUDENT_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            },

            update() {
                var result = updateAddress(this.addressId, this.@slot{{module_name}});
                result.then((res) => {
                    console.log(res);
                    this.\$toast.success(Message.UPDATE_@slot{{MODULE_NAME}}_SUC);
                })
                .catch((error) => {
                    this.\$toast.error(Message.UPDATE_@slot{{MODULE_NAME}}_ERR + ' - ' + error.message);
                }).finally(() => {

                });
            }
        },
    };
</script>