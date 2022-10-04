<template src="./template/add.html"></template>
<script>
import Student from "../../model/student";
import StudentForm from "../../model/form/student";
import Message from "../../constants/message";
import { addStudent, updateStudent } from "../../api/student";
import { getAddress, getAllAddresses } from "../../api/address";
import {getStudentClass } from "../../api/student_class";

import Address from "../../model/address";

export default {
  props: {
    data: Object,
  },

  components: {
    "form-sub-module-enrolment": () => import("../enrolment/index.vue"),
    "form-sub-module-address": () => import("../address/add.vue"),
  },

  data() {
    return {
      // addresses: [],
      // studentClasses: [],
      // enrolmentIn: '',
      parentID: '',
      student: new Student(undefined, null, null, null, null, new Address()),
      form: new StudentForm(),
      formSubModuleAddressSeen: false,
      formSubModuleEnrolmentSeen: false,
      dataSubForm: {
        mode: "create",
        // address: null,
        parent: "student",
        // student: null,
        // enrolmentIn:'',
        parentID: ''
      }

    };
  },

  mounted() {
    // this.getAddresses();
    // this.getStudentClasses();
    // this.setFrom();

    if (this.data.mode === "edit") {
      this.student = this.data.student;
      this.dataSubForm.mode = "edit";
      this.dataSubForm.student = this.data.student;
      this.dataSubForm.address = this.data.student.address;
      // this.dataSubForm.enrolmentIn = this.data.student.name;
      this.dataSubForm.parentID = this.data.student.id;
    }
   
  },

  methods: {
    setFrom() {
      if (this.data.parent !== undefined && this.data.parent === "address") {
        this.form.setHidAddress(false);
      }
    },

    getAddresses() {
      var result = getAllAddresses();
      result
        .then((response) => {
          this.addresses = response.data;
        })
        .catch((e) => {
          this.$toast.error(Message.GET_LIST_ADDRESS_ERR + " - " + e.message);
        });
    },

    getAddressById(event) {
      let addressId = event.target.value;

      var result = getAddress(addressId);
      result
        .then((res) => {
          this.student.address = res.data;
          // this.dataSubForm.address = res.data;
        })
        .catch((error) => {
          // this.address = "";
          this.$toast.error(Message.GET_ADDRESS_ERR + " - " + error.message);
        })
        .finally(() => {});
    },

    // getStudentClasses() {
    //   var result = getAllStudentClasses();
    //   result
    //     .then((response) => {
    //       this.studentClasses = response.data;
    //       // console.log(response.data);
    //     })
    //     .catch((e) => {
    //       this.$toast.error(
    //         Message.GET_LIST_STUDENT_CLASS_ERR + " - " + e.message
    //       );
    //     });
    // },

    getStudentClassId(event) {

                let studentClassId = event.target.value;

                var result = getStudentClass(studentClassId);
                result
                  .then((res) => {
                    console.log(res.data.name);
                    this.student.studentClass.name = res.data.name;
                  })
                  .catch((error) => {

                    console.log(error);
                    
                  })
                  .finally(() => {});
    },

    create() {
      // this.studentClasses = null;
      //this.student.setAddress(this.data.address)
      // this.student.address.student = null;
      // console.log(this.student);
      var result = addStudent(this.student);
      result
        .then((res) => {
          console.log('Res' + res);
          this.$toast.success(Message.ADD_STUDENT_SUC);
        })
        .catch((e) => {
          this.$toast.error(Message.ADD_STUDENT_ERR + " - " + e.message);
        })
        .finally(() => {});
    },

    update() {

      console.log(this.student);

      var result = updateStudent(this.student.id, this.student);
      result
        .then((res) => {
          console.log('Res' + res);
          this.$toast.success(Message.UPDATE_STUDENT_SUC);
        })
        .catch((error) => {
          this.$toast.error(Message.UPDATE_STUDENT_ERR + " - " + error.message);
        })
        .finally(() => {});
    },

    onSubmit() {
        if (this.data.mode == "create") {
            this.create();
        } else {
            this.update();
        }
    },
  },
};
</script>
