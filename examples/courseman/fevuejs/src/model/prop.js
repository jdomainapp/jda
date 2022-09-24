import FormAddress from "./form/address"
import FormStudent from "./form/student"

export default class Prop {
    constructor() {
        this.addressId = 0
        this.studentId = 0
        this.courseModuleId = 0
        this.enrolmentId = 0
        this.studentClassId = 0

        this.formAddress = new FormAddress()
        this.formStudent = new FormStudent()

        this.other = Object
    }

    setAddressId(id) {
        this.addressId = id
    }

    setStudentId(id) {
        this.studentId = id
    }

    setStudentClassId(id) {
        this.studentClassId = id
    }
}