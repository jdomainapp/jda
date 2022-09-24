import AddressForm from "./form/address"
import StudentForm from "./form/student"

export default class Receiver {
    constructor() {
        this.addressId = 0
        this.studentId = 0
        this.courseModuleId = 0
        this.enrolmentId = 0
        this.studentClassId = 0

        this.addressForm = new AddressForm()
        this.studentForm = new StudentForm()

        this.other = Object
    }
}